package org.jglrxavpok.ld29;

import org.jglrxavpok.ld29.entity.Entity;
import org.jglrxavpok.ld29.entity.EntityPlayer;
import org.jglrxavpok.ld29.level.Level;
import org.jglrxavpok.ld29.render.Camera2D;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;

public class PreparingLevelCamera extends Camera2D
{

	private Level lvl;
	private EntityPlayer ent;
	private double frame;

	public PreparingLevelCamera(Level lvl)
	{
		this.lvl = lvl;
		ent = lvl.getFirstPlayer();
	}
	
	public void apply()
	{
		float t = ((float)GameStart.preparingWait/(float)GameStart.preparingMaxWait);
		float targetY = (float)GameStart.HEIGHT/(float)GameStart.ZOOM/2f-(ent.y + ent.h /2);
		float y = (1f-t)*targetY;
		setX((float)GameStart.WIDTH/(float)GameStart.ZOOM/2f-(ent.x + ent.w /2)).
		setY(y);
		super.apply();
	}
	
	public void unapply()
	{
		float t = ((float)GameStart.preparingWait/(float)GameStart.preparingMaxWait);
		float targetY = (float)GameStart.HEIGHT/(float)GameStart.ZOOM/2f-(ent.y + ent.h /2);
		float y = GameStart.HEIGHT/GameStart.ZOOM/2f-(1f-t)*targetY;
		double minU = 0;
		double minV = 1;
		double maxU = 16f/64f;
		double maxV = 7f/8f;
		Textures.bind("/assets/textures/entities/player.png");
		if(ent.direction == Entity.LEFT)
		{
			maxV = 16f/128f;
			minV = 32f/128f;
		}
		else if(ent.direction == Entity.RIGHT)
		{
			maxV = 0f/128f;
			minV = 16f/128f;
		}
		maxU = ((int)(frame/1.5 % 4.0))*16f/64f+16f/64f;
		minU = ((int)(frame/1.5 % 4.0))*16f/64f;
		frame+=0.15;
		if(frame >= 16.0)
			frame = 0;
		Tessellator.instance.startDrawingQuads();
		int oldColor = Tessellator.instance.getColor();
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Tessellator.instance.addVertexWithUV(ent.x, y, 0, minU, minV);
		Tessellator.instance.addVertexWithUV(ent.x+ent.w, y, 0, maxU, minV);
		Tessellator.instance.addVertexWithUV(ent.x+ent.w, y+ent.h, 0, maxU, maxV);
		Tessellator.instance.addVertexWithUV(ent.x, y+ent.h, 0, minU, maxV);
		Tessellator.instance.setColor(oldColor);
		Tessellator.instance.flush();
	}
}
