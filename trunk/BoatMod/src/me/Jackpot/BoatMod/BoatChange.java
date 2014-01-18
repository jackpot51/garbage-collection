package me.Jackpot.BoatMod;

import org.bukkit.World;
import org.bukkit.util.Vector;

public class BoatChange {
	Vector real;
	double dtheta;
	BlockData bd;
	
	BoatChange(Vector real, double dtheta, BlockData bd){
		this.real = real;
		this.dtheta = dtheta;
		this.bd = bd;
	}
	
	public void apply(World world){
		bd.setBlock(world.getBlockAt(this.real.getBlockX(), this.real.getBlockY(), this.real.getBlockZ()), this.dtheta);
	}
}
