package org.jglrxavpok.ld29.level;

import java.util.ArrayList;

import org.jglrxavpok.ld29.entity.Entity;

public class EntitiesLayer extends LevelLayer
{

	private ArrayList<Entity> entities = new ArrayList<Entity>();

	public EntitiesLayer(String layerName)
	{
		super(layerName);
	}
	
	public void addEntity(Entity e)
	{
		this.entities .add(e);
	}

	public void removeEntity(Entity e)
	{
		entities.remove(e);
	}
	
	public ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public Entity getEntityByName(String n)
	{
		for(Entity e : entities)
			if(e.getName().equals(n))
				return e;
		return null;
	}

	public Entity getClosestEntity(Entity entity, double maxDist)
	{
		Entity ent = null;
		double dist = maxDist*maxDist;
		for(Entity e : entities)
		{
			if(e.getDistanceSquared(entity) <= dist)
			{
				ent = e;
				dist = e.getDistanceSquared(entity);
			}
		}
		return ent;
	}
	
	public ArrayList<Entity> getClosestEntities(Entity entity, double maxDist)
	{
		ArrayList<Entity> ents = new ArrayList<Entity>();
		for(Entity e : entities)
		{
			if(e.getDistanceSquared(entity) <= maxDist*maxDist)
			{
				ents.add(e);
			}
		}
		
		return ents;
	}
}
