package org.jglrxavpok.ui;

import java.util.ArrayList;

public class UIPopupMenu extends UIMenu
{

	private ArrayList<UIComponentBase>	c;

	public UIPopupMenu(float x, float y, UIComponentBase[] components)
	{
		c = new ArrayList<UIComponentBase>();
		for(int i = 0;i<components.length;i++)
			c.add(components[i]);
	}
	
	public void initMenu()
	{
		comps.addAll(c);
	}
}
