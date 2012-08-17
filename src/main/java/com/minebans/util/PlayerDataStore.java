package com.minebans.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

public class PlayerDataStore {
	
	private File storageFile;
	private HashMap<String, String> playerData;
	
	public PlayerDataStore(File file){
		this.storageFile = file;
		this.playerData = new HashMap<String, String>();
		
		if (!this.storageFile.exists()){
			try{
				this.storageFile.createNewFile();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void load(){
		try{
			DataInputStream input = new DataInputStream(new FileInputStream(this.storageFile));
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			String line, playerName, playerData;
			String info[];
			
			while ((line = reader.readLine()) != null){
				info = line.split(":");
				playerName = info[0];
				playerData = info[1];
				
				if (this.playerData.containsKey(playerName) == false){
					this.playerData.put(playerName, playerData);
				}
			}
			
			reader.close();
			input.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void save(){
		try{
			FileWriter stream = new FileWriter(this.storageFile);
			BufferedWriter out = new BufferedWriter(stream);
			
			for (String playerName : this.playerData.keySet()){
				out.write(playerName + ":" + this.playerData.get(playerName));
				out.newLine();
			}
			
			out.close();
			stream.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean contains(String playerName){
		return this.playerData.containsKey(playerName.toLowerCase());
	}
	
	public void add(String playerName, String playerData){
		playerName = playerName.toLowerCase();
		
		if (!this.playerData.containsKey(playerName)){
			this.playerData.put(playerName, playerData);
		}
	}
	
	public void remove(String playerName){
		this.playerData.remove(playerName.toLowerCase());
	}
	
	public Set<String> getPlayerNames(){
		return this.playerData.keySet();
	}
	
	
	public String getData(String playerName){
		playerName = playerName.toLowerCase();
		
		if (!this.playerData.containsKey(playerName)){
			return "";
		}
		
		return this.playerData.get(playerName);
	}
	
}