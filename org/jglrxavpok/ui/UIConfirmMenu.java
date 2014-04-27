package org.jglrxavpok.ui;

import org.jglrxavpok.ui.UILabel.LabelAlignment;

public class UIConfirmMenu extends UIMenu
{

	private int	color;
	private String	text;
	private UIMenu	noMenu;
	private UIMenu	yesMenu;
	private UIButton	yesButton;
	private UIButton	noButton;

	public UIConfirmMenu(UIMenu yesMenu, UIMenu noMenu, String questionText, int questionColor)
	{
		this.yesMenu = yesMenu;
		this.noMenu = noMenu;
		this.text = questionText;
		this.color = questionColor;
	}
	
	public void initMenu()
	{
		UILabel label = new UILabel(text,w/2,h/2+50, LabelAlignment.CENTERED);
		comps.add(label);
		label.color = color;
		
		yesButton = new UIButton(this, w/2-360,h/2-60,350,40,"Yes");
		noButton = new UIButton(this, w/2+10,h/2-60,350,40,"No");
		
		comps.add(yesButton);
		comps.add(noButton);
	}

	public void componentClicked(UIComponentBase b)
	{
		if(b == yesButton)
		{
			UI.displayMenu(yesMenu);
		}
		else if(b == noButton)
		{
			UI.displayMenu(noMenu);
		}
	}
	
	public void renderOverlay(int mx, int my, boolean[] buttons)
	{
		super.renderOverlay(mx, my, buttons);
	}
	
	public void render(int mx, int my, boolean[] buttons)
	{
		super.render(mx, my, buttons);
	}
}
