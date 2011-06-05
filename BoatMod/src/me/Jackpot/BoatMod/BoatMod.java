package me.Jackpot.BoatMod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatMod extends JavaPlugin {
	Hashtable<String, String> config;
	ArrayList<Material> boatable;
	Hashtable<String,String> scripts;
	Hashtable<Player, String> scriptselect;
	private final BoatModPlayerListener playerListener = new BoatModPlayerListener(this);
	Hashtable<Player, Boat> boats;
	Hashtable<Player, Thread> scriptboats;
	Logger log = Logger.getLogger("Minecraft");
	String name;
	
	public void LogMessage(String msg){
		log.info("[" + name + "] " + msg);
	}
	
	public void onEnable(){
		PluginManager pm = this.getServer().getPluginManager();
		name = getDescription().getName();
		config = new Hashtable<String, String>();
		boatable = new ArrayList<Material>();
		scripts = new Hashtable<String,String>();
		scriptselect = new Hashtable<Player, String>();
		boats = new Hashtable<Player, Boat>();
		scriptboats = new Hashtable<Player, Thread>();
		ReadFile(new File("plugins/" + name));
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		LogMessage("Version " + this.getDescription().getVersion() + " has been enabled.");
	}
	public void onDisable(){
		for(Enumeration<Player> players = boats.keys(); players.hasMoreElements();){
			RemoveBoat(players.nextElement());
		}
		LogMessage("Successfully disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("boatmod")){
			if(args.length == 1 && scripts.containsKey(args[0])){
				if(sender instanceof Player){
					if(sender.isOp()){
						scriptselect.put((Player)sender, args[0]);
						Message((Player)sender, "Click a boat to apply the " + args[0] + " script to it.");
					}else{
						Message((Player)sender, "You do not have access to the boatmod command.");
					}
					
				}else{
					LogMessage("Only players may use this command.");
				}
				return true;
			}
		}
		return false;
	}
	
	enum DataType{
		UNKNOWN,
		CONFIG,
		MATERIAL
	}
	
	public void ReadFile(File file){
		if(file != null){
			if(file.isDirectory()){
				File[] files = file.listFiles();
				for(int i = 0; i < files.length; i++){
					ReadFile(files[i]);
				}
			}else if(file.isFile()){
				try {
					String name = file.getName();
					if(name.endsWith(".js")){
						String scriptname = name.substring(0, name.indexOf(".js"));
						LogMessage("Script: " + scriptname + " @ " + file.getPath());
						scripts.put(scriptname, file.getPath());
					}else{
						LogMessage("Data file: " + file.getPath());
						BufferedReader br = new BufferedReader(new FileReader(file));
						DataType dt = DataType.UNKNOWN;
						while(br.ready()){
							String line = br.readLine().trim();
							if(line.startsWith("[") && line.endsWith("]")){
								line = line.replace("[", "");
								line = line.replace("]", "");
								dt = DataType.UNKNOWN;
							}
							switch(dt){
								case CONFIG:
									int seperator = line.indexOf("=", 0);
									if(seperator >0){
										String configname = line.substring(0, seperator);
										String configvalue = line.substring(seperator+1);
										config.put(configname, configvalue);
									}else{
										LogMessage("Config data error: " + line);
									}
									break;
								case MATERIAL:
									if(Material.valueOf(line.toUpperCase()) != null){
										boatable.add(Material.valueOf(line));
									}else{
										LogMessage("Material data error: " + line);
									}
									break;
								case UNKNOWN:
									if(DataType.valueOf(line.toUpperCase()) != null){
										dt = DataType.valueOf(line.toUpperCase());
									}
									break;
							}
						}
						br.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void Message(Player player, String message){
		player.sendMessage("§2[" + name + "] §c" + message);
	}
	
	private Boat getBoat(Player captain){
		if(boats.containsKey(captain)){
			return boats.get(captain);
		}else{
			return null;
		}
	}
	
	public int MaxBoatSize(Player player){
		int maxsize;
		if(player.isOp()){
			maxsize = Integer.parseInt(config.get("MaxSizeOp"));
		}else{
			maxsize = Integer.parseInt(config.get("MaxSizePlayer"));
		}
		return maxsize;
	}
	
	public int MaxBoatSpeed(Player player){
		return Integer.parseInt(config.get("MaxSpeed"));
	}
	
	public boolean CheckBoatable(Material m){
		return boatable.contains(m);
	}
	
	public void AddBoat(Player player, Block block){
		Boat boat = getBoat(player);
		if(boat != null){
			boats.remove(player);
		}
		if(scriptboats.containsKey(player)){
			Thread boatthread = scriptboats.get(player);
			boatthread.interrupt();
			scriptboats.remove(player);
		}
		if(scriptselect.containsKey(player)){
			ScriptBoat sboat = new ScriptBoat(block, player, this, scripts.get(scriptselect.get(player)));
			Thread boatthread = new Thread(sboat);
			scriptboats.put(player, boatthread);
			boatthread.start();
			boat = sboat;
		}else{
			boat = new Boat(block, player, this);
			boats.put(player, boat);
			Message(player, "You now have control of " + (boat._vectors.size()) + " blocks.");
		}
		LogMessage(player.getDisplayName() + " created a boat of size " + boat._vectors.size() + " blocks.");
	}
	
	public void RemoveBoat(Player player){
		Boat boat = getBoat(player);
		if(boat != null){
			boats.remove(player);
			Message(player, "Your boat has been deboated.");
			LogMessage(player.getDisplayName() + "'s boat was removed.");
		}
		if(scriptboats.containsKey(player)){
			Thread boatthread = scriptboats.get(player);
			boatthread.interrupt();
			scriptboats.remove(player);
		}
		if(scriptselect.containsKey(player)){
			Message(player, "The " + scriptselect.get(player) + " script will no longer be applied.");
			scriptselect.remove(player);
			LogMessage(player.getDisplayName() + "'s script was removed.");
		}
	}
	
	public void MoveBoat(Player player){
		Boat boat = getBoat(player);
		if(boat != null){
			boat.Move(player.getLocation().getDirection());
		}
	}
	
	public void ChangeSpeed(Player player){
		Boat boat = getBoat(player);
		if(boat != null){
			boat.changeSpeed();
		}
	}
}
