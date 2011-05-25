package me.Jackpot.BoatMod;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.Vector;

public class BoatModPlayerListener extends PlayerListener{
	public static BoatMod plugin;
	public BoatModPlayerListener(BoatMod instance){
		plugin = instance;
	}
	Boat _boat;

	private Vector GetCompassDirection(Vector dir){
		Vector vec = new Vector(0,0,0);
		if(dir.getX() > 0.75){
			vec.setX(1);
		}
		if(dir.getX() < -0.75){
			vec.setX(-1);
		}
		/*
		if(dir.getY() > 0.75){
			vec.setY(1);
		}
		if(dir.getY() < -0.75){
			vec.setY(-1);
		}
		*/
		if(dir.getZ() > 0.75){
			vec.setZ(1);
		}
		if(dir.getZ() < -0.75){
			vec.setZ(-1);
		}
		return vec;
	}

	int movespeed = 2; //this will ensure that it moves as fast as a minecaft does maxxed out.
	
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getMaterial() == Material.BOAT && event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(_boat == null || _boat.getCaptain() == event.getPlayer()){
				_boat = new Boat(event.getClickedBlock(), GetCompassDirection(event.getPlayer().getLocation().getDirection()),
						event.getClickedBlock().getWorld(), event.getPlayer(), plugin);
			}
		}
		if(_boat.getCaptain() == event.getPlayer()){
			if(event.getMaterial() == Material.BOAT && event.getAction() == Action.LEFT_CLICK_AIR){
				_boat.getCaptain().sendMessage("[BoatMod] Your boat has been deboated.");
				_boat.getCaptain().sendMessage("[BoatMod] You have been demoted.");
				_boat = null;
			}
			if(event.getMaterial() == Material.COMPASS){
				if(_boat != null){
					Vector v = event.getPlayer().getLocation().getDirection();
					if(event.getAction() == Action.RIGHT_CLICK_AIR){
						for(int i = 0; i < movespeed; i++){ //it has to be done this way to keep water intact and properly collide
							if(!_boat.MoveBlocks(GetCompassDirection(v))){
								break;
							}
						}
					}
					else if(event.getAction() == Action.LEFT_CLICK_AIR){
						if(movespeed < 16){
							movespeed *= 2;
							_boat.getCaptain().sendMessage("[BoatMod] Moving " + movespeed + " blocks per click.");
						}else{
							movespeed = 1;
							_boat.getCaptain().sendMessage("[BoatMod] Moving " + movespeed + " block per click.");
						}
					}
				}else{
					event.getPlayer().sendMessage("[BoatMod] Please define a boat.");
				}
			}
		}
	}
}
