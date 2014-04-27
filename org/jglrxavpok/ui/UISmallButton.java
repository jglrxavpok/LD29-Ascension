package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.ImageUtils;
import org.jglrxavpok.opengl.LWJGLHandler;
import org.jglrxavpok.opengl.Textures;
import org.lwjgl.opengl.GL11;

public class UISmallButton extends UIButton
{

	public UISmallButton(ActionListener listener, float x, float y, float w, float h, String text)
	{
		super(listener, x, y, w, h, text);
	}

	public void render(int mx, int my, boolean[] buttonsPressed)
	{
		float type = 2;
		boolean mouseOver = this.isMouseOver(mx, my);
		if(enabled)
		{
			type = mouseOver ? 0 : 1;
		}
		Textures.bind(UI.TEMPLATE_TEXID);		
		GL11.glBegin(GL11.GL_QUADS);
		float v = (1f/(150f/20f))*type;
		float v2 = (1f/(150f/20f))*type+1f/(150f/30f);
		if(type == 0)
		{
			v = 0;
			v2 = 1f/(150f/30f);
		}
		else if(type == 1)
		{
			v = 1f/(200f/40f);
			v2 = v+1f/(150f/30f);
		}
		else if(type == 2)
		{
			v = 1f/(150f/60);
			v2 = v+1f/(150f/30f);
		}
		GL11.glTexCoord2d(0f, v);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2d(1f/(200f/30f), v);
		GL11.glVertex2f(x+w, y);
		GL11.glTexCoord2d(1f/(200f/30f), v2);
		GL11.glVertex2f(x+w, y+h);
		GL11.glTexCoord2d(0f, v2);
		GL11.glVertex2f(x, y+h);
		GL11.glEnd();
		if(displayString == null)
		{
			
		}
		else
		{
			FontRenderer.drawString(displayString, x+w/2-(displayString.length()*8f)/2f+1, y+h/2-8-1, 0);
			FontRenderer.drawString(displayString, x+w/2-(displayString.length()*8f)/2f, y+h/2-8, enabled ? mouseOver ? 0xFAFA2A : 0xFFFFFF : 0xC0C0C0);
		}
		
		if(mouseOver && tooltipText != null)
		{
			tooltipCounter++;
		}
		else
		{
			tooltipCounter = 0;
		}
		
//		if(tooltipCounter >= 40)
//		{
//			GL11.glDisable(GL11.GL_TEXTURE_2D);
//			int color = 0xC0C0C0;
//			float r = ((color >> 16) & 0xFF)/255f;
//			float g = ((color >> 8) & 0xFF)/255f;
//			float b = ((color >> 0) & 0xFF)/255f;
//			mx+=5;
//			GL11.glColor3f(r, g, b);
//			GL11.glBegin(GL11.GL_QUADS);
//			GL11.glVertex2f(mx-5, my);
//			GL11.glVertex2f(mx+(tooltipText.length()*8f)+5, my);
//			GL11.glVertex2f(mx+(tooltipText.length()*8f)+5, my+20);
//			GL11.glVertex2f(mx-5, my+20);
//			GL11.glEnd();
//			GL11.glColor3f(0,0,0);
//			GL11.glBegin(GL11.GL_QUADS);
//			GL11.glVertex2f(mx+1-5, my+1);
//			GL11.glVertex2f(mx+(tooltipText.length()*8f)-1+5, my+1);
//			GL11.glVertex2f(mx+(tooltipText.length()*8f)-1+5, my+20-1);
//			GL11.glVertex2f(mx+1-5, my+20-1);
//			GL11.glEnd();
//			GL11.glEnable(GL11.GL_TEXTURE_2D);
//			GL11.glColor3f(1, 1, 1);
//			FontRenderer.drawString(tooltipText, mx+1, my-1+2, 0x404040);
//			FontRenderer.drawString(tooltipText, mx, my+2, 0xFFFFFF);
//		}
	}
}
