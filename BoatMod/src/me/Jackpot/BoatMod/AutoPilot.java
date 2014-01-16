package me.Jackpot.BoatMod;

import org.bukkit.util.Vector;


public class AutoPilot implements Runnable{
	public static BoatMod plugin;
	int task_id;
	Boat boat;
	LocalVector lv;
	
	public AutoPilot(Boat setup_boat, BoatMod instance){
		plugin = instance;
		this.boat = setup_boat;
		this.lv = new LocalVector(this.boat.dir.clone());
		this.task_id = 0;
	}
	
	public void start(int ticks){
		if(isRunning()){
			stop();
		}
		this.task_id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, ticks);
	}
	
	public void stop(){
		if(isRunning()){
			plugin.getServer().getScheduler().cancelTask(this.task_id);
			this.task_id = 0;
		}
	}
	
	public boolean isRunning(){
		return (this.task_id != 0);
	}
	
	@Override
	public void run() {
		this.boat.Move((this.lv.toReal(new Vector(0, 0, 0), this.boat.lasttheta)));
	}
}
