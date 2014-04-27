package org.jglrxavpok.ui;

import java.util.ArrayList;

public class UIMenu extends UIComponentBase implements ActionListener
{

	public ArrayList<UIComponentBase> comps = new ArrayList<UIComponentBase>();
	
	public UIMenu()
	{
		x = 0;
		y = 0;
		w = UI.getWidth();
		h = UI.getHeight();
	}
	
	public void initMenu(){}
	
	public void update(int mx, int my, boolean[] buttonsPressed)
	{
		for(UIComponentBase comp : comps)
		{
			if(comp.visible)
				comp.update(mx, my, buttonsPressed);
		}
	}
	
	public void renderBackground(int mx, int my, boolean[] buttonsPressed)
    {
        for(UIComponentBase comp : comps)
        {
            if(comp.visible)
                comp.renderBackground(mx, my, buttonsPressed);
        }
    }
	
	public void render(int mx, int my, boolean[] buttonsPressed)
	{
		for(UIComponentBase comp : comps)
		{
			if(comp.visible)
				comp.render(mx, my, buttonsPressed);
		}
	}
	
	public void onKeyEvent(char c, int key, boolean releasedOrPressed)
	{
		for(UIComponentBase comp : comps)
		{
			if(comp.enabled && comp.visible)
				comp.onKeyEvent(c, key, releasedOrPressed);
		}
	}
	
	public void onMouseEvent(int mx, int my, int buttonPressed, boolean released)
	{
		for(UIComponentBase comp : comps)
		{
			if(comp.enabled && comp.visible)
				comp.onMouseEvent(mx, my, buttonPressed, released);
		}
	}

	public void renderOverlay(int mx, int my, boolean[] buttonsPressed)
	{
		for(UIComponentBase comp : comps)
		{
			if(comp.visible)
				comp.renderOverlay(mx, my, buttonsPressed);
		}
	}

	@Override
	public void componentClicked(UIComponentBase clicked)
	{
		
	}

    public boolean doesPauseGame()
    {
        return true;
    }

    public void onMenuClose(){}
}
