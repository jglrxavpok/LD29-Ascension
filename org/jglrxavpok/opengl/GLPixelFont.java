package org.jglrxavpok.opengl;


public class GLPixelFont extends GLFont
{

	public GLPixelFont()
	{
		super(LWJGLHandler.loadTexture(ImageUtils.getFromClasspath("/assets/textures/fonts/unicode_pages.png")));
	}

	@Override
	public boolean shouldCharBeDrawn(char c)
	{
		if(c == ' ')
		{
			return false;
		}
		if(c == '\n' || c == '\b')
		{
			return false;
		}
		
		if(c < '!')
			return false;
		return true;
	}

	@Override
	public float getCharWidth(char c)
	{
		return 16;
	}

	@Override
	public float getCharHeight(char c)
	{
		return 16;
	}

	@Override
	public float getCharSpacing()
	{
		return -8;
	}

}
