package org.jglrxavpok.ld29.entity;

import org.jglrxavpok.ld29.SoundManager;
import org.jglrxavpok.ld29.level.EntitiesLayer;
import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;

public class EntityCoin extends Entity
{

	private double frame;

	public EntityCoin(Level lvl, int layerID)
	{
		super(lvl, layerID);
		this.gravityEfficiency = 0;
		this.decceleration = 0;
		w = 16;
		h = 16;
	}
	
	public void render()
	{
		Textures.bind("/assets/textures/entities/coin.png");
		double minU = 0;
		double minV = 1;
		double maxV = 0;
		double maxU = 0;
		
		maxU = ((int)(frame % 4.0))*16f/128f+16f/128f;
		minU = ((int)(frame % 4.0))*16f/128f;
		frame+=0.10;
		if(frame >= 16)
			frame = 0;
		
		Tessellator.instance.startDrawingQuads();
		int oldColor = Tessellator.instance.getColor();
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Tessellator.instance.addVertexWithUV(x+w/4, y+h/4, 0, minU, minV);
		Tessellator.instance.addVertexWithUV(x+w/4+w/2, y+h/4, 0, maxU, minV);
		Tessellator.instance.addVertexWithUV(x+w/4+w/2, y+h/2+h/4, 0, maxU, maxV);
		Tessellator.instance.addVertexWithUV(x+w/4, y+h/2+h/4, 0, minU, maxV);
		Tessellator.instance.setColor(oldColor);
		Tessellator.instance.flush();
	}
	
	public void update()
	{
		
	}
	
	public void onCollide(Entity e)
	{
		if(e instanceof EntityPlayer)
		{
			((EntityPlayer)e).coins++;
			((EntitiesLayer) getLevel().getLayer(getLayer())).removeEntity(this);
			SoundManager.instance.playSound("coin");
		}
	}

}
