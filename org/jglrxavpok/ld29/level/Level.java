package org.jglrxavpok.ld29.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jglrxavpok.ld29.GameStart;
import org.jglrxavpok.ld29.entity.Entity;
import org.jglrxavpok.ld29.entity.EntityPlayer;
import org.jglrxavpok.ld29.render.Camera2D;
import org.jglrxavpok.ld29.utils.PipeNetwork;
import org.jglrxavpok.opengl.Tessellator;
import org.jglrxavpok.opengl.Textures;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

public class Level
{

	private int width;
	private int height;
	private ArrayList<LevelLayer> layers = new ArrayList<LevelLayer>();
	private Tileset tileset;
	public float gravity = 0.85f;
	private Tileset entitiesTileset;
	private Camera2D camera;
	public boolean renderAndUpdateEntities = true;

	public Level(String jsonData)
	{
		parse(jsonData);
	}

	@SuppressWarnings("unchecked")
	private void parse(String jsonData)
	{
		JSONObject obj = new JSONObject(jsonData);
		this.width = obj.getInt("width");
		this.height = obj.getInt("height");
		JSONArray tilesets = obj.getJSONArray("tilesets");
		this.tileset = new Tileset(tilesets.getJSONObject(0));
		this.entitiesTileset = new Tileset(tilesets.getJSONObject(1));
		
		JSONArray array = obj.getJSONArray("layers");
		for(int i = 0;i<array.length();i++)
		{
			JSONObject layer = array.getJSONObject(i);
			String layerName = layer.getString("name");
			String layerType = layer.getString("type");
			if(layerType.equals("tilelayer"))
			{
				int[][] tiles = new int[width][height];
				JSONArray tileData = layer.getJSONArray("data");
				for(int x= 0;x<tiles.length;x++)
				{
					for(int y= 0;y<tiles[0].length;y++)
					{
						tiles[x][y] = tileData.getInt(x+y*tiles.length);
					}
				}
				TileLayer layerObj = new TileLayer(tiles, layerName);
				layers.add(layerObj);
			}
			else if(layerType.equals("objectgroup"))
			{
				EntitiesLayer layerObj = new EntitiesLayer(layerName);
				JSONArray objects = layer.getJSONArray("objects");
				for(int index = 0;index<objects.length();index++)
				{
					JSONObject o = objects.getJSONObject(index);
					Entity ent = Entity.createFromID(this, i, o.getInt("gid")-entitiesTileset.getTileStart());
					if(ent != null)
					{
						ent.x = o.getInt("x");
						ent.y = o.getInt("y")-ent.h;
						if(o.has("properties"))
						{
							JSONObject props = o.getJSONObject("properties");
							Iterator<String> it = props.keys();
							HashMap<String, String> map = new HashMap<String, String>();
							while(it.hasNext())
							{
								String key = it.next();
								map.put(key, props.getString(key));
							}
							ent.setExtraProps(map);
						}
						if(o.has("name"))
						{
							String n = o.getString("name");
							if(n.length() > 0)
							{
								ent.setName(n);
							}
						}
						layerObj.addEntity(ent);
					}
				}
				layers.add(layerObj);
			}
		}
	}
	
	public void update()
	{
		for(int i = 0;i<layers.size();i++)
		{
			LevelLayer layer = layers.get(i);
			if(layer instanceof TileLayer)
			{
				// TODO: Update tiles ?
			}
			else if(layer instanceof EntitiesLayer && renderAndUpdateEntities)
			{
				ArrayList<Entity> ents = ((EntitiesLayer) layer).getEntities();
				for(int index = 0;index<ents.size();index++)
				{
					ents.get(index).update();
				}
			}
		}
		PipeNetwork.updateAll();
	}
	
	public void render()
	{
		GL11.glPushMatrix();
		if(camera != null)
			camera.apply();
		float xoffset = camera.getX();
		float yoffset = camera.getY();
		for(int i = 0;i<layers.size();i++)
		{
			
			LevelLayer layer = layers.get(i);
			if(layer instanceof TileLayer)
			{
				Textures.bind("/assets/textures/tilesets/"+getTileset().getName().toLowerCase()+".png");
				Tessellator.instance.startDrawingQuads();
				TileLayer current = (TileLayer)layer;
				for(int y = (int)((-(yoffset))/16f);y<(int)((-(yoffset))/16f)+height;y++)
				{
					for(int x = (int)((-(xoffset))/16f);x<(int)((-(xoffset))/16f)+width;x++)
					{
						if(!inFrustum(x*16f,y*16f))
							continue;
						int tile = current.getTile(x,y);
						if(tile == 72)
							continue;
						if(tile == 0)
							continue;
						tile-=1;
						int tileX = tile % (getTileset().getImgWidth()/getTileset().getTileWidth());
						int tileY = getTileset().getImgHeight()/getTileset().getTileHeight() - tile / (getTileset().getImgWidth()/getTileset().getTileWidth());
						double minU = ((double)getTileset().getTileWidth()*((double)tileX)/(double)getTileset().getImgWidth());
						double minV = ((double)getTileset().getTileHeight()*((double)tileY)/(double)getTileset().getImgHeight());
						double maxU = ((double)getTileset().getTileWidth()*((double)tileX+1.0)/(double)getTileset().getImgWidth());
						double maxV = ((double)getTileset().getTileHeight()*((double)tileY-1.0)/(double)getTileset().getImgHeight());
						Tessellator.instance.addVertexWithUV((double)x*(double)getTileset().getTileWidth(), (double)y*(double)getTileset().getTileHeight(), 0, minU, minV);
						Tessellator.instance.addVertexWithUV((double)(x)*(double)getTileset().getTileWidth()+(double)getTileset().getTileWidth(), (double)y*(double)getTileset().getTileHeight(), 0, maxU, minV);
						Tessellator.instance.addVertexWithUV((double)(x)*(double)getTileset().getTileWidth()+(double)getTileset().getTileWidth(), (double)(y)*(double)getTileset().getTileHeight()+(double)getTileset().getTileHeight(), 0, maxU, maxV);
						Tessellator.instance.addVertexWithUV((double)x*(double)getTileset().getTileWidth(), (double)(y)*(double)getTileset().getTileHeight()+(double)getTileset().getTileHeight(), 0, minU, maxV);
					}
				}
				Tessellator.instance.flush();
			}
			else if(layer instanceof EntitiesLayer && renderAndUpdateEntities)
			{
				ArrayList<Entity> ents = ((EntitiesLayer) layer).getEntities();
				for(int index = 0;index<ents.size();index++)
				{
					ents.get(index).render();
				}
			}
		}
		if(camera != null)
			camera.unapply();
		GL11.glPopMatrix();
	}
	
	private boolean inFrustum(float x1, float y1)
	{
		float x = 0;
		float y = 0;
		if(camera != null)
		{
			x1 += camera.getX();
			y1 += camera.getY();
		}
		
		return x1 >= -16 && y1 >= -16 && x1 <= GameStart.WIDTH/GameStart.ZOOM && y1 <= GameStart.HEIGHT/GameStart.ZOOM;
	}

	public EntitiesLayer getFirstEntitiesLayer()
	{
		for(LevelLayer l : layers)
		{
			if(l instanceof EntitiesLayer)
				return (EntitiesLayer) l;
		}
		return null;
	}
	
	public int getTile(int x, int y, int layerID)
	{
		if(layers.get(layerID) instanceof TileLayer)
		return ((TileLayer) layers.get(layerID)).getTile(x,y);
		
		return -1;
	}
	
	public boolean isSolid(int x, int y, int layerID)
	{
		if(x < 0 || y < 0 || x>= width || y >= height)
			return true;
		if(layerID <= -1)
		{
			for(LevelLayer layer : layers)
				if(layer != null && layer instanceof TileLayer)
				{
					int tile = ((TileLayer) layer).getTile(x,y);
					if(tile == -1)
						return true;
					if(getTileset().hasTerrain("Solid"))
					{
						if(getTileset().hasTileTerrain(tile, "Solid"))
							return true;
					}
				}
			return false;
		}
		else
		{
			LevelLayer layer = layers.get(layerID);
			if(layer != null && layer instanceof TileLayer)
			{
				int tile = ((TileLayer) layer).getTile(x,y);
				if(tile == -1)
					return true;
				if(getTileset().hasTerrain("Solid"))
				{
					return getTileset().hasTileTerrain(tile, "Solid");
				}
				else
					return false;
			}
			else
				return false;
		}
	}
	
	public void setTileset(Tileset t)
	{
		tileset = t;
	}

	public Tileset getTileset()
	{
		return tileset;
	}
	
	public void setCamera(Camera2D cam)
	{
		this.camera = cam;
	}
	
	public Camera2D getCamera()
	{
		return camera;
	}

	public LevelLayer getLayer(int layer)
	{
		return layers.get(layer);
	}

	public boolean hasTile(int i, int j, int k)
	{
		for(LevelLayer layer : layers)
			if(layer != null && layer instanceof TileLayer)
			{
				if(((TileLayer) layer).getTile(i,j) == k)
					return true;
			}
		return false;
	}

	public EntityPlayer getFirstPlayer()
	{
		for(Entity e : getFirstEntitiesLayer().getEntities())
			if(e instanceof EntityPlayer)
				return (EntityPlayer) e;
		return null;
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
