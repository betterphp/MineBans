package com.minebans.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PlayerListStore {
	
	private File storageFile;
	private ArrayList<String> playerNames;
	
	public PlayerListStore(File file){
		this.storageFile = file;
		this.playerNames = new ArrayList<String>();
		
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
			
			String line, playerName;
			
			while ((line = reader.readLine()) != null){
				playerName = line.toLowerCase();
				
				if (!this.playerNames.contains(playerName)){
					this.playerNames.add(playerName);
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
			
			for (String playerName : this.playerNames){
				out.write(playerName);
				out.newLine();
			}
			
			out.close();
			stream.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean contains(String playerName){
		return this.playerNames.contains(playerName.toLowerCase());
	}
	
	public void add(String playerName){
		playerName = playerName.toLowerCase();
		
		if (!this.playerNames.contains(playerName)){
			this.playerNames.add(playerName);
		}
	}
	
	public void remove(String playerName){
		this.playerNames.remove(playerName.toLowerCase());
	}
	
	public ArrayList<String> getPlayerNames(){
		return this.playerNames;
	}
	
}
