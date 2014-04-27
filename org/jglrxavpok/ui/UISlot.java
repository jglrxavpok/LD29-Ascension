package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.Tessellator;
import org.lwjgl.opengl.GL11;

public class UISlot extends UIComponentBase
{

	private ActionListener	listener;

	public UISlot(ActionListener listener, float w, float h)
	{
		this.listener = listener;
		this.w = w;
		this.h = h;
	}
	
	public void updateSlot(int id, float slotX, float slotY, int mx, int my, boolean buttons[], boolean selected)
	{
		super.update(mx, my, buttons);
	}
	
	public void renderSlot(int id, float x, float y, int mx, int my, boolean[] buttons, boolean selected)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA_F(0, 0, 0,0.5f);
		t.addVertex(x+1, y+1,0);
		t.addVertex(x+w-1, y+1,0);
		t.addVertex(x+w-1, y+h-1,0);
		t.addVertex(x+1, y+h-1,0);
		t.flush();
		t.setColorRGBA_F(1,1,1,1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

//		t.setColorOpaque_F(1, 1, 1);
	}

	public void onMouseEvent(int slotIndex, float slotX, float slotY, int mx, int my, int buttonPressed, boolean released, boolean selected)
	{
		if(isMouseOver(slotX, slotY, w,h,mx,my) && buttonPressed == 0 && released)
		if(listener != null)
			listener.componentClicked(this);
	}

	public void renderSlotOverlay(int slotIndex, float slotX, float slotY, int mx, int my, boolean[] buttons, boolean selected)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		if(selected)
		{
			GL11.glColor3f(0.75f, 0.75f, 0.75f);
			GL11.glVertex2f(slotX, slotY);
			GL11.glVertex2f(slotX+w, slotY);
			
			GL11.glVertex2f(slotX, slotY+h);
			GL11.glVertex2f(slotX+w, slotY+h);
			
			GL11.glVertex2f(slotX, slotY);
			GL11.glVertex2f(slotX, slotY+h);
			
			GL11.glVertex2f(slotX+w, slotY);
			GL11.glVertex2f(slotX+w, slotY+h);
		}
		GL11.glEnd();
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
