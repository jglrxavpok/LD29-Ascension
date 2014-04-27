package org.jglrxavpok.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.jglrxavpok.opengl.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class UITextField extends UIComponentBase
{

	private String	placeholder;
	private boolean	focused;
	private int index;
	private int	cursorTimer;
	private boolean	showCursor;
	private StringBuilder builder = new StringBuilder();
	private boolean	mousePressed;
	private int	selectionStartIndex;
	private int	selectionEndIndex;
	private boolean shiftPressed = false;
	private boolean	controlPressed;
	private TextChangeListener listener;
	private boolean	textChanged;
	public boolean	editable = true;
	
	public UITextField(float x, float y, float w, float h)
	{
		this(x,y,w,h,"");
	}
	
	public UITextField(float x, float y, float w, float h, String txt)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		builder.append(txt);
	}
	
	public void setListener(TextChangeListener l)
	{
		listener = l;
	}
	
	public TextChangeListener getListener()
	{
		return listener;
	}
	
	public void onMouseEvent(int mx, int my, int buttonPressed, boolean released)
	{
		if(isMouseOver(mx,my))
		{
			if(buttonPressed == 0 && released)
			{
				focused = true;
			}
		}
		else if(buttonPressed == 0 && released)
		{
			focused = false;
		}
		
		if(isMouseOver(mx,my) && buttonPressed == 0 && released)
		{
			float tx = mx-x;
			index = (int)(tx/8f);
			if(index < 0)
			{
				index = 0;
			}
			else if(index > builder.length())
			{
				index = builder.length();
			}
			selectionStartIndex = index;
			mousePressed = true;
		}
		if(!released && buttonPressed == 0)
		{
			mousePressed = false;
		}
	}
	
	public void render(int mx, int my, boolean[] buttons)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(0.75f, 0.75f, 0.75f);
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x+w, y);
		GL11.glVertex2f(x+w, y+h);
		GL11.glVertex2f(x, y+h);
		GL11.glColor3f(0, 0, 0);
		GL11.glVertex2f(x+1, y+1);
		GL11.glVertex2f(x+w-1, y+1);
		GL11.glVertex2f(x+w-1, y+h-1);
		GL11.glVertex2f(x+1, y+h-1);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		float tx = x;
		float ty = y;
		boolean thereIsMoreBefore = false;
		while(tx+index*8f > x+w-8f)
		{
			tx-=8f;
			thereIsMoreBefore = true;
		}
		boolean thereIsMoreAfter = false;
		if(FontRenderer.getWidth(builder.toString()) > w && index < builder.length())
		{
			thereIsMoreAfter = true;;
		}
		
		if(builder.toString().trim().equals("") && !focused)
		{
			FontRenderer.drawStringInBounds(placeholder, tx+4, y+h/2f-8, 0xB0B0B0,x,y,x+w,y+h);
		}
		else if(!builder.toString().trim().equals(""))
		{
			FontRenderer.drawStringInBounds(builder.toString(), tx+4, ty+h/2f-8, 0xFFFFFF,x,y,x+w,y+h);
		}
		if(focused)
		{
			cursorTimer++;
			if(cursorTimer >= 50)
			{
				showCursor = !showCursor;
				cursorTimer = showCursor ? -30 : 0;
			}
		}
		if(showCursor && focused)
		{
			if(index >= builder.length())
			{
				FontRenderer.drawString("_", tx+4+index*8f, y+h/2f-8, 0xFFFFFF);
			}
			else
			{
				FontRenderer.drawString("|", tx+index*8f, y+h/2f-8, 0xFFFFFF);
			}
		}
		
		int sIndex1 = Math.min(selectionStartIndex, selectionEndIndex);
		int sIndex2 = Math.max(selectionStartIndex, selectionEndIndex);
		
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0, 0, 1, 0.25f);
		GL11.glBegin(GL11.GL_QUADS);
		while(tx+4+sIndex1*8f < x)
		{
			sIndex1++;
		}
		
		while(tx+4+sIndex2*8f > x+w)
		{
			sIndex2--;
		}
		GL11.glVertex2f(tx+4+sIndex1*8f, y+h/2f-8);
		GL11.glVertex2f(tx+4+sIndex2*8f, y+h/2f-8);
		GL11.glVertex2f(tx+4+sIndex2*8f, y+h/2f+8);
		GL11.glVertex2f(tx+4+sIndex1*8f, y+h/2f+8);
		
		if(!thereIsMoreBefore)
		{
			GL11.glColor4f(0.75f, 0.75f, 0.75f, 0.5f);
			GL11.glVertex2f(x+1, y+h/2f-14);
			GL11.glVertex2f(x+1+1, y+h/2f-14);
			GL11.glVertex2f(x+1+1, y+h/2f+14);
			GL11.glVertex2f(x+1, y+h/2f+14);
		}
		if(!thereIsMoreAfter)
		{
			GL11.glColor4f(0.75f, 0.75f, 0.75f, 0.5f);
			GL11.glVertex2f(x+w-2, y+h/2f-14);
			GL11.glVertex2f(x+w-2+1, y+h/2f-14);
			GL11.glVertex2f(x+w-2+1, y+h/2f+14);
			GL11.glVertex2f(x+w-2, y+h/2f+14);
		}
		GL11.glEnd();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public String getPlaceholder()
	{
		return placeholder;
	}
	
	public void setPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
	}
	
	public void update(int mx, int my, boolean[] buttons)
	{
		if(mousePressed && selectionStartIndex != -1)
		{
			float tx = mx-x;
			selectionEndIndex = (int)(tx/8f);
			if(selectionEndIndex < 0)
			{
				selectionEndIndex = 0;
			}
			else if(selectionEndIndex > builder.length())
			{
				selectionEndIndex = builder.length();
			}
		}
	}
	
	public void onKeyEvent(char c, int key, boolean releasedOrPressed)
	{
	    if(!focused)
	        return;
		if(!releasedOrPressed)
		{
			if(key == Keyboard.KEY_LSHIFT || key == Keyboard.KEY_RSHIFT)
			{
				shiftPressed = false;
			}
			if(key == Keyboard.KEY_LCONTROL)
			{
				controlPressed = false;
			}
		}
		if(releasedOrPressed)
		{
			int sindex1 = Math.min(selectionStartIndex, selectionEndIndex);
			int sindex2 = Math.max(selectionStartIndex, selectionEndIndex);
			if(key == Keyboard.KEY_LSHIFT || key == Keyboard.KEY_RSHIFT)
			{
				shiftPressed = true;
				return;
			}
			if(key == Keyboard.KEY_LCONTROL)
			{
				controlPressed = true;
				return;
			}
			if((key == Keyboard.KEY_C || key == Keyboard.KEY_X) && controlPressed)
			{
				String selected = builder.substring(sindex1, sindex2);
				if(selected != null && !selected.trim().equals(""))
				{
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selected), null);
					boolean flag = key == Keyboard.KEY_X && editable;
					if(flag)
					{
						if(sindex1 < 0)
							sindex1 = 0;
						textChanged = true;
						builder.replace(sindex1, sindex2+1, "");
					}
				}
			}
			else if(key == Keyboard.KEY_V && controlPressed && editable)
			{
				Transferable s = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if(s != null)
				{
					try
					{
						if(index >= builder.length())
						{
							textChanged = true;
							builder.insert(index, s.getTransferData(DataFlavor.stringFlavor));
						}
						else
						{
							textChanged = true;
							builder.insert(index, s.getTransferData(DataFlavor.stringFlavor));
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			if(key == Keyboard.KEY_END)
			{
				index = builder.length();
			}
			else if(key == 199)
			{
				index = 0;
			}
			if(key == Keyboard.KEY_LEFT)
			{
				index--;
			}
			if(key == Keyboard.KEY_RIGHT)
			{
				index++;
			}
			if(editable)
			if(key == Keyboard.KEY_DELETE)
			{
				if(sindex2-1 < builder.length())
				{
					if(sindex1 < 0)
						sindex1 = 0;
					textChanged = true;
					builder.replace(sindex1, sindex2+1, "");
				}
			}
			if(editable)
			if(key == Keyboard.KEY_BACK)
			{
				if(sindex2 <= builder.length() && sindex1-1 >= 0)
				{
					if(sindex1 != sindex2)
					{
						builder.replace(sindex1, sindex2, "");
						textChanged = true;
						index=sindex1;
					}
					else
					{
						textChanged = true;
						builder.replace(sindex1-1, sindex2, "");
						index=sindex1-1;
					}
				}
				else if(sindex1 <= 0 && builder.length() > 0)
				{
					if(sindex1 != sindex2)
					{
						textChanged = true;
						builder.replace(sindex1 < 0 ? 0 : sindex1, sindex2, "");
						index=sindex1;
					}
				}
			}
			if(index < 0)
			{
				index = 0;
			}
			else if(index > builder.length())
			{
				index = builder.length();
			}
			if(!shiftPressed)
			{
				selectionStartIndex = index;
			}
			selectionEndIndex = index;

			if(editable)
			if(c >= '!' || c == ' ')
			{
                if(sindex1 != sindex2)
                    builder.replace(sindex1, sindex2, "");
				if(index >= builder.length())
				{
					builder.append(c);
					textChanged = true;
					index++;
					selectionStartIndex = index;
					selectionEndIndex = index;
				}
				else
				{
					builder.insert(index, c);
					textChanged = true;
					index++;
					selectionStartIndex = index;
					selectionEndIndex = index;
				}
			}
		}
		
		if(textChanged && listener != null)
		{
			listener.textChanged(this, builder.toString());
		}
	}

	public String getText()
	{
		return builder.toString();
	}

	public void setText(String txt)
	{
		builder = new StringBuilder(txt);
		if(listener != null)
		{
			listener.textChanged(this, builder.toString());
		}
	}
}
