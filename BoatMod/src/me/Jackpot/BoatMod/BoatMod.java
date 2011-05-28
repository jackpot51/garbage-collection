package me.Jackpot.BoatMod;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BoatMod extends JavaPlugin {
	private final BoatModPlayerListener playerListener = new BoatModPlayerListener(this);
	Hashtable<Player, Boat> _boats;
	Logger log = Logger.getLogger("Minecraft");
	public void onEnable(){
		PluginManager pm = this.getServer().getPluginManager();
		_boats = new Hashtable<Player, Boat>();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.High, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		log.info("BoatMod has been enabled.");
	}
	public void onDisable(){
		for(Enumeration<Player> players = _boats.keys(); players.hasMoreElements();){
			RemoveBoat(players.nextElement());
		}
		log.info("BoatMod has been disabled.");
	}
	
	public void Message(Player player, String message){
		player.sendMessage("§2[BoatMod] §c" + message);
	}
	
	private Boat getBoat(Player captain){
		if(_boats.containsKey(captain)){
			return _boats.get(captain);
		}else{
			return null;
		}
	}
	
	private Vector GetCompassDirection(Vector dir){
		Vector vec = new Vector(0,0,0);
		if(dir.getX() > 0.75){
			vec.setX(1);
		}
		if(dir.getX() < -0.75){
			vec.setX(-1);
		}

		if(dir.getY() > 0.75){
			vec.setY(1);
		}
		if(dir.getY() < -0.75){
			vec.setY(-1);
		}

		if(dir.getZ() > 0.75){
			vec.setZ(1);
		}
		if(dir.getZ() < -0.75){
			vec.setZ(-1);
		}
		return vec;
	}
	
	public int MaxBoatSize(Player player){
		if(player.isOp()){
			return 16384;
		}else{
			return 2048;
		}
	}
	
	public void AddBoat(Player player, Block block){
		Boat boat = getBoat(player);
		if(boat != null){
			_boats.remove(player);
		}
		boat = new Boat(block, player, this);
		_boats.put(player, boat);
		log.info("[BoatMod] " + player.getDisplayName() + " created a boat of size " + boat._vectors.size() + " blocks.");
	}
	
	public void RemoveBoat(Player player){
		Boat boat = getBoat(player);
		if(boat != null){
			_boats.remove(player);
			Message(player, "Your boat has been deboated.");
			Message(player, "You have been demoted.");
			log.info("[BoatMod] " + player.getDisplayName() + "'s boat was removed.");
		}
	}
	
	public void MoveBoat(Player player){
		Boat boat = getBoat(player);
		Vector v = player.getLocation().getDirection();
		boat.Move(GetCompassDirection(v));
	}
	
	public void ChangeSpeed(Player player){
		Boat boat = getBoat(player);
		boat.changeSpeed();
	}
}
