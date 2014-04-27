package org.jglrxavpok.ld29.render;

import org.lwjgl.opengl.GL11;

public class Camera2D
{
	
	private float x;
	private float y;
	
	public Camera2D()
	{
		
	}
	
	public void apply()
	{
		GL11.glTranslatef(x, y, 0);
	}
	
	public void unapply(){}
	
	public Camera2D setX(float x)
	{
		this.x = x;
		return this;
	}
	
	public Camera2D setY(float y)
	{
		this.y = y;
		return this;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
}
