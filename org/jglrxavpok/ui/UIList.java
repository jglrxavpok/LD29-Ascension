package org.jglrxavpok.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class UIList extends UIComponentBase
{

	public UISlot[] slots;
	public float amountScrolled = Float.POSITIVE_INFINITY;
	public int slotsVisible = -1;
	private boolean	mouseInScrollBar;
	private boolean	showBarOnlyIfNecessary;
	private int	selectedIndex = -1;
	private float	totalHeight;
	
	public UIList(float x, float y, float w, float h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h= h;
	}
	
	public boolean getAlwaysShowScrollBar()
	{
		return !showBarOnlyIfNecessary;
	}
	
	public void setAlwaysShowScrollBar(boolean b)
	{
		showBarOnlyIfNecessary = !b;
	}
	
	public void update(int mx, int my, boolean buttons[])
	{
		super.update(mx, my, buttons);
		if(slots != null)
		{
		    slotsVisible = 0;
			totalHeight = 0;
			for(int i = 0;i<slots.length;i++)
			{
			    if(slots[i] != null && slots[i].visible)
			    {
			        slotsVisible++;
			        totalHeight+=slots[i].h;
			    }
			}
			if(buttons[0])
				if((mx > x+550 && mx < x+550+4 && my > y-30 && my < y+h+30) || mouseInScrollBar)
				{
					amountScrolled = ((float)(my-y)/h)*(totalHeight-h);
					mouseInScrollBar = true;
				}
				else;
				else if(mouseInScrollBar)
					mouseInScrollBar = false;
			if(amountScrolled < 0)
			{
				amountScrolled = 0;
			}
			else if(amountScrolled > totalHeight-h/*(((slotsVisible*sh))*((nbr*sh)))*/)
			{
				float value = totalHeight-h/*((slotsVisible*sh))*((nbr*sh))*/;
				amountScrolled = value > 0f ? value : 0;
			}
		}
		if(slots != null)
		{
			int slotIndex = 0;
			float slotX = x;
			float slotY = y-amountScrolled;
			for(int i = slots.length-1;i>=0;i--)
			{
				UISlot slot = slots[i];
				if(slot == null)
					continue;
				if(slotY < y+h && slotY+slot.h > y)
				slot.updateSlot(slotIndex, slotX, slotY, mx, my, buttons, selectedIndex == i);
				slotIndex++;
				slotY+=slot.h-2;
			}
		}
	}
	
	public void onMouseEvent(int mx, int my, int buttonPressed, boolean released)
	{
		if(buttonPressed == -1)
		{
			amountScrolled+=Mouse.getEventDWheel()/10;
		}
		if(slots != null)
		{
			int slotIndex = 0;
			float slotX = x;
			float slotY = y-amountScrolled;
			for(int i = slots.length-1;i>=0;i--)
			{
				UISlot slot = slots[i];
				if(slot == null)
					continue;
				if(slotY < y+h && slotY+slot.h > y)
				slot.onMouseEvent(slotIndex, slotX, slotY, mx, my, buttonPressed, released, selectedIndex == i);
				slotIndex++;
				slotY+=slot.h-2;
			}
		}
	}
	
	public void render(int mx, int my, boolean[] buttons)
	{
		float slotX = x;
		float slotY = y-amountScrolled;
		if(slots != null)
		{
			int slotIndex = 0;
			for(int i = slots.length-1;i>=0;i--)
			{
				UISlot slot = slots[i];
				if(slot == null)
					continue;
				if(slotY < y+h && slotY+slot.h > y)
				slot.renderSlot(slotIndex, slotX, slotY, mx, my, buttons, selectedIndex == i);
				slotIndex++;
				slotY+=slot.h-2;
			}
			
			if(!(showBarOnlyIfNecessary && (!isScrollingAmountPossible(0.1f) || !isScrollingAmountPossible(0.0f))))
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glColor3f(0, 0, 0);
				GL11.glBegin(GL11.GL_QUADS);
				boolean flag  = false;
				if((int)amountScrolled <= 0)
				{
					flag = true;
				}
				GL11.glVertex2f(slotX+550, y);
				GL11.glVertex2f(slotX+550+4, y);
				GL11.glVertex2f(slotX+550+4, y+h);
				GL11.glVertex2f(slotX+550, y+h);
				
				float max = /*totalHeight-*/totalHeight;
				float val = (amountScrolled/max*(h));
				GL11.glColor3f(0.5f, 0.5f, 0.5f);
				GL11.glVertex2f(slotX+550, (y+val+60));
				GL11.glVertex2f(slotX+550+4, (y+val+60));
				GL11.glVertex2f(slotX+550+4, (y+val));
				GL11.glVertex2f(slotX+550, (y+val));
	
				GL11.glColor3f(0.75f, 0.75f, 0.75f);
				GL11.glVertex2f(slotX+550, (y+val+60));
				GL11.glVertex2f(slotX+550+3, (y+val+60));
				GL11.glVertex2f(slotX+550+3, (y+val));
				GL11.glVertex2f(slotX+550, (y+val));
				if(flag)
					amountScrolled = 0;
				GL11.glEnd();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
		}
		
		GL11.glColor3f(1, 1, 1);
	}
	
	public boolean isScrollingAmountPossible(float f)
	{
		if(amountScrolled < 0.f)
		{
			return false;
		}
		else if(amountScrolled > totalHeight-h)
		{
			return false;
		}
		return true;
	}

	public void renderOverlay(int mx, int my, boolean[] buttons)
	{
		super.renderOverlay(mx, my, buttons);
		float slotX = x;
		float slotY = y-amountScrolled;
		if(slots != null)
		{
			int slotIndex = 0;
			for(int i = slots.length-1;i>=0;i--)
			{
				UISlot slot = slots[i];
				if(slot == null)
					continue;
				if(slotY < y+h && slotY+slot.h > y)
				slot.renderSlotOverlay(slotIndex, slotX, slotY, mx, my, buttons, selectedIndex == i);
				slotIndex++;
				slotY+=slot.h-2;
			}
		}
	}

	public void setSelected(UISlot slot)
	{
		selectedIndex = this.getIndex(slot);
	}
	
	public void setSelectedIndex(int index)
	{
		selectedIndex = index;
	}
	
	public int getSelectedIndex()
	{
		return selectedIndex;
	}

	public UISlot getSelected()
	{
		if(selectedIndex < 0 || selectedIndex >= slots.length)
			return null;
		return slots[selectedIndex];
	}

	public int getIndex(UISlot s)
	{
		if(slots != null)
		{
			for(int i = 0;i<slots.length;i++)
			{
				if(slots[i] != null && slots[i].equals(s))
					return i;
			}
		}
		return -1;
	}
}
