package me.Jackpot.BoatMod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class BoatModPlayerListener extends PlayerListener{
	public static BoatMod plugin;
	public BoatModPlayerListener(BoatMod instance){
		plugin = instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getMaterial() == Material.BOAT){
			if(event.getAction() == Action.LEFT_CLICK_BLOCK){
				plugin.AddBoat(player, event.getClickedBlock());
			}else if(event.getAction() == Action.LEFT_CLICK_AIR){
				plugin.RemoveBoat(player);
			}
		}
		else if(event.getMaterial() == Material.MAP){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				plugin.MoveBoat(player);
			}
			else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
				plugin.ChangeSpeed(player);
			}
		}
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		plugin.RemoveBoat(event.getPlayer());
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		plugin.RemoveBoat(event.getPlayer());
	}
	
	public void onPlayerRespawn(PlayerRespawnEvent event){
		plugin.RemoveBoat(event.getPlayer());
	}
}
