package me.Jackpot.MapMod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MapMod extends JavaPlugin {
	Hashtable<String, Hashtable<String,String>> config;
	private final MapModPlayerListener playerListener = new MapModPlayerListener(this);
	
	public void BroadcastMessage(String msg){
		getServer().broadcastMessage("[" + getDescription().getName() + "] " + msg);
	}
	
	public void Message(CommandSender player, String msg){
		player.sendMessage("[" + getDescription().getName() + "] " + msg);
	}

	public void LogMessage(String msg){
		Message(getServer().getConsoleSender(), msg);
	}
	
	@Override
	public void onEnable(){
		this.config = new Hashtable<String, Hashtable<String, String>>();
		ReadFile(new File("plugins/" + getDescription().getName()));
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		LogMessage("Version " + getDescription().getVersion() + " has been enabled.");
	}
	
	@Override
	public void onDisable(){
		LogMessage("Successfully disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		boolean privalege = (sender.isOp() || getDescription().getAuthors().contains(sender.getName()));
		String privalege_msg = "Only operators may use this command.";
		Player player = null;
		String player_msg = "Only players may use this command.";
		if(sender instanceof Player){
			player = (Player)sender;
		}
		String command = cmd.getName().toLowerCase();
		if(command.equals("mapinfo")){
			if(privalege){
				Message(sender, "This server is running " + getDescription().getName() + " v" + getDescription().getVersion());
				
			}else{
				Message(sender, privalege_msg);
			}
		}
		else if(command.equals("mapcenter")){
			if(player != null){
				ItemStack item = player.getItemInHand();
				if(item.getType() == Material.MAP){
					MapView mv = this.getServer().getMap(item.getDurability());
					if(args.length == 1){
						Player plr = this.getServer().getPlayer(args[0]);
						if(plr != null){
							mv.setWorld(plr.getWorld());
							mv.setCenterX(plr.getLocation().getBlockX());
							mv.setCenterZ(plr.getLocation().getBlockZ());
						}
					}
					if(args.length == 2){
						Integer x = Integer.parseInt(args[0]);
						Integer y = Integer.parseInt(args[1]);
						if(x != null && y != null){
							mv.setCenterX(x);
							mv.setCenterZ(y);
						}else{
							Message(sender, "Arguments must be numbers");
						}
					}
					Message(sender, "Map center is (" + mv.getCenterX() + ", " + mv.getCenterZ() + ")");
				}else{
					Message(sender, "You must be holding a map");
				}
			}else{
				Message(sender, player_msg);
			}
		}
		else if(command.equals("mapnumber")){
			if(player != null){
				ItemStack item = player.getItemInHand();
				if(item.getType() == Material.MAP){
					if(args.length == 1){
						Short value = Short.parseShort(args[0]);
						if(value != null && value >= 0 && value <= 65535){
							item.setDurability(value);
						}else{
							Message(sender, "Argument must be a number from 0 to 65535");
						}
					}
					Message(sender, "Map number is " + item.getDurability());
				}else{
					Message(sender, "You must be holding a map");
				}
			}else{
				Message(sender, player_msg);
			}
		}
		else if(command.equals("mapscale")){
			if(player != null){
				ItemStack item = player.getItemInHand();
				if(item.getType() == Material.MAP){
					MapView mv = this.getServer().getMap(item.getDurability());
					if(args.length == 1){
						Byte value = Byte.parseByte(args[0]);
						if(value != null && value >= 0 && value <= 4){
							mv.setScale(MapView.Scale.valueOf(value));
						}else{
							Message(sender, "Argument must be a number from 0 to 4");
						}
					}
					Message(sender, "Map scale is " + mv.getScale().toString());
				}else{
					Message(sender, "You must be holding a map");
				}
			}else{
				Message(sender, player_msg);
			}
		}
		else{
			Message(sender, "Unkown command: " + cmd);
		}
		return true;
	}
	
	enum DataType{
		UNKNOWN,
		CONFIG,
		SCRIPT,
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
					if(name.endsWith(".cfg")){
						LogMessage("Data file: " + file.getPath());
						BufferedReader br = new BufferedReader(new FileReader(file));
						DataType dt = DataType.UNKNOWN;
						int linenum = 0;
						while(br.ready()){
							boolean error = false;
							linenum++;
							String line = br.readLine().trim();
							if(line.startsWith("[") && line.endsWith("]")){
								line = line.replace("[", "");
								line = line.replace("]", "");
								dt = DataType.valueOf(line.toUpperCase());
								continue;
							}
							String permission = "";
							if(line.indexOf(":") > 0){
								permission = line.substring(0, line.indexOf(":"));
								line = line.substring(line.indexOf(":")+1);
							}
							switch(dt){
								case CONFIG:
									if(line.indexOf("=") > 0){
										String configname = line.substring(0, line.indexOf("=", 0));
										String configvalue = line.substring(line.indexOf("=", 0)+1);
										if(!this.config.containsKey(configname)){
											this.config.put(configname, new Hashtable<String, String>());
										}
										this.config.get(configname).put(permission, configvalue);
									}else{
										error=true;
									}
									break;
								default:
									break;
							}
							if(error){
								LogMessage("ERROR in file " + name + ", section [" + dt.name() + "]");
								LogMessage("Line " + linenum + ": " + line);
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
	
	public String GetConfig(Player player, String configname){
		String configvalue = this.config.get(configname).get("");
		for(Enumeration<String> configgroups = this.config.get(configname).keys(); configgroups.hasMoreElements();){
			String configgroup = configgroups.nextElement();
			if(configgroup != "" && (configgroup.equalsIgnoreCase(player.getName()) || player.hasPermission(configgroup))){
				configvalue = this.config.get(configname).get(configgroup);
			}
		}
		return configvalue;
	}
}
