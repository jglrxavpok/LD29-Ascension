package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.TextFormatting;
import org.jglrxavpok.opengl.Textures;
import org.lwjgl.opengl.GL11;

public class UIErrorMenu extends UIMenu
{

	private UIMenu	parent;
	private Throwable	displayedError;
	private UIButton	mainMenuButton;
    private UIMenu mainMenu;

	public UIErrorMenu(UIMenu mainMenu, UIMenu parent, Throwable throwable)
	{
	    this.mainMenu = mainMenu;
		this.parent = parent;
		this.displayedError = throwable;
	}
	
	public void initMenu()
	{
		mainMenuButton = new UIButton(this, w/2f-75f,15,150,30,"Main Menu");
		comps.add(mainMenuButton);
	}
	
	public void componentClicked(UIComponentBase c)
	{
		if(c == mainMenuButton)
		{
			UI.displayMenu(mainMenu);
		}
	}
	
	public void renderOverlay(int mx, int my, boolean[] buttons)
	{
		super.renderOverlay(mx, my, buttons);
	}
	
	public void render(int mx, int my, boolean[] buttons)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1, 1, 1);
		Textures.fillRectangle(24, 16+40, w-24-5-24+1, 20*17+1);
		GL11.glColor3f(0, 0, 0);
		Textures.fillRectangle(25, 17+40, w-24-5-24-1, 20*17-1);
		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		super.render(mx, my, buttons);
		float y = h-80;
		String guiName = "A guy";
		if(parent != null)
		{
			guiName = TextFormatting.UNDERLINE+"Crash in "+parent.getClass().getName();
		}
		GL11.glPushMatrix();
		float factor = 2;
		GL11.glScaled(factor, factor, 1);
		FontRenderer.drawShadowedString(guiName, (this.w/2)/factor-FontRenderer.getWidth(guiName)/2f, (y-30f)/factor, 0xFF2222);
		GL11.glPopMatrix();
		if(displayedError != null)
		{
			String s = TextFormatting.UNDERLINE+displayedError.getClass().getCanonicalName();
			FontRenderer.drawShadowedString(s+(displayedError.getMessage() == null ? "":":"), 25, this.h/2+105, 0xFF2222);
			if(displayedError.getMessage() != null)
			FontRenderer.drawShadowedString(displayedError.getMessage(), 25+16+FontRenderer.getWidth(s), this.h/2+105, 0xFFCCCC);
			float stackY = 0;
			int i = 0;
			for(;i<15;i++)
			{
				if(i >= displayedError.getStackTrace().length)
				{
					break;
				}
				stackY += 20;
				StackTraceElement elem = displayedError.getStackTrace()[i];
				String sourceString = "";
				if(elem.getFileName() == null)
				{
					sourceString += "?";
				}
				else
				{
					sourceString += elem.getFileName();
				}
				sourceString+=":";
				if(elem.getLineNumber() < 0)
				{
					sourceString += "?";
				}
				else
				{
					sourceString += elem.getLineNumber();
				}
				FontRenderer.drawShadowedString("at "+elem.getClassName()+"."+elem.getMethodName()+"("+sourceString+")", 130, this.h/2+105-stackY, 0xFFCCCC);
			}
			if(i < displayedError.getStackTrace().length)
			{
				FontRenderer.drawShadowedString((displayedError.getStackTrace().length-i)+" more...", 130, this.h/2+105-stackY-20, 0xFFCCCC);
			}
		}
	}
}
