package org.jglrxavpok.ld29.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class Tileset
{

	public static class Terrain
	{

		public String name;
		public int id;
		
		public Terrain(String name, int i)
		{
			this.name = name;
			this.id = i;
		}

	}

	private String name;
	private int imgW;
	private int imgH;
	private int tileW;
	private int tileH;
	private ArrayList<Terrain> terrains = new ArrayList<Terrain>();
	private HashMap<Integer, int[]> tilesTerrains = new HashMap<Integer, int[]>();
	private int tileStart;

	@SuppressWarnings("unchecked")
	public Tileset(JSONObject json)
	{
		this.name = json.getString("name");
		this.imgW = json.getInt("imagewidth");
		this.imgH = json.getInt("imageheight");
		this.tileW = json.getInt("tilewidth");
		this.tileH = json.getInt("tileheight");
		this.tileStart = json.getInt("firstgid");
		if(json.has("terrains"))
		{
			JSONArray terrainsArray = json.getJSONArray("terrains");
			for(int i = 0;i<terrainsArray.length();i++)
			{
				JSONObject terrainObj = terrainsArray.getJSONObject(i);
				String name = terrainObj.getString("name");
				terrains.add(new Terrain(name, i));
			}
			if(json.has("tiles"))
			{
				JSONObject obj = json.getJSONObject("tiles");
				Iterator<String> keys = obj.keys();
				while(keys.hasNext())
				{
					String key = keys.next();
					try
					{
						int tile = Integer.parseInt(key);
						JSONArray tileTerrains = obj.getJSONObject(key).getJSONArray("terrain");
						int[] terrains = new int[tileTerrains.length()];
						for(int i = 0;i<tileTerrains.length();i++)
						{
							terrains[i] = tileTerrains.getInt(i);
						}
						tilesTerrains.put(tile, terrains);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	public int getTileStart()
	{
		return tileStart;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getImgWidth()
	{
		return imgW;
	}
	
	public int getImgHeight()
	{
		return imgH;
	}

	public int getTileWidth()
	{
		return tileW;
	}
	
	public int getTileHeight()
	{
		return tileH;
	}

	public boolean hasTerrain(String string)
	{
		for(Terrain t : terrains)
			if(t.name.equals(string))
				return true;
		return false;
	}

	public boolean hasTileTerrain(int tile, String string)
	{
		Terrain target = null;
		if(tile == -1)
			return true;
		if(tile == 0)
			return false;
		tile-=1;
		for(Terrain t : terrains)
			if(t.name.equals(string))
			{
				target =t;
				break;
			}
		if(target == null)
			return false;
		int id = target.id;
		int[] array = tilesTerrains.get(tile);
		if(array != null)
		{
			for(int terrain : array)
			{
				if(terrain == id)
					return true;
			}
		}
		return false;
	}
}
