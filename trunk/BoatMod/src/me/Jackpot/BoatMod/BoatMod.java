package me.Jackpot.BoatMod;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatMod extends JavaPlugin {
	private final BoatModPlayerListener playerListener = new BoatModPlayerListener(this);
	Logger log = Logger.getLogger("Minecraft");
	public void onEnable(){
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		log.info("BoatMod has been enabled.");
	}
	public void onDisable(){
		log.info("BoatMod has been disabled.");
	}
}
