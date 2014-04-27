package org.jglrxavpok.ld29.render;

import org.jglrxavpok.ld29.GameStart;
import org.jglrxavpok.ld29.entity.Entity;

public class EntityCamera extends Camera2D
{

	private Entity ent;
	private boolean freeze;

	public EntityCamera(Entity e)
	{
		this.ent = e;
	}
	
	public void apply()
	{
		if(!freeze)
		{
			setX((float)GameStart.WIDTH/(float)GameStart.ZOOM/2f-(ent.x + ent.w /2f)).
			setY((float)GameStart.HEIGHT/(float)GameStart.ZOOM/2f-(ent.y + ent.h /2f));
		}
		super.apply();
	}

	public void freeze()
	{
		freeze = true;
	}

	public void unfreeze()
	{
		freeze = false;
	}
}
