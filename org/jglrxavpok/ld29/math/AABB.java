package org.jglrxavpok.ld29.math;

public class AABB
{

	private float x;
	private float y;
	private float w;
	private float h;

	public AABB set(float x, float y, float w, float h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		return this;
	}
	
	public boolean collides(AABB aabb)
	{
		if(aabb.x >= x+w
		|| x >= aabb.x+w
		|| aabb.y >= y+h
		|| y >= aabb.y+h)
			return false;
		return true;
	}

}
