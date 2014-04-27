package org.jglrxavpok.ui;

import java.util.ArrayList;

public class RadioButtonGroup
{

	private ArrayList<UIRadioButton>	buttons = new ArrayList<UIRadioButton>();

	public void addButton(UIRadioButton button)
	{
		buttons.add(button);
		button.group = this;
	}
	
	public void handleSelected(UIRadioButton button)
	{
		if(buttons.contains(button))
		{
			for(int i = 0;i<buttons.size();i++)
			{
				UIRadioButton b = buttons.get(i);
				if(b != button)
					b.selected = false;
			}
		}
	}
}
