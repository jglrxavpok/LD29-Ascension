package org.jglrxavpok.opengl;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

public final class Textures
{

	private static HashMap<BufferedImage, Integer>	imgs = new HashMap<BufferedImage, Integer>();

	public static int getFromClasspath(String classpath)
	{
		BufferedImage img = ImageUtils.getFromClasspath(classpath);
		return get(img);
	}

	public static int get(BufferedImage img)
	{
		if(!imgs.containsKey(img))
		{
			imgs.put(img, LWJGLHandler.loadTexture(img));
		}
		return imgs.get(img);
	}

	public static int getTextureHeight(int textID)
	{
		bind(textID);
		int h = GL11.glGetInteger(GL11.GL_TEXTURE_HEIGHT);
		return h;
	}

	public static int getTextureWidth(int textID)
	{
		bind(textID);
		int w = GL11.glGetInteger(GL11.GL_TEXTURE_WIDTH);
		return w;
	}
	
	public static void fillRectangle(float x, float y, float w, float h)
	{
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertex(x, y, 0);
		t.addVertex(x+w, y, 0);
		t.addVertex(x+w, y+h, 0);
		t.addVertex(x, y+h, 0);
		t.flush();
	}
	
	public static void render(final int ID, float x, float y, float w, float h)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.addVertexWithUV(x, y, 0, 0, 0);
		t.addVertexWithUV(x+w, y, 0, 1, 0);
		t.addVertexWithUV(x+w, y+h, 0, 1, 1);
		t.addVertexWithUV(x, y+h, 0, 0, 1);
		t.flush();
	}

	public static void bind(String string)
	{
		bind(getFromClasspath(string));
	}
	
	public static void bind(BufferedImage img)
	{
		bind(get(img));
	}
	
	public static void bind(int id)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
}
