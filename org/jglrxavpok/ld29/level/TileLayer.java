package org.jglrxavpok.ld29.level;

public class TileLayer extends LevelLayer
{

	private int[][] tiles;
	private int width;
	private int height;

	public TileLayer(int[][] tiles, String layerName)
	{
		super(layerName);
		this.tiles = tiles;
		width = tiles.length;
		height = tiles[0].length;
	}
	
	
	public void setTile(int x, int y, int tile)
	{
		if(x < 0 || y < 0 || x >= width || y >= height)
			return;
		tiles[x][y] = tile;
	}
	
	public int getTile(int x, int y)
	{
		if(x < 0 || y < 0 || x >= width || y >= height)
			return -1;
		return tiles[x][y];
	}
	
	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
	
}
