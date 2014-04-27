package org.jglrxavpok.ld29.entity;

import java.util.ArrayList;

import org.jglrxavpok.ld29.level.EntitiesLayer;
import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.ld29.utils.PipeNetwork;

/**
 * Scrapped
 * @author jglrxavpok
 *
 */
@Deprecated
public class EntityPipe extends Entity
{

	public static final int START = 0;
	public static final int MIDDLE = 1;
	public static final int END = 2;
	
	public int type;
	public PipeNetwork pipeNetwork;
	private ArrayList<EntityPipe> children;
	public EntityPipe parent;

	public EntityPipe(Level lvl, int layerID, int type)
	{
		super(lvl, layerID);
		this.type = type;
		this.gravityEfficiency = 0;
		this.decceleration = 0;
	}
	
	public void update()
	{
		EntitiesLayer layer = ((EntitiesLayer) getLevel().getLayer(getLayer()));
		if(pipeNetwork != null && pipeNetwork.invalid)
		{
			layer.removeEntity(this);
		}
		else if(pipeNetwork == null && type == START)
		{
			pipeNetwork = PipeNetwork.create(this);
			if(pipeNetwork == null)
				layer.removeEntity(this);
		}
		else if(pipeNetwork != null && type == START)
		{
			ArrayList<Entity> closestEnts = layer.getClosestEntities(this, 24);
			for(Entity closest : closestEnts)
				if(closest != null)
				{
					suckEntityInNetwork(closest);
				}
		}
		
		motionX = 0;
		motionY = 0;
	}
	
	private void suckEntityInNetwork(Entity closest)
	{
		if(!pipeNetwork.contains(closest))
		{
			if(type == START)
			{
				pipeNetwork.addEntity(closest);
			}
		}
	}

	public EntityPipe getRandomChild()
	{
		return children.get(rand.nextInt(children.size()));
	}

	public ArrayList<EntityPipe> getDirectChildren()
	{
		return children;
	}
	
	public void setDirectChildren(ArrayList<EntityPipe> children)
	{
		this.children = children;
	}
	
}
