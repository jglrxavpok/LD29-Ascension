package org.jglrxavpok.ld29.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.jglrxavpok.ld29.level.EntitiesLayer;
import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.ld29.math.AABB;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;
import org.lwjgl.util.vector.Vector2f;

public class Entity
{

	private Level lvl;
	public float x, y;
	public float prevX = x;
	public float prevY = y;
	public float w = 16, h = 16;
	public float motionX = 0;
	public float motionY = 0;
	private AABB boundingBox = new AABB();
	public boolean isDead;

	public float maxMotionX = 32;
	public float minMotionX = -31;
	public float maxMotionY = 32;
	public float minMotionY = -31;
	public float decceleration = 0.10f;
	public boolean onGround;
	public int direction = RIGHT;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	public boolean smoothProcessing = true;
	public double gravityEfficiency = 1;
	public Random rand = new Random();
	private int layerID;
	public boolean ableToClimb = false;
	public boolean climbing;
	public int climbingCounter;
	private float climbTargetX = Float.NaN;
	private float climbTargetY = Float.NaN;
	public boolean wasOnGround;
	private String name = "";
	private HashMap<String, String> extra;
	private ArrayList<Entity> collisionsAlreadyDone = new ArrayList<Entity>();

	public Entity(Level lvl, int layerID)
	{
		this.lvl = lvl;
		this.layerID = layerID;
	}

	public Level getLevel()
	{
		return lvl;
	}

	public int getLayer()
	{
		return layerID;
	}

	public void render()
	{
		Textures.bind(0);
		Tessellator.instance.startDrawingQuads();
		int oldColor = Tessellator.instance.getColor();
		Tessellator.instance.setColorOpaque(255, 0, 255);
		Tessellator.instance.addVertexWithUV(x, y, 0, 0, 0);
		Tessellator.instance.addVertexWithUV(x + w, y, 0, 1, 0);
		Tessellator.instance.addVertexWithUV(x + w, y + h, 0, 1, 1);
		Tessellator.instance.addVertexWithUV(x, y + h, 0, 0, 1);
		Tessellator.instance.setColor(oldColor);
		Tessellator.instance.flush();
	}

	public AABB getBoundingBox()
	{
		return boundingBox.set(x, y, w, h);
	}

	public void update()
	{
		wasOnGround = onGround;
		onGround = false;
		if (motionX < minMotionX)
		{
			motionX = minMotionX;
		}
		if (motionX > maxMotionX)
		{
			motionX = maxMotionX;
		}
		if (motionY < minMotionY)
		{
			motionY = minMotionY;
		}
		if (motionY > maxMotionY)
		{
			motionY = maxMotionY;
		}

		if (motionX < 0.0)
			direction = LEFT;
		if (motionX > 0.0)
			direction = RIGHT;
		float targetX = x + motionX;
		float targetY = y + motionY;
		boolean waitingForClimbingConfirmation = false;
		if (!climbing || Float.isNaN(climbTargetX) || Float.isNaN(climbTargetY))
		{
			x = getNewPositionPossible(x + motionX, y).x;
			if (Math.floor(x) == Math.floor(targetX))
			{
	
			}
			else
			{
				if (ableToClimb)
				{
					waitingForClimbingConfirmation = true;
				}
				motionX = 0;
			}
		}
		if (!climbing || Float.isNaN(climbTargetX) || Float.isNaN(climbTargetY))
		{
			y = getNewPositionPossible(x, y + motionY).y;
			if (Math.floor(y) == Math.floor(targetY))
			{
				if (Math.abs(motionY) <= 8.0 && ableToClimb
						&& waitingForClimbingConfirmation) // Falling
				{
					boolean canClimb = lvl.isSolid((int)((x+direction+(direction == RIGHT ? w-1 : -1))/16f), (int)((y+h)/16f), -1) && !lvl.isSolid((int)((x+direction+(direction == RIGHT ? w-1 : -1))/16f), (int)((y+h)/16f)-1, -1)
							&& !lvl.isSolid((int)((x)/16f), (int)((y+h)/16f)-1, -1) && !lvl.isSolid((int)((x)/16f), (int)((y)/16f), -1)
							&& !lvl.isSolid((int)((x)/16f), (int)((y+h)/16f), -1) && !lvl.isSolid((int)((x)/16f), (int)((y)/16f), -1);
					if(canClimb)
//						(!lvl.isSolid((int) ((prevX + w) / 16f + direction),
//								(int) ((prevY + h) / 16f), -1)
//								&& !lvl.isSolid((int) (prevX / 16f),
//										(int) ((prevY + h) / 16f) - 1, -1)
//								&& !lvl.isSolid((int) (prevX / 16f) + direction,
//										(int) ((prevY + h) / 16f) - 1, -1)
//								&& !lvl.isSolid((int) (prevX / 16f),
//										(int) ((prevY + h) / 16f) + 1, -1)
//								&& !lvl.isSolid((int) (prevX / 16f),
//										(int) ((prevY + h) / 16f), -1)
//								&& !lvl.isSolid((int) (prevX / 16f),
//										(int) ((prevY) / 16f), -1)
//								&& !lvl.isSolid((int) (prevX / 16f),
//										(int) ((prevY) / 16f), -1)
//								&& !lvl.isSolid((int) ((prevX+w) / 16f),
//										(int) ((prevY) / 16f), -1)
//								&& !lvl.isSolid((int) (prevX / 16f),
//										(int) ((prevY+h) / 16f), -1));
					{
						climbing = true;
						climbTargetX = (x / 16f + direction) * 16f-direction*6;
						climbTargetY = ((y) / 16f - 1) * 16f;
						climbingCounter = 20;
						x = (int) (x / 16f)*16f+direction*(w/2);
						y = (int) ((y) / 16f)*16f+h/2;
					}
				}
			}
			else
			{
				if (motionY >= 0.0f)
					onGround = true;
				motionY = 0.0f;
			}
		}
			
		if (!climbing || Float.isNaN(climbTargetX) || Float.isNaN(climbTargetY))
		{
			motionY += lvl.gravity * gravityEfficiency;
			prevX = x;
			prevY = y;

			climbTargetX = Float.NaN;
			climbTargetY = Float.NaN;
			climbing = false;
		}
		else
		{
			climbingCounter--;

			if (climbingCounter <= 0)
			{
				x = climbTargetX;
				y = climbTargetY;
				climbTargetX = Float.NaN;
				climbTargetY = Float.NaN;
				climbing = false;
				onGround = true;
				wasOnGround = true;
			}
			else
			{
				y-=(float)Math.round((((20f-(float)climbingCounter)/20f) * 1000f) / 1000f);
			}
			motionY = 0;
			motionX = 0;
		}

		motionX *= decceleration;
		
		ArrayList<Entity> ents = ((EntitiesLayer) lvl.getLayer(layerID)).getEntities();
		for(int i = 0;i<ents.size();i++)
		{
			Entity e = ents.get(i);
			if(e != this && e.getBoundingBox().collides(getBoundingBox()) && !collisionsAlreadyDone.contains(e))
			{
				e.collisionsAlreadyDone.add(this);
				e.onCollide(this);
				this.onCollide(e);
			}
		}
		collisionsAlreadyDone.clear();
	}

	public void onCollide(Entity e)
	{
		
	}

	public Vector2f getNewPositionPossible(float nx, float ny)
	{
		Vector2f last = new Vector2f();
		last.set(nx, ny);
		for (double t = 0; t <= 1.D; t += smoothProcessing ? 0.25D : 1D)
		{
			float xAtT = (float) Math.floor((1.0 - t) * x + t * nx);
			float yAtT = (float) Math.floor((1.0 - t) * y + t * ny);
			for (double w1 = 0; w1 < w; w1++)
			{
				for (double h1 = 0; h1 < h; h1++)
				{
					int tx = (int) Math.floor((xAtT + w1) / 16f);
					int ty = (int) Math.floor((yAtT + h1) / 16f);
					if (lvl.isSolid(tx, ty, -1))
					{
						return last;
					}
				}
			}
			last.set(xAtT, yAtT);
		}
		return last;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public String getName()
	{
		return name;
	}

	public void setExtraProps(HashMap<String, String> props)
	{
		this.extra = props;
	}
	
	public HashMap<String, String> getExtraProps()
	{
		return extra;
	}
	
	public static Entity createFromID(Level lvl, int layerID, int i)
	{
		if (i == 0)
		{
			return new EntityPlayer(lvl, layerID);
		}
		else if(i == 4)
			return new EntityCoin(lvl, layerID);
		/*else if (i == 1)
		{
			return new EntityPipe(lvl, layerID, EntityPipe.MIDDLE);
		}
		else if (i == 2)
		{
			return new EntityPipe(lvl, layerID, EntityPipe.START);
		}
		else if (i == 3)
		{
			return new EntityPipe(lvl, layerID, EntityPipe.END);
		}*/
		return null;
	}

	public double getDistanceSquared(Entity entity)
	{
		double dx = entity.x-x;
		double dy = entity.y-y;
		return dx*dx+dy*dy;
	}

}
