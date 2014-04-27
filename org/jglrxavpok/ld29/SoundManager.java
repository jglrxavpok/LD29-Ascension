package org.jglrxavpok.ld29;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

public class SoundManager
{

	public static final SoundManager instance = new SoundManager();
	private HashMap<String, ArrayList<String>> idMap = new HashMap<String, ArrayList<String>>();
	private SoundSystem sndSystem; 
	
	private SoundManager()
	{
		try
		{
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setDefaultFadeDistance(100);

			sndSystem = new SoundSystem();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		registerSound("/assets/sounds/sfx/Jump", "wav", "jump");
		registerSound("/assets/sounds/sfx/Coin", "wav", "coin");
	}
	
	public void playSoundFX(String sound, float volume, float pitch)
	{
		sndSystem.newStreamingSource(false, sound, SoundManager.class.getResource(sound), sound.substring(sound.lastIndexOf(".")+1), false, 0, 0, 0, 0, 0);
		sndSystem.setVolume(sound, volume);
		sndSystem.setPitch(sound, pitch);
		sndSystem.play(sound);
	}

	
	public void playSound(String soundID)
	{
		String filename = soundID;
		if(idMap.containsKey(soundID))
		{
			ArrayList<String> list = idMap.get(soundID);
			filename = list.get(sndSystem.randomNumberGenerator.nextInt(list.size()));
		}
		else
			throw new IllegalArgumentException("No sound registred with id: "+soundID);
		playSoundFX(filename,0.25f,1);
	}
	
	/**
	 * Can register multiple sound files
	 * @param filepath
	 * @param soundID
	 */
	public void registerSound(String filepath, String audioType, String soundID)
	{
		String start = filepath;
		ArrayList<String> paths = new ArrayList<String>(); 
		try
		{
			InputStream in = SoundManager.class.getResourceAsStream(start+"."+audioType.toLowerCase());
			if(in != null && in.available() != 0)
			{
				paths.add(filepath+"."+audioType.toLowerCase());
				System.out.println("Registred sound "+filepath+"."+audioType.toLowerCase()+" at id "+soundID);
			}
		}
		catch(Exception e)
		{
			;
		}
		
		InputStream in = null;
		try
		{
			int index = 0;
			do
			{
				if(index != 0)
				{
					paths.add(start+(index)+"."+audioType.toLowerCase());
					System.out.println("Registred sound "+(start+(index)+"."+audioType.toLowerCase())+" at id "+soundID);
				}
				in = SoundManager.class.getResourceAsStream(start+(++index)+"."+audioType.toLowerCase());
			}
			while(in != null && (in.available() != 0));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		idMap.put(soundID, paths);
	}
}
