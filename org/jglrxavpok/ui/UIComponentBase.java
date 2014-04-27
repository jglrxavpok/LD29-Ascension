package org.jglrxavpok.ui;

public abstract class UIComponentBase
{

	public float x, y, w = 2, h = 2;
	public boolean enabled = true;
	public boolean visible = true;
    private boolean selected;
	
	public void update(int mx, int my, boolean[] buttonsPressed)
	{
		
	}
	
	public boolean isSelected()
	{
	    return selected;
	}
	
	public void setSelected(boolean s)
	{
	    selected = s;
	}
	
	public void renderBackground(int mx, int my, boolean[] buttonsPressed)
    {
        
    }
	
	public void render(int mx, int my, boolean[] buttonsPressed)
	{
		
	}
	
	public void onKeyEvent(char c, int key, boolean releasedOrPressed)
	{
		
	}
	
	/**
	 * 
	 * @param mx
	 * @param my
	 * @param buttonsPressed : buttonsPressed[0]=left; buttonsPressed[1]=wheel; buttonsPressed[2]=right 
	 */
	public void onMouseEvent(int mx, int my, int buttonPressed, boolean released)
	{
		
	}
	
	public boolean isMouseOver(float x, float y, float w, float h, int mx, int my)
	{
		return mx >= x && mx <= x+w && my >= y && my <= y+h;
	}
	
	public boolean isMouseOver(int mx, int my)
	{
		return isMouseOver(x,y,w,h,mx,my);
	}
	
	public void renderOverlay(int mx, int my, boolean[] buttonsPressed)
	{
		
	}
}
