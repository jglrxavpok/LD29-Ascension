package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.ImageUtils;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;

public class UIRadioButton extends UIButton
{

	public boolean	selected;
	public RadioButtonGroup group = null;

	public UIRadioButton(ActionListener listener, float x, float y, String text)
	{
		super(listener, x, y, 16,16, text);
	}
	
	public void onMouseEvent(int mx, int my, int buttonPressed, boolean released)
	{
		if(buttonPressed == 0 && released && isMouseOver(mx,my))
		{
			selected = !selected;
			if(group != null && selected)
				group.handleSelected(this);
			listener.componentClicked(this);
		}
	}
	
	public void update(int mx, int my, boolean[] buttonsPressed)
	{
		this.w = 16+FontRenderer.getWidth(displayString);
	}
	
	public void render(int mx, int my, boolean[] buttonsPressed)
	{
		float type = 0;
		float type2 = 0;
		boolean mouseOver = this.isMouseOver(mx, my);
//		if(enabled)
		{
			type2 = mouseOver ? 1 : 0;
			type = selected ? 0 : 1;
		}
		Tessellator t = Tessellator.instance;
		float minU = (30f+16f*type2)/200f;
		float minV = (58f+16f*type)/150f;
		float maxU = (30f+16f+16f*type2)/200f;
		float maxV = (58f+16f*type+16)/150f;
		Textures.bind(UI.TEMPLATE_TEXID);
		t.startDrawingQuads();
		t.addVertexWithUV(x, y, 0, minU, minV);
		t.addVertexWithUV(x+16, y, 0, maxU, minV);
		t.addVertexWithUV(x+16, y+16, 0, maxU, maxV);
		t.addVertexWithUV(x, y+16, 0, minU, maxV);
		t.flush();
		if(displayString == null)
		{
			
		}
		else
		{
			FontRenderer.drawString(displayString, x+16+1+1, y+h/2-8-1, 0);
			FontRenderer.drawString(displayString, x+16+1, y+h/2-8, enabled ? mouseOver ? 0xFAFA2A : 0xFFFFFF : 0xC0C0C0);
		}
		
		if(mouseOver && tooltipText != null)
		{
			tooltipCounter++;
		}
		else
		{
			tooltipCounter = 0;
		}
	}

}
