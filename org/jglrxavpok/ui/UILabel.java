package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.FontRenderer;

public class UILabel extends UIComponentBase
{

	public static enum LabelAlignment
	{
		CENTERED, LEFT, RIGHT;
	}

	public String	displayString;
	public int	color;
	public LabelAlignment	alignment;

	public UILabel(String txt, float x, float y)
	{
		this(txt, x, y, LabelAlignment.LEFT);
	}
	
	public UILabel(String txt, float x, float y, LabelAlignment a)
	{
		alignment = a;
		this.x = x;
		this.y = y;
		displayString = txt;
	}
	
	public void render(int mx, int my, boolean[] buttons)
	{
		float tx = x;
		float ty = y;
		if(alignment == LabelAlignment.CENTERED)
		{
			tx-=FontRenderer.getWidth(displayString)/2f;
		}
		else if(alignment == LabelAlignment.RIGHT)
		{
			tx-=FontRenderer.getWidth(displayString);
		}
		FontRenderer.drawString(displayString, tx+1, ty-1, 0);
		FontRenderer.drawString(displayString, tx, ty, color);
	}

	public UILabel setColor(int i)
	{
		color = i;
		return this;
	}
}
