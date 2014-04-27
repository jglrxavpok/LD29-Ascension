package org.jglrxavpok.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class TaggedStorageSystem
{

    public TaggedStorageSystem()
    {
        
    }

    public TaggedStorageChunk readChunk(byte[] bytes) throws IOException
    {
        TaggedStorageChunk chunk = new TaggedStorageChunk();
        ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry e;
        while((e = in.getNextEntry()) != null)
        {
            if(e.getName().equals("ChunkName"))
            {
                chunk.setName(readStringContent(in));
            }
            else if(e.getName() != null)
            {
                TaggedStorageTag tag = new TaggedStorageTag(e.getName());
                tag.setValue(readStringContent(in));
                chunk.addTag(tag);
            }
        }
        in.close();
        return chunk;
    }
    
    private String readStringContent(ZipInputStream in) throws IOException
    {
        int i = 0;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((i = in.read(buffer, 0, buffer.length)) != -1)
        {
            out.write(buffer,0,i);
        }
        out.close();
        return new String(out.toByteArray());
    }

    public byte[] writeChunk(TaggedStorageChunk chunk) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(baos);
        TaggedStorageTag[] tags = chunk.getTags();
        out.putNextEntry(new ZipEntry("ChunkName"));
        out.write(chunk.getChunkName().getBytes());
        for(TaggedStorageTag tag : tags)
        {
            out.putNextEntry(new ZipEntry(tag.getTagName()));
            out.write(tag.getValue().getBytes());
            out.flush();
            out.closeEntry();
        }
        out.flush();
        out.close();
        return baos.toByteArray();
    }
}
