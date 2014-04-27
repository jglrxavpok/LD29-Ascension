package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;

public class UIButton extends UIComponentBase
{

	public String	displayString;
	public ActionListener	listener;
	protected String	tooltipText;
	protected int	tooltipCounter;
	public static int textureID = -1;

	public UIButton(ActionListener listener, float x, float y, float w, float h, String text)
	{
		this.listener = listener;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		displayString = text;
	}
	
	public void setTooltipText(String txt)
	{
		if(txt == null || txt.trim().equals(""))
			tooltipText = null;
		tooltipText = txt;
	}
	
	public String getTooltipText()
	{
		return tooltipText;
	}
	
	public void onMouseEvent(int mx, int my, int buttonPressed, boolean released)
	{
		if(buttonPressed == 0 && released && isMouseOver(mx,my))
		{
			listener.componentClicked(this);
		}
	}
	
	public void update(int mx, int my, boolean[] buttonsPressed)
	{
		
	}
	
	public void render(int mx, int my, boolean[] buttonsPressed)
	{
		float type = 0;
		boolean mouseOver = this.isMouseOver(mx, my) || isSelected();
		if(enabled)
		{
			type = mouseOver ? 2 : 1;
		}
		Textures.bind(UI.TEMPLATE_TEXID);
		Tessellator t = Tessellator.instance;
		
		float minU = 0f;
		float maxU = 10f/UI.TEMPLATE_WIDTH;
		float minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-10f)/UI.TEMPLATE_HEIGHT;
		float maxV = (UI.TEMPLATE_HEIGHT-type*20f-10f)/UI.TEMPLATE_HEIGHT;
		t.startDrawingQuads();
		
		/**
		 * Fill center
		 */
		{
		    float widthToFill = (x+w-10f)-(x+10f);
		    float heightToFill = (y+h-10f)-(y+10f);
		    float xNbr = widthToFill/10f;
		    float yNbr = heightToFill/10f;
            minU = 40f/UI.TEMPLATE_WIDTH;
            maxU = 50f/UI.TEMPLATE_WIDTH;
            minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-10f)/UI.TEMPLATE_HEIGHT;
            maxV = (UI.TEMPLATE_HEIGHT-type*20f-10f)/UI.TEMPLATE_HEIGHT;
            for(int i = 0;i<xNbr;i++)
            {
                for(int ii = 0;ii<yNbr;ii++)
                {
                    t.addVertexWithUV(x+10f+i*10f, y+10f+ii*10f, 0, minU, minV);
                    t.addVertexWithUV(x+20f+i*10f, y+10f+ii*10f, 0, maxU, minV);
                    t.addVertexWithUV(x+20f+i*10f, y+20f+ii*10f, 0, maxU, maxV);
                    t.addVertexWithUV(x+10f+i*10f, y+20f+ii*10f, 0, minU, maxV);
                }   
            }
		}
		/**
         *  Fill borders
         */
        {
            float widthToFill = (x+w-10f)-(x+10f);
            if(widthToFill > 0)
            {
                float xNbr = widthToFill/10f;
                for(int i = 0;i<(int)xNbr;i++)
                {
                    minU = 20f/UI.TEMPLATE_WIDTH;
                    maxU = 30f/UI.TEMPLATE_WIDTH;
                    minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-20f)/UI.TEMPLATE_HEIGHT;
                    maxV = (UI.TEMPLATE_HEIGHT-type*20f-20f)/UI.TEMPLATE_HEIGHT;
                    t.addVertexWithUV(x+10f+i*10f, y+h-10f, 0, minU, minV);
                    t.addVertexWithUV(x+20f+i*10f, y+h-10f, 0, maxU, minV);
                    t.addVertexWithUV(x+20f+i*10f, y+h, 0, maxU, maxV);
                    t.addVertexWithUV(x+10f+i*10f, y+h, 0, minU, maxV);

                    
                    minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-10f)/UI.TEMPLATE_HEIGHT;
                    maxV = (UI.TEMPLATE_HEIGHT-type*20f-10f)/UI.TEMPLATE_HEIGHT;
                    t.addVertexWithUV(x+10f+i*10f, y, 0, minU, minV);
                    t.addVertexWithUV(x+20f+i*10f, y, 0, maxU, minV);
                    t.addVertexWithUV(x+20f+i*10f, y+10f, 0, maxU, maxV);
                    t.addVertexWithUV(x+10f+i*10f, y+10f, 0, minU, maxV);

                }
            }
            
            float heightToFill = (y+h-10f)-(y+10f);
            if(heightToFill > 0)
            {
                float yNbr = heightToFill/10f;
                for(int i = 0;i<(int)yNbr;i++)
                {
                    minV = (UI.TEMPLATE_HEIGHT-type*20f-20f+20f)/UI.TEMPLATE_HEIGHT;
                    maxV = (UI.TEMPLATE_HEIGHT-type*20f-30f+20f)/UI.TEMPLATE_HEIGHT;
                    minU = 30f/UI.TEMPLATE_WIDTH;
                    maxU = 40f/UI.TEMPLATE_WIDTH;
                    t.addVertexWithUV(x, y+10f+i*10f, 0, minU, minV);
                    t.addVertexWithUV(x+10f, y+10f+i*10f, 0, maxU, minV);
                    t.addVertexWithUV(x+10f, y+20f+i*10f, 0, maxU, maxV);
                    t.addVertexWithUV(x, y+20f+i*10f, 0, minU, maxV);

                    minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-20f)/UI.TEMPLATE_HEIGHT;
                    maxV = (UI.TEMPLATE_HEIGHT-type*20f-20f)/UI.TEMPLATE_HEIGHT;
                    t.addVertexWithUV(x+w-10f, y+10f+i*10f, 0, minU, minV);
                    t.addVertexWithUV(x+w, y+10f+i*10f, 0, maxU, minV);
                    t.addVertexWithUV(x+w, y+20f+i*10f, 0, maxU, maxV);
                    t.addVertexWithUV(x+w-10f, y+20f+i*10f, 0, minU, maxV);

                    
//                    minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-10f)/UI.TEMPLATE_HEIGHT;
//                    maxV = (UI.TEMPLATE_HEIGHT-type*20f-10f)/UI.TEMPLATE_HEIGHT;
//                    t.addVertexWithUV(x+10f+i*10f, y, 0, minU, minV);
//                    t.addVertexWithUV(x+20f+i*10f, y, 0, maxU, minV);
//                    t.addVertexWithUV(x+20f+i*10f, y+10f, 0, maxU, maxV);
//                    t.addVertexWithUV(x+10f+i*10f, y+10f, 0, minU, maxV);

                }
            }
        }
        
		/**
         * Corners
         */
		{
		    minU = 0f;
	        maxU = 10f/UI.TEMPLATE_WIDTH;
		    minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-10f)/UI.TEMPLATE_HEIGHT;
		    maxV = (UI.TEMPLATE_HEIGHT-type*20f-10f)/UI.TEMPLATE_HEIGHT;
    		t.addVertexWithUV(x, y, 0, minU, minV);
    		t.addVertexWithUV(x+10f, y, 0, maxU, minV);
    		t.addVertexWithUV(x+10f, y+10f, 0, maxU, maxV);
    		t.addVertexWithUV(x, y+10f, 0, minU, maxV);
    		
    		minU = 10f/UI.TEMPLATE_WIDTH;
            maxU = 20f/UI.TEMPLATE_WIDTH;
            
    		t.addVertexWithUV(x+w-10f, y, 0, minU, minV);
            t.addVertexWithUV(x+w, y, 0, maxU, minV);
            t.addVertexWithUV(x+w, y+10f, 0, maxU, maxV);
            t.addVertexWithUV(x+w-10f, y+10f, 0, minU, maxV);
            
            minV = (UI.TEMPLATE_HEIGHT-type*20f+10f-20f)/UI.TEMPLATE_HEIGHT;
            maxV = (UI.TEMPLATE_HEIGHT-type*20f-20f)/UI.TEMPLATE_HEIGHT;
            t.addVertexWithUV(x+w-10f, y+h-10f, 0, minU, minV);
            t.addVertexWithUV(x+w, y+h-10f, 0, maxU, minV);
            t.addVertexWithUV(x+w, y+h, 0, maxU, maxV);
            t.addVertexWithUV(x+w-10f, y+h, 0, minU, maxV);
            
            minU = 0f;
            maxU = 10f/UI.TEMPLATE_WIDTH;
            t.addVertexWithUV(x, y+h-10f, 0, minU, minV);
            t.addVertexWithUV(x+10f, y+h-10f, 0, maxU, minV);
            t.addVertexWithUV(x+10f, y+h, 0, maxU, maxV);
            t.addVertexWithUV(x, y+h, 0, minU, maxV);
		}
		
//		t.addVertexWithUV(x, y,0,0f, (1f/(150f/(150f-20f*type-20f))));
//		t.addVertexWithUV(x+w, y, 0, 1f, (1f/(150f/(150f-20f*type-20f))));
//		t.addVertexWithUV(x+w, y+h, 0, 1f, (1f/(150f/(150f-20f*type-20f)))+1f/(150f/20f));
//		t.addVertexWithUV(x, y+h, 0, 0f, (1f/(150f/(150f-20f*type-20f)))+1f/(150f/20f));
		t.flush();
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
		
		
	}
	
	public void renderOverlay(int mx, int my, boolean[] buttonsPressed)
	{
		if(tooltipCounter >= 40)
		{
			Tessellator t = Tessellator.instance;
			int color = 0xC0C0C0;
			float r = ((color >> 16) & 0xFF)/255f;
			float g = ((color >> 8) & 0xFF)/255f;
			float b = ((color >> 0) & 0xFF)/255f;
			mx+=5;
			t.startDrawingQuads();
			t.setColorRGBA_F(r, g, b, 1);
			t.addVertex(mx-5, my,0);
			t.addVertex(mx+(tooltipText.length()*8f)+5, my,0);
			t.addVertex(mx+(tooltipText.length()*8f)+5, my+20,0);
			t.addVertex(mx-5, my+20,0);
			t.flush();
			t.startDrawingQuads();
			t.setColorOpaque_I(0);
			t.addVertex(mx+1-5, my+1,0);
			t.addVertex(mx+(tooltipText.length()*8f)-1+5, my+1,0);
			t.addVertex(mx+(tooltipText.length()*8f)-1+5, my+20-1,0);
			t.addVertex(mx+1-5, my+20-1,0);
			t.flush();
			t.setColorOpaque_F(1, 1,1);
			FontRenderer.drawString(tooltipText, mx+1, my-1+2, 0x404040);
			FontRenderer.drawString(tooltipText, mx, my+2, 0xFFFFFF);
		}
	}
}
