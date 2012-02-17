package me.Jackpot.BoatMod;

import org.bukkit.util.Vector;


public class AutoPilot implements Runnable{
	public static BoatMod plugin;
	int _autopilot;
	Boat _boat;
	LocalVector _lv;
	
	public AutoPilot(Boat boat, BoatMod instance){
		plugin = instance;
		_boat = boat;
		_lv = new LocalVector(_boat._dir.clone());
	}
	public void set(boolean autopilot, int ticks){
		if(autopilot ^ (_autopilot != 0)){
			if(autopilot){
				_autopilot = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, ticks);
			}else{
				plugin.getServer().getScheduler().cancelTask(_autopilot);
				_autopilot = 0;
			}
		}
	}
	
	@Override
	public void run() {
		_boat.Move((_lv.toReal(new Vector(0, 0, 0), _boat.lasttheta)));
	}
}
