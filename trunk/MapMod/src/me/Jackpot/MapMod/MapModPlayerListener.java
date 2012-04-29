package me.Jackpot.MapMod;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MapModPlayerListener implements Listener{
	public static MapMod plugin;
	public MapModPlayerListener(MapMod instance){
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public static void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(plugin.getDescription().getAuthors().contains(player.getName())){
			plugin.BroadcastMessage("The creator of " + plugin.getDescription().getName() + ", " + player.getName() + " has arrived!");
		}
	}

 	/*
 	@EventHandler(priority = EventPriority.HIGHEST)
	public static void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		if(item.getType() == Material.MAP){
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
				MapView mv = plugin.getServer().getMap(item.getDurability());
				byte scale = (byte) (mv.getScale().getValue()+1);
				if(scale > 4){
					scale = 0;
				}
				mv.setScale(MapView.Scale.valueOf(scale));
			}
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				MapView mv = plugin.getServer().getMap(item.getDurability());
				mv.setWorld(player.getWorld());
				mv.setCenterX(player.getLocation().getBlockX());
				mv.setCenterZ(player.getLocation().getBlockZ());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public static void onPlayerKick(PlayerKickEvent event){
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public static void onPlayerQuit(PlayerQuitEvent event){
	}
	*/
}
