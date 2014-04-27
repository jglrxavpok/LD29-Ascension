package org.jglrxavpok.opengl;

import java.util.Random;

import org.lwjgl.opengl.GL11;

public class FontRenderer
{

	private static Random rand = new Random();
	private static boolean	bitrottAllowed = true;
	
	public static GLFont font = new GLPixelFont();
    private static float scale = 1f;
	
	public static void setScale(float scale)
	{
	    FontRenderer.scale = scale;
	}
	
	public static float getScale()
	{
	    return scale;
	}
	
	public static void drawString(String s, float x, float y, int color)
	{
		drawStringInBounds(s,x,y,color,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
	}
	
	public static void drawShadowedString(String s, float x, float y, int color)
	{
		drawShadowedStringInBounds(s,x,y,color,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
	}
	
	public static boolean drawShadowedStringInBounds(String s, float x, float y, int color, float minX, float minY, float maxX, float maxY)
	{
		boolean f = drawStringInBounds(s,x+1,y-1,0,minX,minY,maxX,maxY);
		boolean f1 = drawStringInBounds(s,x,y,color,minX,minY,maxX,maxY);
		return f&f1;
	}
	
	public static boolean drawStringInBounds(String s, float x, float y, int color, float minX, float minY, float maxX, float maxY)
	{
		if(s==null)
			return true;
		GL11.glPushMatrix();
		boolean flag = true;
		font.bind();
		Tessellator t = Tessellator.instance;
		float a = ((color >> 24) & 0xFF)/255f;
		float r = ((color >> 16) & 0xFF)/255f;
		float g = ((color >> 8) & 0xFF)/255f;
		float b = ((color >> 0) & 0xFF)/255f;
		t.startDrawingQuads();
		t.setColorRGBA_F(r, b, b, a);
		x-=8;
		int index = -1;
		char[] chars = s.toCharArray();
		boolean obfuscated = false;
		boolean underlined = false;
		boolean italics = false;
		boolean inverseBitrottMode = false;
		int toSkip = 0;
		for(char c : chars)
		{
			index++;
			if(toSkip > 0)
			{
				toSkip--;
				continue;
			}
			if(c == '\\')
			{
			    if(index-1 >= 0)
			    {
			        if(chars[index-1] != '\\')
			        {
			            continue;
			        }
			    }
			    else
			        continue;
			}
			if(c == '§')
			{
				if(index+1 < chars.length)
				{
				    boolean escaped = false;
				    if(index-1 >= 0)
				    {
				        if(chars[index-1] == '\\')
				        {
				            escaped = true;
				        }
				    }
				    if(!escaped)
				    {
    					String formatting = ""+c+chars[index+1];
    					if(formatting.equals(TextFormatting.OBFUSCATED.toString()))
    					{
    						obfuscated = true;
    					}
    					else if(formatting.equals(TextFormatting.UNDERLINE.toString()))
    					{
    						underlined = true;
    					}
    					else if(formatting.equals(TextFormatting.ITALIC.toString()))
    					{
    						italics = true;
    					}
    					else if(formatting.equals(TextFormatting.RESET.toString()))
    					{
    						underlined = false;
    						obfuscated = false;
    						italics = false;
    						inverseBitrottMode = false;
    					}
    					else if(formatting.equals("§§"))
    					{
    						inverseBitrottMode = true;
    					}
    					toSkip = 1;
    					continue;
				    }
				}
			}
			if(underlined)
			{
				t.flush();
				t.startDrawing(GL11.GL_LINES);
				t.setColorOpaque_F(r, g, b);
				t.addVertex(x-font.getCharSpacing()*scale, y, 0);
				t.addVertex(x+(font.getCharWidth(c)*scale), y, 0);
				t.flush();
				t.startDrawingQuads();
				t.setColorOpaque_F(r, g, b);
			}
			boolean flag1 = font.shouldCharBeDrawn(c);
			if(c == ' ' && !flag1)
			{
				x+=font.getCharWidth(' ')*scale+font.getCharSpacing()*scale;
			}
			
			if(flag1)
			{
				x+=font.getCharWidth(c)*scale+font.getCharSpacing()*scale;
				if(x < minX || x+16f > maxX || y < minY || y+16f > maxY)
				{
					flag = false;
					continue;
				}
				
				float key = c;
				if(obfuscated)
				{
					do
					{
						key = rand.nextInt(256*6+index);
						while(key > 256*6)
						{
							key -= 256*6;
						}
					}while(key < '!' || (key > '~' && key < '¡'));
				}
				
				float v = 0;
				float u = 0;
				while(key-16f > 0f)
				{
					v+=16f;
					key-=16f;
				}
				float tmp = (256f/((int)key*16f));
				if(tmp == 1f || tmp == Float.NEGATIVE_INFINITY || tmp == Float.POSITIVE_INFINITY || tmp == Float.NaN)
				{
					v+=16f;
				}
				else
					u = 1f/(tmp);
				if(bitrottAllowed && font.allowBitrott())
				{
//					if((Lang.getCurrentLanguage().equals("bitrott") && !inverseBitrottMode) || (!Lang.getCurrentLanguage().equals("bitrott") && inverseBitrottMode))
//						v=1f/(256/(256f-v));
//					else
						v=1f/(256f*6/(256f*6-v));
				}
				else
					v=1f/(256f*6/(256f*6-v));
				if(italics)
				{
				    float italicsRotation = 6f;
				    t.addVertexWithUV(x+italicsRotation, y+font.getCharHeight(c)*scale, 0, u, v);
                    t.addVertexWithUV(x+font.getCharWidth(c)*scale+italicsRotation, y+font.getCharHeight(c)*scale, 0, u+1f/(256f/16f), v);
                    t.addVertexWithUV(x+font.getCharWidth(c)*scale-italicsRotation, y, 0, u+1f/(256f/16f), v-1f/(256f*6/16f));
                    t.addVertexWithUV(x-italicsRotation, y, 0, u, v-1f/(256f*6/16f));
				}
				else
				{
    				t.addVertexWithUV(x, y+font.getCharHeight(c)*scale, 0, u, v-1f/(256f*6/16f));
    				t.addVertexWithUV(x+font.getCharWidth(c)*scale, y+font.getCharHeight(c)*scale, 0, u+1f/(256f/16f), v-1f/(256f*6/16f));
    				t.addVertexWithUV(x+font.getCharWidth(c)*scale, y, 0, u+1f/(256f/16f), v);
    				t.addVertexWithUV(x, y, 0, u, v);
				}
			}
		}
		t.flush();
		t.setColorOpaque_I(0xFFFFFF);
		
		GL11.glPopMatrix();
		return flag;
	}

	public static float getWidth(String txt)
	{
		if(txt == null)
			return 0;
		float l = 0;
		int index = 0;
		int toSkip = 0;
		char[] chars = txt.toCharArray();
		for(char c : chars)
		{
			index++;
			if(c == ' ')
			{
				l+=font.getCharWidth(c)*scale+font.getCharSpacing()*scale;
				continue;
			}
			if(toSkip > 0)
			{
				toSkip--;
				continue;
			}
			if(c == '\\')
            {
			    if(index-1 >= 0)
			    {
			        if(chars[index-1] != '\\')
			        {
			            continue;
			        }
			    }
			    else
			        continue;
            }
			if(c == '§')
			{
				if(index+1 < chars.length)
				{
				    boolean escaped = false;
                    if(index-1 >= 0)
                    {
                        if(chars[index-1] == '\\')
                        {
                            escaped = true;
                        }
                    }
                    if(!escaped)
                    {
                        toSkip = 1;
                        continue;
                    }
				}
			}
			if(c < '!' || (c > '~' && c < '¡'))
				continue;
			l+=font.getCharWidth(c)*scale+font.getCharSpacing()*scale;
		}
		return l;
	}

	public static void setBitrottAllowed(boolean b)
	{
		bitrottAllowed = b;
	}
}
