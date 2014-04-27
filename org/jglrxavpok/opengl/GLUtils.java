package org.jglrxavpok.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

public class GLUtils
{
    private static final Map<Integer, Integer> displayLists = new HashMap<Integer, Integer>();
    private static final List<?> textureList = new ArrayList<Object>();

    /**
     * Generates the specified number of display lists and returns the first index.
     */
    public static synchronized int generateDisplayLists(int par0)
    {
        int j = GL11.glGenLists(par0);
        displayLists.put(Integer.valueOf(j), Integer.valueOf(par0));
        return j;
    }

    public static synchronized void deleteDisplayLists(int par0)
    {
        GL11.glDeleteLists(par0, ((Integer)displayLists.remove(Integer.valueOf(par0))).intValue());
    }

    public static synchronized void deleteTextures()
    {
        for (int i = 0; i < textureList.size(); ++i)
        {
            GL11.glDeleteTextures(((Integer)textureList.get(i)).intValue());
        }

        textureList.clear();
    }

    public static synchronized void deleteTexturesAndDisplayLists()
    {
        Iterator<?> iterator = displayLists.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry<?, ?> entry = (Entry<?, ?>)iterator.next();
            GL11.glDeleteLists(((Integer)entry.getKey()).intValue(), ((Integer)entry.getValue()).intValue());
        }

        displayLists.clear();
        deleteTextures();
    }

    /**
     * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static synchronized ByteBuffer createDirectByteBuffer(int par0)
    {
        return ByteBuffer.allocateDirect(par0).order(ByteOrder.nativeOrder());
    }

    /**
     * Creates and returns a direct int buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static IntBuffer createDirectIntBuffer(int par0)
    {
        return createDirectByteBuffer(par0 << 2).asIntBuffer();
    }

    /**
     * Creates and returns a direct float buffer with the specified capacity. Applies native ordering to speed up
     * access.
     */
    public static FloatBuffer createDirectFloatBuffer(int par0)
    {
        return createDirectByteBuffer(par0 << 2).asFloatBuffer();
    }
}
