package org.jglrxavpok.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class Tessellator
{
    private static int nativeBufferSize = 0x200000;
    private static int trivertsInBuffer = (nativeBufferSize / 48) * 6;
    private int rawBufferSize = 0;
    /**
     * Boolean used to check whether quads should be drawn as two triangles. Initialized to false and never changed.
     */
    private static boolean convertQuadsToTriangles;


    /** The byte buffer used for GL allocation. */
    private static ByteBuffer byteBuffer = GLUtils.createDirectByteBuffer(nativeBufferSize * 4);

    /** The same memory as byteBuffer, but referenced as an integer buffer. */
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();

    /** The same memory as byteBuffer, but referenced as an float buffer. */
    private static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

    /** Short buffer */
    private static ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

    /** Raw integer array. */
    private int[] currentDataBuffer;

    /**
     * The number of vertices to be drawn in the next draw call. Reset to 0 between draw calls.
     */
    private int vertexCount;

    /** The first coordinate to be used for the texture. */
    private double textureU;

    /** The second coordinate to be used for the texture. */
    private double textureV;
    private int brightness;

    /** The color (RGBA) value to be used for the following draw call. */
    private int color;

    /**
     * Whether the current draw object for this tessellator has color values.
     */
    private boolean hasColor;

    /**
     * Whether the current draw object for this tessellator has texture coordinates.
     */
    private boolean hasTexture;
    
    /**
     * Whether the current draw object for this tessellator has normal coordinates.
     */
    private boolean hasBrightness;

    /**
     * Whether the current draw object for this tessellator has normal values.
     */
    private boolean hasNormals;

    /** The index into the raw buffer to be used for the next data. */
    private int rawBufferIndex;

    /**
     * The number of vertices manually added to the given draw call. This differs from vertexCount because it adds extra
     * vertices when converting quads to triangles.
     */
    private int addedVertices;

    /** Disables all color information for the following draw call. */
    private boolean isColorDisabled;

    /** The draw mode currently being used by the tessellator. */
    public int drawMode;

    /**
     * An offset to be applied along the x-axis for all vertices in this draw call.
     */
    public double xOffset;

    /**
     * An offset to be applied along the y-axis for all vertices in this draw call.
     */
    public double yOffset;

    /**
     * An offset to be applied along the z-axis for all vertices in this draw call.
     */
    public double zOffset;

    /** The normal to be applied to the face being drawn. */
    private int normal;

    /** The static instance of the Tessellator. */
    public static Tessellator instance = new Tessellator(2097152);

    /** Whether this tessellator is currently in draw mode. */
    public boolean isDrawing;

    /** Whether we are currently using VBO or not. */
    private static boolean useVBO = false;

    /** An IntBuffer used to store the indices of vertex buffer objects. */
    private static IntBuffer vertexBuffers;

    /**
     * The index of the last VBO used. This is used in round-robin fashion, sequentially, through the vboCount vertex
     * buffers.
     */
    private int vboIndex;
    private int bufferSize;

    /** Number of vertex buffer objects allocated for use. */
    private static int vboCount = 10;

    public Tessellator(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }
    
    static
    {
        useVBO = GLContext.getCapabilities().GL_ARB_vertex_buffer_object;

        if (useVBO)
        {
            vertexBuffers = GLUtils.createDirectIntBuffer(vboCount);
            ARBVertexBufferObject.glGenBuffersARB(vertexBuffers);
        }
    }

    /**
     * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
     */
    public int flush()
    {
        if (!this.isDrawing)
        {
            throw new IllegalStateException("Not tesselating!");
        }
        else
        {
            this.isDrawing = false;

            int offs = 0;
            while (offs < vertexCount)
            {
                int vtc = 0;
                if (drawMode == GL11.GL_QUADS && convertQuadsToTriangles)
                {
                    vtc = Math.min(vertexCount - offs, trivertsInBuffer);
                }
                else
                {
                    vtc = Math.min(vertexCount - offs, nativeBufferSize >> 5);
                }
                Tessellator.intBuffer.clear();
                Tessellator.intBuffer.put(this.currentDataBuffer, offs * 8, vtc * 8);
                Tessellator.byteBuffer.position(0);
                Tessellator.byteBuffer.limit(vtc * 32);
                offs += vtc;

                if (useVBO)
                {
                    this.vboIndex = (this.vboIndex + 1) % Tessellator.vboCount;
                    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, Tessellator.vertexBuffers.get(this.vboIndex));
                    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, Tessellator.byteBuffer, ARBVertexBufferObject.GL_STREAM_DRAW_ARB);
                }

                if (this.hasTexture)
                {
                    if (Tessellator.useVBO)
                    {
                        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12L);
                    }
                    else
                    {
                        Tessellator.floatBuffer.position(3);
                        GL11.glTexCoordPointer(2, 32, Tessellator.floatBuffer);
                    }

                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);

                    if (Tessellator.useVBO)
                    {
                        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 32, 28L);
                    }
                    else
                    {
                        Tessellator.shortBuffer.position(14);
                        GL11.glTexCoordPointer(2, 32, Tessellator.shortBuffer);
                    }

                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    if (Tessellator.useVBO)
                    {
                        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 32, 20L);
                    }
                    else
                    {
                        Tessellator.byteBuffer.position(20);
                        GL11.glColorPointer(4, true, 32, Tessellator.byteBuffer);
                    }

                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    if (Tessellator.useVBO)
                    {
                        GL11.glNormalPointer(GL11.GL_UNSIGNED_BYTE, 32, 24L);
                    }
                    else
                    {
                        Tessellator.byteBuffer.position(24);
                        GL11.glNormalPointer(32, Tessellator.byteBuffer);
                    }

                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                if (Tessellator.useVBO)
                {
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0L);
                }
                else
                {
                    Tessellator.floatBuffer.position(0);
                    GL11.glVertexPointer(3, 32, Tessellator.floatBuffer);
                }

                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

                if (this.drawMode == GL11.GL_QUADS && convertQuadsToTriangles)
                {
                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vtc);
                }
                else
                {
                    GL11.glDrawArrays(this.drawMode, 0, vtc);
                }

                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

                if (this.hasTexture)
                {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            if (rawBufferSize > 0x20000 && rawBufferIndex < (rawBufferSize << 3))
            {
                rawBufferSize = 0;
                currentDataBuffer = null;
            }

            int i = this.rawBufferIndex * 4;
            this.reset();
            return i;
        }
    }

    /**
     * Clears the tessellator state in preparation for new drawing.
     */
    private void reset()
    {
        this.vertexCount = 0;
        Tessellator.byteBuffer.clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    /**
     * Sets draw mode in the tessellator to draw quads.
     */
    public void startDrawingQuads()
    {
        this.startDrawing(GL11.GL_QUADS);
    }

    /**
     * Resets tessellator state and prepares for drawing (with the specified draw mode).
     */
    public void startDrawing(int par1)
    {
        if (this.isDrawing)
        {
            throw new IllegalStateException("Already tesselating!");
        }
        else
        {
            this.isDrawing = true;
            this.reset();
            this.drawMode = par1;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.hasBrightness = false;
            this.isColorDisabled = false;
        }
    }

    /**
     * Sets the texture coordinates.
     */
    public void setTextureUV(double par1, double par3)
    {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
    }

    public void setBrightness(int par1)
    {
        this.hasBrightness = true;
        this.brightness = par1;
    }

    /**
     * Sets the RGB values as specified, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorOpaque_F(float par1, float par2, float par3)
    {
        this.setColorOpaque((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F));
    }

    /**
     * Sets the RGBA values for the color, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorRGBA_F(float par1, float par2, float par3, float par4)
    {
        this.setColorRGBA((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F), (int)(par4 * 255.0F));
    }

    /**
     * Sets the RGB values as specified, and sets alpha to 255.
     */
    public void setColorOpaque(int red, int green, int blue)
    {
        this.setColorRGBA(red, green, blue, 255);
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    public void setColorRGBA(int red, int green, int blue, int alpha)
    {
        if (!this.isColorDisabled)
        {
            if (red > 255)
            {
                red = 255;
            }

            if (green > 255)
            {
                green = 255;
            }

            if (blue > 255)
            {
                blue = 255;
            }

            if (alpha > 255)
            {
                alpha = 255;
            }

            if (red < 0)
            {
                red = 0;
            }

            if (green < 0)
            {
                green = 0;
            }

            if (blue < 0)
            {
                blue = 0;
            }

            if (alpha < 0)
            {
                alpha = 0;
            }

            this.hasColor = true;

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            {
                this.color = alpha << 24 | blue << 16 | green << 8 | red;
            }
            else
            {
                this.color = red << 24 | green << 16 | blue << 8 | alpha;
            }
        }
    }

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    public void addVertexWithUV(double par1, double par3, double par5, double par7, double par9)
    {
        this.setTextureUV(par7, par9);
        this.addVertex(par1, par3, par5);
    }

    /**
     * Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets
     * full.
     */
    public void addVertex(double par1, double par3, double par5)
    {
        if (rawBufferIndex >= rawBufferSize - 32) 
        {
            if (rawBufferSize == 0)
            {
                rawBufferSize = bufferSize;
                currentDataBuffer = new int[rawBufferSize];
            }
            else
            {
                rawBufferSize *= 2;
                currentDataBuffer = Arrays.copyOf(currentDataBuffer, rawBufferSize);
            }
        }
        ++this.addedVertices;

        if (this.drawMode == GL11.GL_QUADS && convertQuadsToTriangles && this.addedVertices % 4 == 0)
        {
            for (int i = 0; i < 2; ++i)
            {
                int j = 8 * (3 - i);

                if (this.hasTexture)
                {
                    this.currentDataBuffer[this.rawBufferIndex + 3] = this.currentDataBuffer[this.rawBufferIndex - j + 3];
                    this.currentDataBuffer[this.rawBufferIndex + 4] = this.currentDataBuffer[this.rawBufferIndex - j + 4];
                }

                if (this.hasBrightness)
                {
                    this.currentDataBuffer[this.rawBufferIndex + 7] = this.currentDataBuffer[this.rawBufferIndex - j + 7];
                }

                if (this.hasColor)
                {
                    this.currentDataBuffer[this.rawBufferIndex + 5] = this.currentDataBuffer[this.rawBufferIndex - j + 5];
                }

                this.currentDataBuffer[this.rawBufferIndex + 0] = this.currentDataBuffer[this.rawBufferIndex - j + 0];
                this.currentDataBuffer[this.rawBufferIndex + 1] = this.currentDataBuffer[this.rawBufferIndex - j + 1];
                this.currentDataBuffer[this.rawBufferIndex + 2] = this.currentDataBuffer[this.rawBufferIndex - j + 2];
                ++this.vertexCount;
                this.rawBufferIndex += 8;
            }
        }

        if (this.hasTexture)
        {
            this.currentDataBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)this.textureU);
            this.currentDataBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)this.textureV);
        }

        if (this.hasBrightness)
        {
            this.currentDataBuffer[this.rawBufferIndex + 7] = this.brightness;
        }

        if (this.hasColor)
        {
            this.currentDataBuffer[this.rawBufferIndex + 5] = this.color;
        }

        if (this.hasNormals)
        {
            this.currentDataBuffer[this.rawBufferIndex + 6] = this.normal;
        }

        this.currentDataBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(par1 + this.xOffset));
        this.currentDataBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(par3 + this.yOffset));
        this.currentDataBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(par5 + this.zOffset));
        this.rawBufferIndex += 8;
        ++this.vertexCount;
    }

    /**
     * Sets the color to the given opaque value (stored as byte values packed in an integer).
     */
    public void setColorOpaque_I(int par1)
    {
        int j = par1 >> 16 & 255;
        int k = par1 >> 8 & 255;
        int l = par1 & 255;
        this.setColorOpaque(j, k, l);
    }

    /**
     * Sets the color to the given color (packed as bytes in integer) and alpha values.
     */
    public void setColorRGBA_I(int par1, int par2)
    {
        int k = par1 >> 16 & 255;
        int l = par1 >> 8 & 255;
        int i1 = par1 & 255;
        this.setColorRGBA(k, l, i1, par2);
    }

    public void setColor(int color)
    {
        this.color = color;
        hasColor = true;
    }
    
    /**
     * Disables colors for the current draw call.
     */
    public void disableColor()
    {
        this.isColorDisabled = true;
    }

    /**
     * Sets the normal for the current draw call.
     */
    public void setNormal(float par1, float par2, float par3)
    {
        this.hasNormals = true;
        byte b0 = (byte)((int)(par1 * 127.0F));
        byte b1 = (byte)((int)(par2 * 127.0F));
        byte b2 = (byte)((int)(par3 * 127.0F));
        this.normal = b0 & 255 | (b1 & 255) << 8 | (b2 & 255) << 16;
    }

    /**
     * Sets the translation for all vertices in the current draw call.
     */
    public void setTranslation(double par1, double par3, double par5)
    {
        this.xOffset = par1;
        this.yOffset = par3;
        this.zOffset = par5;
    }

    /**
     * Offsets the translation for all vertices in the current draw call.
     */
    public void addTranslation(float xTranslation, float yTranslation, float zTranslation)
    {
        this.xOffset += (double)xTranslation;
        this.yOffset += (double)yTranslation;
        this.zOffset += (double)zTranslation;
    }
    
    public void setColorEnabled(boolean b)
    {
        hasColor = b;
    }
    
    public static class OpenGlHelper
    {
        /**
         * An OpenGL constant corresponding to GL_TEXTURE0, used when setting data pertaining to auxiliary OpenGL texture
         * units.
         */
        public static int defaultTexUnit;

        /**
         * An OpenGL constant corresponding to GL_TEXTURE1, used when setting data pertaining to auxiliary OpenGL texture
         * units.
         */
        public static int lightmapTexUnit;

        /**
         * True if the renderer supports multitextures and the OpenGL version != 1.3
         */
        private static boolean useMultitextureARB;

        /* Stores the last values sent into setLightmapTextureCoords */
        public static float lastBrightnessX = 0.0f;
        public static float lastBrightnessY = 0.0f;

        /**
         * Initializes the texture constants to be used when rendering lightmap values
         */
        public static void initializeTextures()
        {
            useMultitextureARB = GLContext.getCapabilities().GL_ARB_multitexture && !GLContext.getCapabilities().OpenGL13;

            if (useMultitextureARB)
            {
                defaultTexUnit = 33984;
                lightmapTexUnit = 33985;
            }
            else
            {
                defaultTexUnit = 33984;
                lightmapTexUnit = 33985;
            }
        }

        /**
         * Sets the current lightmap texture to the specified OpenGL constant
         */
        public static void setActiveTexture(int par0)
        {
            if (useMultitextureARB)
            {
                ARBMultitexture.glActiveTextureARB(par0);
            }
            else
            {
                GL13.glActiveTexture(par0);
            }
        }

        /**
         * Sets the current lightmap texture to the specified OpenGL constant
         */
        public static void setClientActiveTexture(int par0)
        {
            if (useMultitextureARB)
            {
                ARBMultitexture.glClientActiveTextureARB(par0);
            }
            else
            {
                GL13.glClientActiveTexture(par0);
            }
        }

        /**
         * Sets the current coordinates of the given lightmap texture
         */
        public static void setLightmapTextureCoords(int par0, float par1, float par2)
        {
            if (useMultitextureARB)
            {
                ARBMultitexture.glMultiTexCoord2fARB(par0, par1, par2);
            }
            else
            {
                GL13.glMultiTexCoord2f(par0, par1, par2);
            }

            if (par0 == lightmapTexUnit)
            {
                lastBrightnessX = par1;
                lastBrightnessY = par2;
            }
        }
        
        public static void startNegativeMode()
        {
            GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
            GL11.glLogicOp(GL11.GL_INVERT);
        }
        
        public static void endNegativeMode()
        {
            GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        }
    }

    public int getColor()
    {
        return color;
    }

}
