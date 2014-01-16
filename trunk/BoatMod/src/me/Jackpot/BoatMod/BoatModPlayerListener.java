package me.Jackpot.BoatMod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BoatModPlayerListener implements Listener{
	public static BoatMod plugin;
	public BoatModPlayerListener(BoatMod instance){
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getMaterial() == Material.getMaterial(plugin.GetConfig(player, "SetTool"))){
			if(event.getAction() == Action.LEFT_CLICK_BLOCK){
				event.setCancelled(true);
				if(plugin.getDescription().getAuthors().contains(player.getName())){
					plugin.BroadcastMessage("Clicked on " + event.getClickedBlock().toString());
				}
				plugin.AddBoat(player, event.getClickedBlock());
			}else if(event.getAction() == Action.LEFT_CLICK_AIR){
				event.setCancelled(true);
				plugin.RemoveBoat(player);
			}
		}
		if(event.getMaterial() == Material.getMaterial(plugin.GetConfig(player, "MoveTool"))){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				event.setCancelled(true);
				plugin.MoveBoat(player);
			}
			else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
				event.setCancelled(true);
				plugin.RotateBoat(player);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public static void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(plugin.getDescription().getAuthors().contains(player.getName())){
			plugin.BroadcastMessage("The creator of " + plugin.getDescription().getName() + ", " + player.getName() + " has arrived!");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public static void onPlayerKick(PlayerKickEvent event){
		plugin.RemoveBoat(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public static void onPlayerQuit(PlayerQuitEvent event){
		plugin.RemoveBoat(event.getPlayer());
	}
}
