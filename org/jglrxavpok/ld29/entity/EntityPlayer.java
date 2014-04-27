package org.jglrxavpok.ld29.entity;

import org.jglrxavpok.ld29.GameStart;
import org.jglrxavpok.ld29.SoundManager;
import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.ld29.render.EntityCamera;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;

public class EntityPlayer extends Entity
{

	private double frame;
	private int jumpCounter;
	private boolean controllable;
	public int coins;
	private boolean left;
	private boolean right;
	private boolean jump;
	private boolean deadAnimation;
	private int deadTime;
	private EntityCamera cam;

	public EntityPlayer(Level lvl, int layerID)
	{
		super(lvl, layerID);
		ableToClimb = true;
		controllable = true;
		cam = new EntityCamera(this);
		lvl.setCamera(cam);
	}
	
	public void render()
	{
		double minU = 0;
		double minV = 1;
		double maxU = 16f/64f;
		double maxV = 7f/8f;
		
		if(deadAnimation)
		{
			Textures.bind("/assets/textures/entities/player.png");	
			if(direction == LEFT)
			{
				maxV = 16f/128f;
				minV = 32f/128f;
			}
			else if(direction == RIGHT)
			{
				maxV = 0f/128f;
				minV = 16f/128f;
			}
			maxU = ((int)(frame/1.5 % 4.0))*16f/64f+16f/64f;
			minU = ((int)(frame/1.5 % 4.0))*16f/64f;
			frame+=0.15;
			cam.freeze();
			Tessellator.instance.startDrawingQuads();
			int oldColor = Tessellator.instance.getColor();
			Tessellator.instance.setColorOpaque(255, 255, 255);
			Tessellator.instance.addVertexWithUV(x, y, 0, minU, minV);
			Tessellator.instance.addVertexWithUV(x+w, y, 0, maxU, minV);
			Tessellator.instance.addVertexWithUV(x+w, y+h, 0, maxU, maxV);
			Tessellator.instance.addVertexWithUV(x, y+h, 0, minU, maxV);
			Tessellator.instance.setColor(oldColor);
			Tessellator.instance.flush();
			return;
		}
		if(!climbing)
		{
			Textures.bind("/assets/textures/entities/player.png");
			if(onGround || wasOnGround)
			{
				if(motionX > 0.5 || motionX < -0.5)
				{
					if(direction == LEFT)
						maxV = 112f/128f;
					else if(direction == RIGHT)
					{
						maxV = 96f/128f;
						minV = 112f/128f;
					}
					maxU = ((int)(frame % 4.0))*16f/64f+16f/64f;
					minU = ((int)(frame % 4.0))*16f/64f;
				}
				else
				{
					if(direction == LEFT)
					{
						maxV = 80f/128f;
						minV = 96f/128f;
					}
					else if(direction == RIGHT)
					{
						maxV = 64f/128f;
						minV = 80f/128f;
					}
					maxU = ((int)(frame/4.0 % 4.0))*16f/64f+16f/64f;
					minU = ((int)(frame/4.0 % 4.0))*16f/64f;
				}
			}
			else
			{
				if(motionY > 0.0f) // falling
				{
					if(direction == LEFT)
					{
						maxV = 16f/128f;
						minV = 32f/128f;
					}
					else if(direction == RIGHT)
					{
						maxV = 0f/128f;
						minV = 16f/128f;
					}
					maxU = ((int)(frame/1.5 % 4.0))*16f/64f+16f/64f;
					minU = ((int)(frame/1.5 % 4.0))*16f/64f;
				}
				else if(motionY < 0.0f) // jumping
				{
					if(direction == LEFT)
					{
						maxV = 48f/128f;
						minV = 64f/128f;
					}
					else if(direction == RIGHT)
					{
						maxV = 32f/128f;
						minV = 48f/128f;
					}
				}
				// TODO: Falling anim'
			}
			frame+=0.15;
		}
		else
		{
			Textures.bind("/assets/textures/entities/playerClimbing.png");
			minU = (float)(int)((((20f-climbingCounter)/20f)*7f))*16f/112f;
			maxU = (float)(int)(((20f-climbingCounter)/20f)*7f+1)*16f/112f;
			minV = direction == LEFT ? 1 : 0.5;
			maxV = direction == LEFT ? 0.5 : 0;
		}
		if(frame >= 16.0)
			frame = 0;
		Tessellator.instance.startDrawingQuads();
		int oldColor = Tessellator.instance.getColor();
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Tessellator.instance.addVertexWithUV(x, y, 0, minU, minV);
		Tessellator.instance.addVertexWithUV(x+w, y, 0, maxU, minV);
		Tessellator.instance.addVertexWithUV(x+w, y+h, 0, maxU, maxV);
		Tessellator.instance.addVertexWithUV(x, y+h, 0, minU, maxV);
		Tessellator.instance.setColor(oldColor);
		Tessellator.instance.flush();
	}

	public void update()
	{
		if(deadAnimation)
		{
			deadTime++;
			float speed = 8;
			if(deadTime <= 20)
			{
				y-=speed;
			}
			else
			{
				y+=speed;
			}
			if(y >= getLevel().getHeight()*16f)
			{
				GameStart.restartLevel();
			}
		}
		else
		{
			if(controllable)
			{
				boolean buttonUpdate = false;
				boolean noUpdate = true;
				boolean polled = false;
				boolean shouldReset = false;
				while (Controllers.next())
				{
				    Controller source = Controllers.getEventSource();
				    polled = true;
				    if (Controllers.isEventAxis())
				    {
				    	if(Controllers.getEventSource() != null)
				    	{
				    		System.out.println(Controllers.getEventSource().getAxisValue(3));
			    			if(Controllers.getEventSource().getAxisValue(3) < -0.2f)
			    			{
			    				left = true;
			    				right = false;
						    	noUpdate = false;
						    	shouldReset = false;
			    			}
			    			else if(Controllers.getEventSource().getAxisValue(3) > 0.2f)
			    			{
			    				left = false;
			    				right = true;
						    	noUpdate = false;
						    	shouldReset = false;
			    			}
			    			else
			    			{
			    				if(noUpdate)
			    				{
			    					shouldReset = true;
			    				}
			    			}
				    	}
				    }
				    else if (Controllers.isEventButton())
				    {
				    	if(Controllers.getEventSource().isButtonPressed(Controllers.getEventControlIndex()))
				    	{
					    	buttonUpdate = true;
					    	jump = true;
				    	}
				    	else
				    		buttonUpdate = false;
				    }
				    else if(Controllers.isEventPovX())
				    {
				    	float f = Controllers.getEventSource().getPovX();
				    	if(f < -0.2f)
				    	{
				    		left = true;
		    				right = false;
					    	noUpdate = false;
					    	shouldReset = false;
				    	}
				    	else if(f > 0.2f)
				    	{
				    		left = false;
		    				right = true;
					    	noUpdate = false;
					    	shouldReset = false;
				    	}
				    	else
				    	{
				    		if(noUpdate)
				    		{
					    		shouldReset = true;
				    		}
				    	}
				    }
				}
				if(Controllers.getControllerCount() == 0 || shouldReset)
				{
					left = false;
					right = false;
				}
				else if(!buttonUpdate && polled)
				{
					jump = false;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || left)
				{
					if(motionX > -30)
						motionX += -5;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || right)
				{
					if(motionX < 30)
						motionX += 5;
				}
				
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || jump)
				{
					jump();
				}
			}
			else
			{
				if(rand.nextInt(20) == 0)
					direction = rand.nextBoolean() ? LEFT : RIGHT;
				if(rand.nextInt(10) == 0)
					jump();
			}
			if(jumpCounter > 0)
				jumpCounter--;
			if(getLevel().hasTile((int)(x/16f),(int)(y/16f), 72) && controllable)
			{
				this.controllable = false;
				GameStart.prepareForNextLevel();
			}
			else if((getLevel().hasTile((int)((x+w/2)/16f),(int)((y+h/2)/16f), 9) || getLevel().hasTile((int)((x+w/2)/16f),(int)((y+h/2)/16f), 10) || getLevel().hasTile((int)((x+w/2)/16f),(int)((y+h/2)/16f), 19) || getLevel().hasTile((int)((x+w/2)/16f),(int)((y+h/2)/16f), 20)) && controllable)
			{
				deadAnimation = true;
			}
			super.update();
		}
	}

	private void jump()
	{
		if(jumpCounter > 0 || (!wasOnGround && !onGround))
			return;
		SoundManager.instance.playSound("jump");
		jumpCounter = 5;
		motionY = -9;
	}
}
