package org.jglrxavpok.opengl;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public class ImageUtils
{

	private static HashMap<String, BufferedImage> imgs = new HashMap<String, BufferedImage>();
	
	public static BufferedImage toBufferedImage(Image i)
	{
		BufferedImage result = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		result.createGraphics().drawImage(i, 0,0,null);
		return result;
	}
	
	public static Color getColor(int color, boolean hasAlpha)
	{
		float a = 1f;
		if(hasAlpha)
		{
			a = ((color >> 24) & 0xFF)/255f;
		}
		float r = ((color >> 16) & 0xFF)/255f;
		float g = ((color >> 8) & 0xFF)/255f;
		float b = ((color >> 0) & 0xFF)/255f;
		return new Color(r,g,b,a);
	}
	
	public static Color getColor(int color)
	{
		return getColor(color, false);
	}
	
	public static BufferedImage recolor(BufferedImage src, Color sc)
	{
		float[][] colorMatrix = { { ((float)sc.getRed())/255f, 0, 0,0 }, { ((float)sc.getGreen())/255f, 0, 0,0 }, { ((float)sc.getBlue())/255f, 0, 0,0 } , {0f,0f,0f,1f}};
		BandCombineOp changeColors = new BandCombineOp(colorMatrix, null);
		Raster sourceRaster = src.getRaster();
		WritableRaster displayRaster = sourceRaster.createCompatibleWritableRaster();
		changeColors.filter(sourceRaster, displayRaster);
		return new BufferedImage(src.getColorModel(), displayRaster, true, null);
	}

	public static BufferedImage getFromClasspath(String path)
	{
		if(path == null || path.trim().equals(""))
			return null;
		try
		{
			if(!imgs.containsKey(path))
			{
				imgs.put(path, ImageIO.read(ImageUtils.class.getResourceAsStream(path)));
			}
			return imgs.get(path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage toBufferedImage(Icon icon)
	{
		BufferedImage result = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(null, result.createGraphics(), 0, 0);
		return result;
	}
}
