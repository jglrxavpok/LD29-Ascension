package org.jglrxavpok.storage;

import java.util.HashMap;

public class TaggedStorageChunk
{

    private String name;
    private HashMap<String, TaggedStorageTag> tags = new HashMap<String, TaggedStorageTag>();

    TaggedStorageChunk(){}
    
    public TaggedStorageChunk(String chunkName)
    {
        this.name = chunkName;
    }
    
    public String getChunkName()
    {
        return name;
    }
    
    public void setInteger(String name, int value)
    {
        setString(name, ""+value);
    }
    
    public void setBoolean(String name, boolean value)
    {
        setString(name, ""+value);
    }
    
    public void setString(String name, String value)
    {
        TaggedStorageTag tag = tags.get(name);
        if(tag == null)
            tag = new TaggedStorageTag(name);
        tag.setValue(value);
        addTag(tag);
    }
    
    public TaggedStorageTag get(String name)
    {
        return tags.get(name);
    }
    
    public boolean getBoolean(String tag)
    {
        if(tags.containsKey(tag))
            return tags.get(tag).getBooleanValue();
        return false;
    }
    
    public int getInteger(String tag)
    {
        if(tags.containsKey(tag))
            return tags.get(tag).getIntegerValue();
        return 0;
    }
    
    public float getFloat(String tag)
    {
        if(tags.containsKey(tag))
            return tags.get(tag).getFloatValue();
        return 0;
    }
    
    public double getDouble(String tag)
    {
        if(tags.containsKey(tag))
            return tags.get(tag).getDoubleValue();
        return 0;
    }
    
    public String getString(String tag)
    {
        if(tags.containsKey(tag))
            return tags.get(tag).getValue();
        return null;
    }
    
    public void addTag(TaggedStorageTag tag)
    {
        tags.put(tag.getTagName(), tag);
    }

    public TaggedStorageTag[] getTags()
    {
        return tags.values().toArray(new TaggedStorageTag[1]);
    }

    void setName(String n)
    {
        name = n;
    }

    public void setDouble(String string, double d)
    {
        setString(string, ""+d);
    }
    
    public void setFloat(String string, float f)
    {
        setString(string, ""+f);
    }

    public boolean hasTag(String string)
    {
        return tags.containsKey(string);
    }
}
