package org.jglrxavpok.ui;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.filechooser.FileSystemView;

import org.jglrxavpok.opengl.FontRenderer;
import org.jglrxavpok.opengl.ImageUtils;
import org.jglrxavpok.opengl.Textures;
import org.jglrxavpok.ui.UILabel.LabelAlignment;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class UIFileChooser extends UIMenu implements TextChangeListener
{

	private FileChooserListener listener;
	private UIMenu	parentScreen;
	public UIButton	okButton;
	private UIButton	cancelButton;
	private File currentFile;
	private UITextField	fileName;
	private File currentFolder;
	private UIList	fileList;
	private boolean	showHidden;
	private boolean	dirty;
	private boolean	foldersSelectable;
	private boolean	choosing;
	private FileFilter	filter;
	
	public UIFileChooser(UIMenu parent)
	{
		super();
		currentFolder = new File(System.getProperty("user.home"));
		this.parentScreen = parent;
	}
	
	
	public void setChooserListener(FileChooserListener l)
	{
		listener = l;
	}
	
	public void initMenu()
	{
		okButton = new UIButton(this, w/2-210, 30, 200,20, "OK");
		okButton.enabled = false;
		comps.add(okButton);
		cancelButton = new UIButton(this, w/2+10, 30, 200,20, "Cancel");
		comps.add(cancelButton);
		
		UILabel title = new UILabel("Choose a file", w/2,h-60, LabelAlignment.CENTERED);
		title.color = 0xFFFFFF;
		fileName = new UITextField(w/2-210,60,400+20,30);
		fileName.setPlaceholder("Enter the file name here");
		fileName.setListener(this);
		
		fileList = new UIList(w/2-250,125,w,250);
		
		comps.add(fileList);
		comps.add(fileName);
		comps.add(title);
		
		updateList();
	}
	
	public void update(int mx, int my, boolean[] buttons)
	{
		fileName.editable = !choosing;
		if(dirty)
		{
			dirty = false;
			updateList();
		}
		super.update(mx, my, buttons);
	}
	
	private void renderLoading(String txt)
	{
		renderLoading(txt, 0,0,null);
	}
	
	private void renderLoading(String txt, int index, int fileNbr, File currentFile)
	{
		float x = Display.getWidth()/2;
		float y = Display.getHeight()/2;
		GL11.glEnable(GL11.GL_2D);
		GL11.glDisable(GL11.GL_3D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();

		glLoadIdentity();
		GLU.gluOrtho2D(0, Display.getWidth(), 0, Display.getHeight());

		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();

		glLoadIdentity();
		UI.renderBackground();

		GL11.glFlush();
		fileList.visible = false;
		this.render(0, 0, new boolean[3]);
		fileList.visible = true;
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(0.75f, 0.75f, 0.75f);
		GL11.glVertex2f(x-100, y-50);
		GL11.glVertex2f(x+100, y-50);
		GL11.glVertex2f(x+100, y-50+20);
		GL11.glVertex2f(x-100, y-50+20);
		GL11.glColor3f(0, 0, 0);
		GL11.glVertex2f(x-100+1, y-50+1);
		GL11.glVertex2f(x+100-1, y-50+1);
		GL11.glVertex2f(x+100-1, y-50+20-1);
		GL11.glVertex2f(x-100+1, y-50+20-1);
		if(fileNbr != 0)
		{
			float value = ((float)index/(float)fileNbr)*198f;
			GL11.glColor3f(0.1f, 0.9f, 0.1f);
			GL11.glVertex2f(x-100+1, y-50+1);
			GL11.glVertex2f(x-100+1+value, y-50+1);
			GL11.glVertex2f(x-100+1+value, y-50+20-1);
			GL11.glVertex2f(x-100+1, y-50+20-1);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1, 1, 1);
		String s1 = txt;
		FontRenderer.drawShadowedString(s1, x-FontRenderer.getWidth(s1)/2f, y+20, 0xFFFFFF);
		
		s1 = index+"/"+fileNbr;
		FontRenderer.drawShadowedString(s1, x-FontRenderer.getWidth(s1)/2f, y, 0xFFFFFF);
		
		s1 = currentFile != null ? currentFile.getName() : "";
		FontRenderer.drawShadowedString(s1, x-FontRenderer.getWidth(s1)/2f, y-20, 0xFFFFFF);
		
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
		GL11.glDisable(GL11.GL_2D);
		GL11.glEnable(GL11.GL_3D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		Display.update();
	}
	
	private void updateList()
	{
		renderLoading("Accessing file system");
		File[] files = currentFolder.listFiles();
		if(files != null)
		{
			renderLoading("Preparing file list", 0, files.length, null);
			fileList.amountScrolled = Float.POSITIVE_INFINITY;
			fileList.slotsVisible = -1;
			fileList.slots = new UIFileSlot[files.length+1];
			renderLoading("Preparing file list", 0, files.length, null);
			if(currentFolder.getParentFile() != null)
			{
				UIFileSlot parent = new UIFileSlot(this, currentFolder.getParentFile());
				parent.replacementTxt = "..";
				fileList.slots[0] = parent;
				renderLoading("Preparing file list",0, files.length, null);
			}
			renderLoading("Sorting files",0, files.length, null);
			Arrays.sort(files, 0, files.length, new Comparator<File>()
			{
				@Override
				public int compare(File o1, File o2)
				{
					if(o1.isDirectory() && o2.isFile())
					{
						return 0;
					}
					else if(o2.isDirectory() && o1.isFile())
					{
						return 1;
					}
					else
					{
						return o1.compareTo(o2);
					}
				}
			});
			renderLoading("End of preparating files",0, files.length, null);
			for(int i = 0;i<files.length;i++)
			{
				renderLoading("Parsing files... ",(i+1),files.length,files[i]);
				if(files[i].isHidden() && !showHidden)
					continue;
				if(filter != null && !filter.accept(files[i]))
					continue;
				fileList.slots[i+1] = new UIFileSlot(this, files[i]);
			}
		}
	}


	public void render(int mx, int my, boolean[] buttons)
	{
		super.render(mx, my, buttons);
		String p = currentFolder.getPath();
		if(!p.endsWith(File.separator))
			p+=File.separator;
		
		Textures.render(Textures.get(ImageUtils.toBufferedImage(FileSystemView.getFileSystemView().getSystemIcon(currentFolder))),w/2-FontRenderer.getWidth(p)/2-20,h-100,16,16);
		FontRenderer.drawString(p, w/2-FontRenderer.getWidth(p)/2f,h-100, 0xFFFFFF);
	}
	
	public void componentClicked(UIComponentBase comp)
	{
		if(comp == okButton)
		{
			currentFile = new File(currentFolder, fileName.getText());
			if(currentFile.isDirectory() && !foldersSelectable)
			{
				this.currentFolder = currentFile;
				dirty = true;
			}
			else
			{
				if(listener != null)
				{
					listener.fileChoosen(this, currentFile);
				}
				if(UI.newMenu == null)
				UI.displayMenu(parentScreen);
			}
		}
		else if(comp == cancelButton)
		{
			UI.displayMenu(parentScreen);
		}
		if(comp instanceof UIFileSlot)
		{
			UIFileSlot slot = (UIFileSlot)comp;
			fileName.setText(slot.file.getName());
			if(fileList.getSelected() != slot)
			{
				fileList.setSelected(slot);
				listener.fileSelected(this, slot.file);
			}
			else
			{
				if(slot.file.isDirectory())
				{
					this.currentFolder = slot.file;
					dirty = true;
				}
				else
				{
					currentFile = new File(currentFolder, fileName.getText());
					if(listener != null)
					{
						listener.fileChoosen(this, currentFile);
					}
					if(UI.newMenu == null)
					UI.displayMenu(parentScreen);
				}
			}
		}
	}


	@Override
	public void textChanged(Object source, String text)
	{
		if(source == fileName)
		{
			if(text != null && !text.trim().equals(""))
			{
				okButton.enabled = true;
			}
			else
				okButton.enabled = false;
			okButton.displayString = "Ok";
		}
	}


	public void setChooseMode(boolean b)
	{
		choosing = b;
	}
	
	public void setFilter(FileFilter f)
	{
		filter = f;
	}
}
