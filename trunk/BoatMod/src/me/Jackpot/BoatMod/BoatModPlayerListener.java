package me.Jackpot.BoatMod;

import java.util.Hashtable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.Vector;

public class BoatModPlayerListener extends PlayerListener{
	public static BoatMod plugin;
	Hashtable<Player, Boat> _boats;
	public BoatModPlayerListener(BoatMod instance){
		plugin = instance;
		_boats = new Hashtable<Player, Boat>();
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

	int movespeed = 2; //this will ensure that it moves as fast as a minecaft does maxxed out.
	
	private Boat getBoat(Player captain){
		if(_boats.containsKey(captain)){
			return _boats.get(captain);
		}else{
			return null;
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Boat boat = getBoat(player);
		if(event.getMaterial() == Material.BOAT && event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(boat != null){
				_boats.remove(player);
			}
			_boats.put(player, new Boat(event.getClickedBlock(), event.getClickedBlock().getWorld(), player, plugin));
		}
		if(boat != null){
			if(event.getMaterial() == Material.BOAT && event.getAction() == Action.LEFT_CLICK_AIR){
				_boats.remove(event.getPlayer());
				player.sendMessage("[BoatMod] Your boat has been deboated.");
				player.sendMessage("[BoatMod] You have been demoted.");
			}
			if(event.getMaterial() == Material.COMPASS){
				Vector v = player.getLocation().getDirection();
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
					boat.Move(GetCompassDirection(v));
				}
				else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
					boat.changeSpeed();
				}
			}
		}
	}
}
