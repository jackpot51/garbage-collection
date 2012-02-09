package me.Jackpot.BoatMod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class BoatModPlayerListener implements Listener{
	public static BoatMod plugin;
	public BoatModPlayerListener(BoatMod instance){
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getMaterial() == Material.getMaterial(plugin.GetConfig(player, "SetTool"))){
			if(event.getAction() == Action.LEFT_CLICK_BLOCK){
				plugin.AddBoat(player, event.getClickedBlock());
			}else if(event.getAction() == Action.LEFT_CLICK_AIR){
				plugin.RemoveBoat(player);
			}
		}
		if(event.getMaterial() == Material.getMaterial(plugin.GetConfig(player, "MoveTool"))){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				plugin.MoveBoat(player);
			}
			else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
				plugin.RotateBoat(player);
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
