package me.Jackpot.NoteMod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class NoteMod extends JavaPlugin {
	String name;
	String datadir;
	HashMap<Object, Stack<Note>> notes = new HashMap<Object, Stack<Note>>();
	Logger log = Logger.getLogger("Minecraft");
	private final NoteModPlayerListener playerListener = new NoteModPlayerListener(this);
	public static PermissionHandler permissionHandler;
	
	public void LogMessage(String msg){
		log.info("[" + name + "] " + msg);
	}
	
	public void Message(Player player, String message){
		player.sendMessage("§c" + message);
	}
	
	public void NoteMessage(Player player, String message, Note note){
		Message(player, message + " (#" + (notes.get(player.getDisplayName()).indexOf(note) + 1) + "):");
		String[] notemsg = note.message.split("\n");
		for(int i = 0; i < notemsg.length; i++){
			player.sendMessage("§7" + notemsg[i]);
		}
	}
	
	@Override
	public void onEnable() {
		name = getDescription().getName();
		datadir = "plugins" + File.separator + name + File.separator;
		try {
			ReadNotes(datadir);
		} catch (IOException e) {
			LogMessage("Error @ ReadNotes: " + e.getMessage());
		}
		setupPermissions();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
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
	
	@Override
	public void onDisable() {
		try {
			SaveNotes(datadir);
			LogMessage("All notes saved.");
		} catch (IOException e) {
			LogMessage("Error @ SaveNotes: " + e.getMessage());
		}
	}
	
	public void ReadNotes(String dir) throws IOException{
		notes.clear();
		BufferedReader br = new BufferedReader(new FileReader(dir + "notes.txt"));
		while(br.ready()){
			String line = br.readLine();
			String id = line.substring(line.indexOf("=")+1, line.indexOf(":"));
			Object key = null;
			if(line.startsWith("Entity")){
				key = Integer.parseInt(id);
			}
			else if(line.startsWith("Player")){
				key = id;
			}
			if(key != null){
				notes.put(key, new Stack<Note>());
				String[] values = line.substring(line.indexOf(":")+1).split(",");
				for(int i = 0; i < values.length; i++){
					String notedata = "";
					BufferedReader notereader = new BufferedReader(new FileReader(dir + id + File.separator + values[i] + ".txt"));
					while(notereader.ready()){
						notedata += notereader.readLine() + "\n";
					}
					notereader.close();
					notes.get(key).push(new Note(notedata));
				}
			}
		}
		br.close();
	}
	
	public void DeleteAll(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(int i = 0; i < files.length; i++){
				DeleteAll(files[i]);
			}
		}
		file.delete();
	}
	
	public void SaveNotes(String dir) throws IOException{
		DeleteAll(new File(dir));
		new File(dir).mkdirs();
		BufferedWriter bw = new BufferedWriter(new FileWriter(dir + "notes.txt"));
		for(Iterator<Object> objs = notes.keySet().iterator(); objs.hasNext();){
			Object obj = objs.next();
			if(notes.get(obj).size() > 0){
				String id = "";
				if(obj instanceof Integer){
					id = ((Integer)obj).toString();
					bw.write("Entity=");
				}
				else if(obj instanceof String){
					id = (String)obj;
					bw.write("Player=");
				}
				if(id.length() > 0){
					bw.write(id + ":");
					new File(datadir + id).mkdir();
					Stack<Note> notelist = notes.get(obj);
					for(int i = 1; i <= notelist.size(); i++){
						bw.write(i + "");
						if(i < notelist.size()){
							bw.write(",");
						}
						BufferedWriter notewriter = new BufferedWriter(new FileWriter(new File(datadir + id + File.separator + i + ".txt")));
						notewriter.write(notelist.get(i-1).message.replace("\n", System.getProperty("line.separator")));
						notewriter.close();
					}
				}
				bw.newLine();
			}
		}
		bw.close();
	}	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("note")){
			if(sender instanceof Player){
				boolean syntax = false;
				Player player = (Player)sender;
				if(args.length > 1){
					if(args[0].equalsIgnoreCase("add")){
						Note note = new Note(args);
						if(lastNote(player.getDisplayName()) < getAmount(player.getInventory(), Material.PAPER)){
								if(note.message.length() > 0){
									pushNote(player.getDisplayName(), note);
									NoteMessage(player, "Added note", note);
								}
						}else{
							Message(player, "You already have the maximum number of notes.");
						}
					}
					else if(args[0].equalsIgnoreCase("del")){
						Integer num = Integer.parseInt(args[1]);
						Note note = getNote(player.getDisplayName(), num); 
						if(note != null){
							NoteMessage(player, "Removed note", note);
							removeNote(player.getDisplayName(), num);
						}else{
							Message(player, "Could not find note (#" + num + ").");
						}
					}
					else if(args[0].equalsIgnoreCase("hold")){
						Integer num = Integer.parseInt(args[1]);
						Note note = getNote(player.getDisplayName(), num);
						if(note != null){
							removeNote(player.getDisplayName(), num);
							pushNote(player.getDisplayName(), note);
							NoteMessage(player, "Moved note (#" + num + ") to top note", note);
						}
					}
					else if(args[0].equalsIgnoreCase("show")){
						Integer num = Integer.parseInt(args[1]);
						Note note = getNote(player.getDisplayName(), num);
						if(note != null){
							NoteMessage(player, "Getting note", note);
						}else{
							Message(player, "Could not find note (#" + num + ").");
						}
					}else{
						syntax = true;
					}
				}else if(args.length > 0){
					if(args[0].equalsIgnoreCase("load")){
						try {
							ReadNotes(datadir);
							Message(player, "Notes loaded.");
						} catch (IOException e) {
							LogMessage("Error @ ReadNotes: " + e.getMessage());
						}
						return true;
					}else if(args[0].equalsIgnoreCase("save")){
						try{
							SaveNotes(datadir);
							Message(player, "Notes saved.");
						}catch (IOException e){
							LogMessage("Error @ SaveNotes: " + e.getMessage());
						}
					}else{
						syntax = true;
					}
				}else{
					syntax = true;
				}
				if(syntax){
					Message(player, "Commands: add [text], del [#], hold [#], show [#].");
				}
			}else{
				LogMessage("Only players may use this command.");
			}
			return true;
		}
		return false;
	}
	
	public int getAmount(Inventory inv, Material m){
		int i = 0;
		HashMap<Integer, ? extends ItemStack> stacks = inv.all(m);
		for(Iterator<Integer> ints = stacks.keySet().iterator(); ints.hasNext();){
			i += inv.getItem(ints.next()).getAmount();
		}
		return i;
	}
	
	public int lastNote(String key){
		if(notes.containsKey(key)){
			return notes.get(key).size();
		}
		return 0;
	}
	
	public Note peekNote(String key){
		if(notes.containsKey(key) && notes.get(key).size() > 0){
			return notes.get(key).peek();
		}
		return null;
	}
	
	public Note getNote(String key, int num){
		if(notes.containsKey(key) && num > 0 && num <= notes.get(key).size()){
			return notes.get(key).get(num-1);
		}
		return null;
	}
	
	public Note removeNote(String key, int num){
		if(notes.containsKey(key) && num > 0 && num <= notes.get(key).size()){
			Note note = notes.get(key).get(num-1);
			notes.get(key).remove(num-1);
			return note;
		}
		return null;
	}
	
	public Note popNote(Object key){
		if(notes.containsKey(key) && !notes.get(key).empty()){
			return notes.get(key).pop();
		}
		return null;
	}
	
	public void pushNote(Object key, Note note){
		if(!notes.containsKey(key)){
			notes.put(key, new Stack<Note>());
		}
		notes.get(key).push(note);
	}
}
