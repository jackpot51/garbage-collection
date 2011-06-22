package me.Jackpot.NoteMod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class NoteModPlayerListener extends PlayerListener{
	public static NoteMod plugin;
	public NoteModPlayerListener(NoteMod instance){
		plugin = instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getMaterial() == Material.PAPER){
			if(event.getAction() == Action.RIGHT_CLICK_AIR){
				Note note = plugin.peekNote(event.getPlayer().getDisplayName());
				if(note != null){
					plugin.NoteMessage(player, "Holding note", note);
				}
			}
		}
	}
	
	public void onPlayerDropItem(PlayerDropItemEvent event){
		if(event.getItemDrop().getItemStack().getType() == Material.PAPER){
			Note note = plugin.peekNote(event.getPlayer().getDisplayName());
			if(note != null){
				plugin.NoteMessage(event.getPlayer(), "Dropped note", note);
				plugin.popNote(event.getPlayer().getDisplayName());
				plugin.pushNote(event.getItemDrop().getEntityId(), note);
			}
		}
	}
	
	public void onPlayerPickupItem(PlayerPickupItemEvent event){
		if(event.getItem().getItemStack().getType() == Material.PAPER){
			Note note = plugin.popNote(event.getItem().getEntityId());
			if(note != null){
				plugin.pushNote(event.getPlayer().getDisplayName(), note);
				plugin.NoteMessage(event.getPlayer(), "Picked up note", note);
			}
		}
	}
}
