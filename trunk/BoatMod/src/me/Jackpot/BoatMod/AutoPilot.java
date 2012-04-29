package me.Jackpot.BoatMod;

import org.bukkit.util.Vector;


public class AutoPilot implements Runnable{
	public static BoatMod plugin;
	int autopilot;
	Boat boat;
	LocalVector lv;
	
	public AutoPilot(Boat setup_boat, BoatMod instance){
		plugin = instance;
		this.boat = setup_boat;
		this.lv = new LocalVector(this.boat.dir.clone());
	}
	
	public void start(int ticks){
		if(this.autopilot != 0){
			stop();
		}
		this.autopilot = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, ticks);
	}
	
	public void stop(){
		if(this.autopilot != 0){
			plugin.getServer().getScheduler().cancelTask(this.autopilot);
			this.autopilot = 0;
		}
	}
	
	@Override
	public void run() {
		this.boat.Move((this.lv.toReal(new Vector(0, 0, 0), this.boat.lasttheta)));
	}
}
