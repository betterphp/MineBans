package com.minebans.pluginInterfaces.coreprotect;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.coreprotect.Config;
import net.coreprotect.CoreProtect;

import org.bukkit.configuration.file.YamlConfiguration;

import com.minebans.MineBans;
import com.minebans.pluginInterfaces.LoggingPluginInterface;

public class CoreProtectPluginIntereface extends LoggingPluginInterface {
	
	private MineBans plugin;
	private CoreProtect coreProtect;
	
	private boolean useMySQL;
	private String tablePrefix;
	
	public CoreProtectPluginIntereface(MineBans plugin){
		this.plugin = plugin;
		this.coreProtect = (CoreProtect) plugin.pluginManager.getPlugin("CoreProtect");
		
		try{
			Field useMySQL = CoreProtect.class.getDeclaredField("use_mysql");
			useMySQL.setAccessible(true);
			
			this.useMySQL = ((Integer) useMySQL.get(this.coreProtect) == 1);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		try{
			Field prefix = CoreProtect.class.getDeclaredField("prefix");
			prefix.setAccessible(true);
			
			this.tablePrefix = (String) prefix.get(this.coreProtect);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean pluginEnabled(){
		return (this.coreProtect != null);
	}
	
	public String getPluginName(){
		return "CoreProtect";
	}
	
	public boolean checkConfig(){
		YamlConfiguration config = new YamlConfiguration();
		
		try{
			config.load(new File("plugins/CoreData/config.yml"));
			
			if (!config.getBoolean("block-place")){
				plugin.log.warn("To provide the best data CoreProtect should be set to log block changes made by players.");
			}
			
			if (!config.getBoolean("item-transactions")){
				plugin.log.warn("To provide the best data CoreProtect should be set to log item transactions made by players.");
			}
		}catch (Exception e){
			plugin.log.fatal("Failed to read CoreProtect config file");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public HashMap<Integer, Integer> getChestAccess(String playerName){
		HashMap<Integer, Integer> taken = new HashMap<Integer, Integer>();
		
		if (this.useMySQL){
			Connection connection = Config.connect(1);
			
			try{
				if (!connection.isClosed()){
					StringBuilder sql = new StringBuilder();
					sql.append("SELECT `type`, `action`, `amount` FROM `" + this.tablePrefix + "containers` ");
					sql.append("WHERE `user` = '" + playerName + "' ");
					sql.append("AND UNIX_TIMESTAMP() - `time` < 86400");
					
					ResultSet result = connection.createStatement().executeQuery(sql.toString());
					
					while (result.next()){
						Integer blockId = result.getInt("type");
						Integer action = result.getInt("action");
						Integer amount = result.getInt("amount");
						
						if (action == 0){
							taken.put(blockId, (taken.containsKey(blockId)) ? taken.get(blockId) - amount :  -amount);
						}else{
							taken.put(blockId, (taken.containsKey(blockId)) ? taken.get(blockId) + amount :  amount);
						}
					}
					
					result.close();
				}
			}catch (SQLException e){
				plugin.log.warn("Failed to get data from CoreProtect (MySQL)");
				e.printStackTrace();
			}
		}else{
			Long minTimestampms = System.currentTimeMillis() - 86400000;
			Long minTimestamp = minTimestampms / 1000L;
			
			File folder = new File("plugins/CoreData/c");
			
			try{
				for (File file : folder.listFiles()){
					if (file.lastModified() > minTimestampms){
						RandomAccessFile raf = new RandomAccessFile(file, "r");
						String line;
						
						while ((line = raf.readLine()) != null){
							String[] parts = line.trim().split(",");
							
							if (Long.parseLong(parts[0]) > minTimestamp && parts[1].equalsIgnoreCase(playerName)){
								Integer blockId = Integer.parseInt(parts[3]);
								Integer amount = Integer.parseInt(parts[4]);
								
								if (parts[14].equals("0")){
									taken.put(blockId, (taken.containsKey(blockId)) ? taken.get(blockId) - amount :  -amount);
								}else{
									taken.put(blockId, (taken.containsKey(blockId)) ? taken.get(blockId) + amount :  amount);
								}
							}
						}
						
						raf.close();
					}
				}
			}catch (Exception e){
				plugin.log.warn("Failed to get data from CoreProtect (Files)");
				e.printStackTrace();
			}
			
		}
		
		return taken;
	}
	
	public HashMap<Integer, Integer> getBlocksPlaced(String playerName){
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		Long minTimestamp = (System.currentTimeMillis() / 1000L) - 86400;
		
		if (this.useMySQL){
			Connection connection = Config.connect(1);
			
			try{
				if (!connection.isClosed()){
					StringBuilder sql = new StringBuilder();
					sql.append("SELECT `type` FROM `" + this.tablePrefix + "blocks` ");
					sql.append("WHERE `user` = '" + playerName + "' ");
					sql.append("AND UNIX_TIMESTAMP() - `time` < 86400 ");
					sql.append("AND `action` = 1");
					
					ResultSet result = connection.createStatement().executeQuery(sql.toString());
					
					while (result.next()){
						Integer blockId = result.getInt("type");
						placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
					}
					
					result.close();
				}
			}catch (SQLException e){
				plugin.log.warn("Failed to get data from CoreProtect (MySQL)");
				e.printStackTrace();
			}
		}else{
			File userFile = new File("plugins/CoreData/p/" + playerName + ".dat");
			
			if (userFile.exists()){
				try{
					RandomAccessFile uraf = new RandomAccessFile(userFile, "r");
					ArrayList<String> chunks = new ArrayList<String>();
					
					String line;
					
					while ((line = uraf.readLine()) != null){
						String[] parts = line.trim().split(",");
						
						if (Long.parseLong(parts[0]) > minTimestamp && !chunks.contains(parts[1])){
							chunks.add(parts[1]);
						}
					}
					
					uraf.close();
					
					for (String chunk : chunks){
						File chunkFile = new File("plugins/CoreData/b/" + chunk + ".dat");
						
						if (chunkFile.exists()){
							RandomAccessFile craf = new RandomAccessFile(chunkFile, "r");
							
							while ((line = craf.readLine()) != null){
								String[] parts = line.split(",");
								
								if (Long.parseLong(parts[0]) > minTimestamp && parts[1].equalsIgnoreCase(playerName) && parts[5].equals("1")){
									Integer blockId = Integer.parseInt(parts[3]);
									placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
								}
							}
							
							craf.close();
						}
					}
				}catch (Exception e){
					plugin.log.warn("Failed to get data from CoreProtect (Files)");
					e.printStackTrace();
				}
			}
		}
		
		return placed;
	}
	
	public HashMap<Integer, Integer> getBlocksBroken(String playerName){
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		Long minTimestamp = (System.currentTimeMillis() / 1000L) - 86400;
		
		if (this.useMySQL){
			Connection connection = Config.connect(1);
			
			try{
				if (!connection.isClosed()){
					StringBuilder sql = new StringBuilder();
					sql.append("SELECT `type` FROM `" + this.tablePrefix + "blocks` ");
					sql.append("WHERE `user` = '" + playerName + "' ");
					sql.append("AND UNIX_TIMESTAMP() - `time` < 86400 ");
					sql.append("AND `action` = 0");
					
					ResultSet result = connection.createStatement().executeQuery(sql.toString());
					
					while (result.next()){
						Integer blockId = result.getInt("type");
						broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
					}
					
					result.close();
				}
			}catch (SQLException e){
				plugin.log.warn("Failed to get data from CoreProtect (MySQL)");
				e.printStackTrace();
			}
		}else{
			File userFile = new File("plugins/CoreData/p/" + playerName + ".dat");
			
			if (userFile.exists()){
				try{
					RandomAccessFile uraf = new RandomAccessFile(userFile, "r");
					ArrayList<String> chunks = new ArrayList<String>();
					
					String line;
					
					while ((line = uraf.readLine()) != null){
						String[] parts = line.trim().split(",");
						
						if (Long.parseLong(parts[0]) > minTimestamp && !chunks.contains(parts[1])){
							chunks.add(parts[1]);
						}
					}
					
					uraf.close();
					
					for (String chunk : chunks){
						File chunkFile = new File("plugins/CoreData/b/" + chunk + ".dat");
						
						if (chunkFile.exists()){
							RandomAccessFile craf = new RandomAccessFile(chunkFile, "r");
							
							while ((line = craf.readLine()) != null){
								String[] parts = line.trim().split(",");
								
								if (Long.parseLong(parts[0]) > minTimestamp && parts[1].equalsIgnoreCase(playerName) && parts[5].equals("0")){
									Integer blockId = Integer.parseInt(parts[3]);
									broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
								}
							}
							
							craf.close();
						}
					}
				}catch (Exception e){
					plugin.log.warn("Failed to get data from CoreProtect (Files)");
					e.printStackTrace();
				}
			}
		}
		
		return broken;
	}
	
	public HashMap<String, HashMap<Integer, Integer>> getBlockChanges(String playerName){
		HashMap<Integer, Integer> broken = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> placed = new HashMap<Integer, Integer>();
		
		Long minTimestamp = (System.currentTimeMillis() / 1000L) - 86400;
		
		if (this.useMySQL){
			Connection connection = Config.connect(1);
			
			try{
				if (!connection.isClosed()){
					StringBuilder sql = new StringBuilder();
					sql.append("SELECT `type`, `action` FROM `" + this.tablePrefix + "blocks` ");
					sql.append("WHERE `user` = '" + playerName + "' ");
					sql.append("AND UNIX_TIMESTAMP() - `time` < 86400");
					
					ResultSet result = connection.createStatement().executeQuery(sql.toString());
					
					while (result.next()){
						Integer blockId = result.getInt("type");
						Integer action = result.getInt("action");
						
						if (action == 0){
							broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
						}else{
							placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
						}
					}
					
					result.close();
				}
			}catch (SQLException e){
				plugin.log.warn("Failed to get data from CoreProtect (MySQL)");
				e.printStackTrace();
			}
		}else{
			File userFile = new File("plugins/CoreData/p/" + playerName + ".dat");
			
			if (userFile.exists()){
				try{
					RandomAccessFile uraf = new RandomAccessFile(userFile, "r");
					ArrayList<String> chunks = new ArrayList<String>();
					
					String line;
					
					while ((line = uraf.readLine()) != null){
						String[] parts = line.trim().split(",");
						
						if (Long.parseLong(parts[0]) > minTimestamp && !chunks.contains(parts[1])){
							chunks.add(parts[1]);
						}
					}
					
					uraf.close();
					
					for (String chunk : chunks){
						File chunkFile = new File("plugins/CoreData/b/" + chunk + ".dat");
						
						if (chunkFile.exists()){
							RandomAccessFile craf = new RandomAccessFile(chunkFile, "r");
							
							while ((line = craf.readLine()) != null){
								String[] parts = line.trim().split(",");
								
								if (Long.parseLong(parts[0]) > minTimestamp && parts[1].equalsIgnoreCase(playerName)){
									Integer blockId = Integer.parseInt(parts[3]);
									
									if (parts[5].equals("0")){
										broken.put(blockId, (broken.containsKey(blockId)) ? broken.get(blockId) + 1 : 1);
									}else{
										placed.put(blockId, (placed.containsKey(blockId)) ? placed.get(blockId) + 1 : 1);
									}
								}
							}
							
							craf.close();
						}
					}
				}catch (Exception e){
					plugin.log.warn("Failed to get data from CoreProtect (Files)");
					e.printStackTrace();
				}
			}
		}
		
		HashMap<String, HashMap<Integer, Integer>> data = new HashMap<String, HashMap<Integer, Integer>>();
		
		data.put("broken", broken);
		data.put("placed", placed);
		
		return data;
	}
	
}
