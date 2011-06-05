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

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

public class BoatMod extends JavaPlugin {
	String name;
	Hashtable<String, Hashtable<String,String>> config;
	ArrayList<Material> boatable;
	Hashtable<String,String> scripts;
	Hashtable<Player, String> scriptselect;
	Hashtable<Player, Boat> boats;
	Hashtable<Player, Thread> scriptboats;
	Logger log = Logger.getLogger("Minecraft");
	private final BoatModPlayerListener playerListener = new BoatModPlayerListener(this);
	public static PermissionHandler permissionHandler;
	
	public void LogMessage(String msg){
		log.info("[" + name + "] " + msg);
	}
	
	public void onEnable(){
		name = getDescription().getName();
		config = new Hashtable<String, Hashtable<String,String>>();
		boatable = new ArrayList<Material>();
		scripts = new Hashtable<String,String>();
		scriptselect = new Hashtable<Player, String>();
		boats = new Hashtable<Player, Boat>();
		scriptboats = new Hashtable<Player, Thread>();
		ReadFile(new File("plugins/" + name));
		setupPermissions();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		LogMessage("Version " + this.getDescription().getVersion() + " has been enabled.");
	}
	
	public void setupPermissions(){
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if(permissionHandler == null){
			if(permissionsPlugin != null){
				permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			}else{
				LogMessage("Permissions plugin not detected.");
			}
		}
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
									if(line.indexOf("=") > 0){
										String configgroup = "";
										if(line.indexOf(":") > 0){ 
											configgroup = line.substring(0, line.indexOf(":"));
											line = line.substring(line.indexOf(":")+1);
										}
										String configname = line.substring(0, line.indexOf("=", 0));
										String configvalue = line.substring(line.indexOf("=", 0)+1);
										if(!config.containsKey(configgroup)){
											config.put(configgroup, new Hashtable<String, String>());
										}
										config.get(configgroup).put(configname, configvalue);
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
	
	public String GetConfig(Player player, String configname){
		String configvalue = config.get("").get(configname);
		if(permissionHandler != null){
			for(Enumeration<String> configgroups = config.keys(); configgroups.hasMoreElements();){
				String configgroup = configgroups.nextElement();
				if(configgroup != "" && permissionHandler.permission(player, configgroup)){
					configvalue = config.get(configgroup).get(configname);
				}
			}
		}
		return configvalue;
	}
	
	public int MaxBoatSize(Player player){
		return Integer.parseInt(GetConfig(player, "MaxSize"));
	}
	
	public int MaxBoatSpeed(Player player){
		return Integer.parseInt(GetConfig(player, "MaxSpeed"));
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
