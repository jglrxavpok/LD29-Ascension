package org.jglrxavpok.ui;

import org.jglrxavpok.opengl.Textures;
import org.lwjgl.input.Mouse;

public class UI 
{

    public static UIMenu newMenu;
    public static UIMenu menu;
    public static float TEMPLATE_WIDTH = 200;
    public static float TEMPLATE_HEIGHT = 300;
    public static int TEMPLATE_TEXID = -1; 
    private static float uiheight;
    private static float uiwidth;
    private static int cursorX;
    private static int cursorY;

    public static void displayMenu(UIMenu menu)
    {
        newMenu = menu;
        if(UI.menu != null)
            UI.menu.onMenuClose();
    }

    public static void render()
    {
        if(menu != null)
        menu.render(cursorX, cursorY, new boolean[]
                {
            Mouse.isButtonDown(0),Mouse.isButtonDown(2),Mouse.isButtonDown(1)
                });
    }
    
    public static void renderBackground()
    {
        if(menu != null)
        menu.renderBackground(cursorX, cursorY, new boolean[]
                {
            Mouse.isButtonDown(0),Mouse.isButtonDown(2),Mouse.isButtonDown(1)
                });
    }
    
    public static void renderOverlays()
    {
        if(menu != null)
        menu.renderOverlay(cursorX, cursorY, new boolean[]
                {
            Mouse.isButtonDown(0),Mouse.isButtonDown(2),Mouse.isButtonDown(1)
                });
    }
    
    public static void update()
    {
        if(menu != null)
        menu.update(cursorX, cursorY, new boolean[]
                {
            Mouse.isButtonDown(0),Mouse.isButtonDown(2),Mouse.isButtonDown(1)
                });
    }
    
    public static void onMouseEvent(int mx, int my, int button, boolean released)
    {
        if(menu != null)
        {
            menu.onMouseEvent(mx, my, button, released);
        }
    }
    
    public static void onKeyEvent(char c, int k, boolean released)
    {
        if(menu != null)
        {
            menu.onKeyEvent(c, k, released);
        }
    }
    
    public static void preRender()
    {
        if(newMenu == menu)
            return;
        if(TEMPLATE_TEXID == -1)
        {
            TEMPLATE_TEXID = Textures.getFromClasspath("/assets/textures/ui.png");
        }
        if(newMenu != null)
        {
            menu = newMenu;
            menu.comps.clear();
            menu.initMenu();
        }
        else
        {
            menu = null;
        }
    }
    
    public static void lazyDrawAll()
    {
        preRender();
        if(menu != null)
        {
            renderBackground();
            render();
            renderOverlays();
        }
    }
    
    public static boolean isMenuNull()
    {
        return menu == null;
    }

    public static boolean doesMenuPauseGame()
    {
        if(menu == null)
            return false;
        return menu.doesPauseGame();
    }

    public static void setWidth(int w)
    {
        uiwidth = w;
    }
    
    public static void setHeight(int h)
    {
        uiheight = h;
    }
    
    public static float getHeight()
    {
        return uiheight;
    }
    
    public static float getWidth()
    {
        return uiwidth;
    }

    public static int getCursorX()
    {
        return cursorX;
    }

    public static int getCursorY()
    {
        return cursorY;
    }
    
    public static void setCursorPos(int cx, int cy)
    {
        cursorX = cx;
        cursorY = cy;
    }
}
