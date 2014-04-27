package org.jglrxavpok.ui;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.Textures;

public class UIFileSlot extends UISlot
{

	public File	file;
	public String replacementTxt = null;
	private int	textID;
	private static FileSystemView view = FileSystemView.getFileSystemView();

	public UIFileSlot(ActionListener listener, File f)
	{
		super(listener, 516, 20);
		file = f;
		try
		{
			textID = Textures.get(ImageIO.read(f));
		}
		catch(Exception e)
		{
			textID = Textures.get(org.jglrxavpok.opengl.ImageUtils.toBufferedImage(view.getSystemIcon(f)));
		}
	}
	
	public void renderSlot(int id, float x, float y, int mx, int my, boolean[] buttons, boolean selected)
	{
		super.renderSlot(id, x, y, mx, my, buttons, selected);
		String n = file.getName();
		if(file.isDirectory() && !file.getName().endsWith(File.separator))
			n+=File.separator;
		if(!FontRenderer.drawStringInBounds(replacementTxt == null ? n: replacementTxt, x+22, y+20/2f-8, !isMouseOver(x,y,w,h,mx, my) ? 0xFFFFFF: 0xFFFF00,x,y,x+500,y+h))
		{
			FontRenderer.drawString("[..]", x+500-16f, y+20/2f-8, !isMouseOver(x,y,w,h,mx, my) ? 0xFFFFFF: 0xFFFF00);
		}
		Textures.render(textID, x+2, y+2, 16, 16);
	}
	

}
