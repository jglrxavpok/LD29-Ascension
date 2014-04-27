package org.jglrxavpok.storage;

public class TaggedStorageTag
{

    private String name;
    private String value;

    public TaggedStorageTag(String name)
    {
        this.name = name;
    }
    
    public String getValue()
    {
        return value;  
    }
    
    public boolean getBooleanValue()
    {
        try
        {
            return Boolean.parseBoolean(value);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getIntegerValue()
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    
    public void setValue(String val)
    {
        value = val;
    }
    
    public void setBooleanValue(boolean b)
    {
        setValue(""+b);
    }
    
    public void setIntegerValue(int i)
    {
        setValue(""+i);
    }
    
    public String getTagName()
    {
        return name;
    }

    public double getDoubleValue()
    {
        try
        {
            return Double.parseDouble(value);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0D;
        }
    }
    
    public float getFloatValue()
    {
        try
        {
            return Float.parseFloat(value);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0f;
        }
    }
}
