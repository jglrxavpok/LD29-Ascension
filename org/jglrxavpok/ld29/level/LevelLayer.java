package org.jglrxavpok.ld29.level;

public abstract class LevelLayer
{

	private String layerName;

	public LevelLayer(String layerName)
	{
		this.layerName = layerName;
	}
	
	public String getName()
	{
		return layerName;
	}

}
