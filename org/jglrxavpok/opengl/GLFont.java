package org.jglrxavpok.opengl;

public abstract class GLFont
{

	private int	texID;

	public GLFont(int texID)
	{
		this.texID = texID;
	}
	
	public void bind()
	{
		Textures.bind(texID);
	}
	
	public abstract boolean shouldCharBeDrawn(char c);
	
	public abstract float getCharWidth(char c);
	
	public abstract float getCharHeight(char c);
	
	public abstract float getCharSpacing();
	
	public boolean allowBitrott()
	{
		return true;
	}
}
