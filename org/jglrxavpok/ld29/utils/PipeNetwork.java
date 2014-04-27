package org.jglrxavpok.ld29.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.jglrxavpok.ld29.entity.Entity;
import org.jglrxavpok.ld29.entity.EntityPipe;
import org.jglrxavpok.ld29.level.EntitiesLayer;
import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.ld29.math.AABB;

public class PipeNetwork
{

	public boolean invalid = false;
	private ArrayList<EntityPipe> pipesList;
	private ArrayList<Entity> ents = new ArrayList<Entity>();
	private EntityPipe start;
	private EntityPipe end;
	private HashMap<Entity, EntityPipe> ents2pipe = new HashMap<Entity, EntityPipe>();
	
	private static ArrayList<PipeNetwork> loadedNetworks = new ArrayList<PipeNetwork>();
	
	public PipeNetwork(EntityPipe start, EntityPipe end)
	{
		this.start = start;
		this.end = end;
		loadedNetworks.add(this);
		if(start.type != EntityPipe.START)
		{
			invalid = true;
		}
		else
			createNetwork(start, end);
	}
	
	private void createNetwork(EntityPipe start, EntityPipe end)
	{
		Level lvl = start.getLevel();
		EntitiesLayer layer = (EntitiesLayer) lvl.getLayer(start.getLayer());
		ArrayList<Entity> ents = layer.getEntities();
		ArrayList<EntityPipe> pipes = new ArrayList<EntityPipe>();
		for(Entity e : ents)
			if(e instanceof EntityPipe)
				pipes.add((EntityPipe)e);
		ArrayList<EntityPipe> opened = new ArrayList<EntityPipe>();
		ArrayList<EntityPipe> closed = new ArrayList<EntityPipe>();
		opened.add(start);
		EntityPipe last = start;
		while(!opened.isEmpty())
		{
			EntityPipe above = getClosestInDirection(opened.get(0), pipes, opened, closed, 0, 1);
			EntityPipe below = getClosestInDirection(opened.get(0), pipes, opened, closed, 1, 1);
			EntityPipe left = getClosestInDirection(opened.get(0), pipes, opened, closed, 2, 1);
			EntityPipe right = getClosestInDirection(opened.get(0), pipes, opened, closed, 3, 1);
			ArrayList<EntityPipe> children = last.getDirectChildren();
			if(children == null)
			{
				 children = new ArrayList<EntityPipe>();
				 for(EntityPipe child : children)
						child.pipeNetwork = this;
				 last.setDirectChildren(children);
			}
			if(above == end)
			{
				children.add(end);
				pipesList = reconstructPath(above);
				break;
			}
			else if(below == end)
			{
				children.add(end);
				pipesList = reconstructPath(below);
				break;
			}
			else if(left == end)
			{
				children.add(end);
				pipesList = reconstructPath(left);
				break;
			}
			else if(right == end)
			{
				children.add(end);
				pipesList = reconstructPath(right);
				break;
			}
			else
			{
				if(above != null)
				{
					above.parent = opened.get(0);
					children.add(above);
					opened.add(above);
				}
				if(below != null)
				{
					below.parent = opened.get(0);
					children.add(below);
					opened.add(below);
				}
				if(left != null)
				{
					left.parent = opened.get(0);
					children.add(left);
					opened.add(left);
				}
				if(right != null)
				{
					right.parent = opened.get(0);
					children.add(right);
					opened.add(right);
				}
				children.remove(opened.get(0));
				for(EntityPipe child : children)
					child.pipeNetwork = this;
				opened.get(0).setDirectChildren(children);
				closed.add(opened.get(0));
				last = opened.get(0);
				opened.remove(opened.get(0));
			}
		}
	}
	
	private ArrayList<EntityPipe> reconstructPath(EntityPipe above)
	{
		ArrayList<EntityPipe> path = new ArrayList<EntityPipe>();
		EntityPipe parent = above.parent;
		ArrayList<EntityPipe> children = new ArrayList<EntityPipe>();
		children.add(above);
		while(parent != null)
		{
			path.add(parent);
			children.add(parent);
			parent.pipeNetwork = this;
			parent = parent.parent;
			parent.setDirectChildren(children);
			children.clear();
		}
		return path;
	}

	/**
	 *	direction: 0 UP 1 DOWN 2 LEFT 3 RIGHT
	 */
	public EntityPipe getClosestInDirection(EntityPipe asker, ArrayList<EntityPipe> pipes, ArrayList<EntityPipe> opened, ArrayList<EntityPipe> closed, int direction, int maxDist)
	{
		AABB aabb = new AABB().set(asker.x, asker.y, asker.w, asker.h);
		int translateX = direction == 0 ? 0 : direction == 1 ? 0 : direction == 2 ? -1 : 1;
		int translateY = direction == 0 ? -1 : direction == 1 ? 1 : direction == 2 ? 0 : 0;
		int xo = 0;
		int yo = 0;
		for(int i = 0;i<=maxDist+1;i++)
		{
			xo=translateX*i;
			yo=translateY*i;
			aabb.set(asker.x+xo, asker.y+yo, asker.w, asker.h);
			for(EntityPipe pipe : pipes)
			{
				if(pipe != asker && pipe.getBoundingBox().collides(aabb) && !closed.contains(pipe) && !opened.contains(pipe))
				{
					return pipe;
				}
			}
		}
		return null;
	}

	public static PipeNetwork create(EntityPipe pipe)
	{
		PipeNetwork result = null;
		if(pipe.type != EntityPipe.START)
		{
			String s = pipe.getExtraProps().get("OtherSide");
			if(s != null)
			{
				Entity e = ((EntitiesLayer) pipe.getLevel().getLayer(pipe.getLayer())).getEntityByName(s);
				if(e instanceof EntityPipe)
				{
					EntityPipe startPipe = (EntityPipe)e;
					if(startPipe.type == EntityPipe.START)
					{
						s = startPipe.getExtraProps().get("OtherSide");
						if(s != null)
						{
							e = ((EntitiesLayer) pipe.getLevel().getLayer(pipe.getLayer())).getEntityByName(s);
							System.out.println(e);
							if(e instanceof EntityPipe)
							{
								EntityPipe end = (EntityPipe)e;
								if(end.type == EntityPipe.END)
									result = new PipeNetwork(startPipe, end);
							}
						}
					}
				}
			}
		}
		else
		{
			String s = pipe.getExtraProps().get("OtherSide");
			if(s != null)
			{
				Entity e = ((EntitiesLayer) pipe.getLevel().getLayer(pipe.getLayer())).getEntityByName(s);
				if(e instanceof EntityPipe)
				{
					if(((EntityPipe)e).type == EntityPipe.END)
					{
						result = new PipeNetwork(pipe, (EntityPipe) e);
					}
				}
			}
		}
		return result;
	}
	
	public void addEntity(Entity e)
	{
		if(e instanceof EntityPipe == false)
		ents.add(e);
	}
	
	public void removeEntity(Entity e)
	{
		ents.remove(e);
	}
	
	public ArrayList<Entity> getEntities()
	{
		return ents;
	}

	public boolean contains(Entity e)
	{
		return ents.contains(e);
	}
	
	public static void updateAll()
	{
		for(PipeNetwork network : loadedNetworks)
			network.update();
	}

	public void update()
	{
		for(int i = 0;i<ents.size();i++)
		{
			Entity e = ents.get(i);
			EntityPipe pipe = ents2pipe.get(e);
			if(pipe == null)
			{
				ents2pipe.put(e, start);
			}
			pipe = ents2pipe.get(e);
			if(pipe.rand.nextInt(50) == 0)
			{
				if(pipe.getDirectChildren() == null)
				{
					removeEntity(e);
					e.motionX = (e.rand.nextFloat()-0.5f)*5f;
					e.motionY = -8;
					e.x = end.x;
					e.y = end.y;
					continue;
				}
				EntityPipe child = pipe.getRandomChild();
				ents2pipe.put(e, child);
				if(child == null || child.type == EntityPipe.END)
				{
					System.out.println("hi");

					removeEntity(e);
				}
			}
			e.motionX = 0;
			e.motionY = 0;
			e.x = pipe.x;
			e.y = pipe.y;
		}
	}
}
