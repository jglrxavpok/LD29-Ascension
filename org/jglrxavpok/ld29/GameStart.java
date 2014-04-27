package org.jglrxavpok.ld29;

import java.awt.Canvas;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.ld29.render.Camera2D;
import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.IO;
import org.jglrxavpok.opengl.LWJGLHandler;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GameStart
{

	public static final int WIDTH = (int) (740f*(16f/9f));
	public static final int HEIGHT = 740;
	public static final int ZOOM = 2;
	private static File folder;
	public static boolean alive;
	private static JFrame mainFrame;
	public static Level lvl;
	private static boolean f11Pressed;
	private static boolean preparingNextLevel;
	static int preparingWait;
	static int preparingMaxWait = 90;
	
	private static int levelIndex = 0;
	private static String[] levels = new String[]
			{
		"1_1", "Climbing to success", "Still going up"
			};
	private static PreparingLevelCamera preparingCam;
	private static boolean win;
	private static long startTime;
	private static boolean drawStartText;
	private static long last;

	public static void main(String[] args)
	{
		try
		{
			LWJGLHandler.load(new File(getFolder(),"natives").getCanonicalPath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		mainFrame = new JFrame();
		mainFrame.setResizable(false);
		Canvas canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		mainFrame.add(canvas);
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setTitle("Ascension - LD29 - Beneath the surface");
		try
		{
			mainFrame.setIconImage(ImageIO.read(GameStart.class.getResourceAsStream("/assets/textures/icon.png")));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		mainFrame.setVisible(true);
		try
		{
			Display.setParent(canvas);
			Display.create();
			start();
		}
		catch(LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	private static void start()
	{
		alive = true;
		
		GL11.glOrtho(0.0, WIDTH/ZOOM,  HEIGHT/ZOOM,0,  -1000.0, 10000.0);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, WIDTH/ZOOM, HEIGHT/ZOOM, 0);
		SoundManager.instance.getClass();
		Display.setVSyncEnabled(true);
//		prepareForNextLevel();
		try
		{
			Keyboard.create();
			Controllers.create();
		}
		catch (LWJGLException e1)
		{
			e1.printStackTrace();
		}
		startTime = System.currentTimeMillis();
		while(alive)
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_F11) && !f11Pressed)
			{
				f11Pressed = true;
				try
				{
					Display.setFullscreen(!Display.isFullscreen());
				}
				catch (LWJGLException e)
				{
					e.printStackTrace();
				}
				mainFrame.requestFocusInWindow();
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_F11) && f11Pressed)
				f11Pressed = false;
			if(Display.isFullscreen() && !Display.isActive())
				try
				{
					Display.setFullscreen(false);
				}
				catch (LWJGLException e)
				{
					e.printStackTrace();
				}
			updateLoop();
			renderLoop();
			if(Display.isCloseRequested() || !mainFrame.isVisible())
				alive = false;
		}
		Keyboard.destroy();
		Controllers.destroy();
		Display.destroy();
		mainFrame.dispose();
		System.exit(0);
	}

	private static void nextLevel()
	{
		try
		{
			lvl = new Level(IO.readString(GameStart.class.getResourceAsStream("/assets/maps/"+levels[levelIndex++]+".json"), "UTF-8"));
		}
		catch (Exception e)
		{
			if(e instanceof ArrayIndexOutOfBoundsException)
			{
				win();
			}
			else
			{
				e.printStackTrace();
			}
		}
	}

	private static void win()
	{
		win = true;
	}

	private static void renderLoop()
	{
		if(Display.isFullscreen())
		{
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		else
		{
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();


		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_2D);

		// Transparent Textures
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
		
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        if(lvl != null)
        {
	        Camera2D lvlCamera = lvl.getCamera();
	        if(preparingNextLevel && !win)
			{
	        	lvl.setCamera(preparingCam);
	        	lvl.renderAndUpdateEntities = false;
			}
			lvl.render();
			if(preparingNextLevel && !win)
			{
	        	lvl.renderAndUpdateEntities = true;
	        	float a = (float)preparingWait/(float)preparingMaxWait+0.5f;
	        	if(a >= 1f)
	        		a = 2f-a;
	        	GL11.glColor4f(1, 1, 1, a);
	        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        	FontRenderer.setScale(2);
	        	String s = levels[levelIndex-1].replace("_", "-").toUpperCase();
	        	FontRenderer.drawString(s, WIDTH/ZOOM/2f-FontRenderer.getWidth(s)/2, HEIGHT/ZOOM/2F-50f, 0xFFFFFF | ((int)(a*255f) << 24));
	        	s = "Get ready to climb back up!";
	        	FontRenderer.drawString(s, WIDTH/ZOOM/2f-FontRenderer.getWidth(s)/2, HEIGHT/ZOOM/2F+50f, 0xFFFFFF | ((int)(a*255f) << 24));
	        	FontRenderer.setScale(1);
	        	GL11.glColor4f(1, 1, 1, 1);
	        	GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
			float x = 10;
			float y = 10;
			
			Textures.bind("/assets/textures/ui/coin.png");
			Tessellator.instance.startDrawingQuads();
			Tessellator.instance.addVertexWithUV(x, y, 0, 0, 1);
			Tessellator.instance.addVertexWithUV(x+16, y, 0, 1, 1);
			Tessellator.instance.addVertexWithUV(x+16, y+16, 0, 1, 0);
			Tessellator.instance.addVertexWithUV(x, y+16, 0, 0, 0);
			Tessellator.instance.flush();
			x+=16;
			int coinsNumber = lvl.getFirstPlayer().coins;
			FontRenderer.drawString("x"+coinsNumber, x, y-1, 0xFFFFFFFF);
			
			lvl.setCamera(lvlCamera);
        }
        else
        {
        	Textures.bind("/assets/textures/ui/menuBack.png");
			Tessellator.instance.startDrawingQuads();
			Tessellator.instance.addVertexWithUV(0, 0, 0, 0, 1);
			Tessellator.instance.addVertexWithUV(WIDTH/ZOOM, 0, 0, 1, 1);
			Tessellator.instance.addVertexWithUV(WIDTH/ZOOM, HEIGHT/ZOOM, 0, 1, 0);
			Tessellator.instance.addVertexWithUV(0, HEIGHT/ZOOM, 0, 0, 0);
			Tessellator.instance.flush();
			if(System.currentTimeMillis()-startTime >= 30*1000)
			{
				FontRenderer.setScale(0.5f);
				FontRenderer.drawShadowedString("This is Sparta!", 700/2, 430/2, 0xFF000000);
				FontRenderer.setScale(1f);
			}
			if(System.currentTimeMillis()-last >= 500)
			{
				drawStartText = !drawStartText;
				last = System.currentTimeMillis();
			}
			
			if(drawStartText)
			{
				String s = "Press [Enter/Return] or [Any-button-on-your-gamepad] to begin";
				FontRenderer.drawShadowedString(s, WIDTH/ZOOM/2-FontRenderer.getWidth(s)/2, HEIGHT/ZOOM/2+160, 0xFFFFFFFF);
			}
        }
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		Display.update();
		Display.sync(60);
	}

	private static void updateLoop()
	{
		if(lvl != null)
		{
			if(preparingNextLevel && !win)
			{
				lvl.renderAndUpdateEntities = false;
			}
			lvl.update();
			if(preparingNextLevel && !win)
			{
				lvl.renderAndUpdateEntities = true;
				preparingWait--;
				if(preparingWait <= 0)
				{
					preparingNextLevel = false;
				}
			}
		}
		else
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_RETURN))
			{
				prepareForNextLevel();
			}
			else
			{
				while (Controllers.next())
				{
				    if (Controllers.isEventButton())
				    {
				    	if(Controllers.getEventSource().isButtonPressed(Controllers.getEventControlIndex()))
				    	{
				    		prepareForNextLevel();
				    	}
				    }
				}
			}
		}
	}

	public static File getFolder()
	{
		if (folder == null)
		{
			String home = System.getenv("APPDATA") != null ? System.getenv("APPDATA") : System.getProperty("user.home");
			folder = new File(home, ".ld29_jglrxavpok");
			if (!folder.exists())
				folder.mkdirs();
		}
		return folder;
	}

	public static void prepareForNextLevel()
	{
		preparingNextLevel = true;
		nextLevel();
		preparingCam = new PreparingLevelCamera(lvl);
		preparingWait = preparingMaxWait;
	}

	public static void restartLevel()
	{
		preparingNextLevel = true;
		try
		{
			lvl = new Level(IO.readString(GameStart.class.getResourceAsStream("/assets/maps/"+levels[levelIndex-1]+".json"), "UTF-8"));
		}
		catch (Exception e)
		{
			if(e instanceof ArrayIndexOutOfBoundsException)
			{
				win();
			}
			else
			{
				e.printStackTrace();
			}
		}
		preparingCam = new PreparingLevelCamera(lvl);
		preparingWait = preparingMaxWait;
	}

}
