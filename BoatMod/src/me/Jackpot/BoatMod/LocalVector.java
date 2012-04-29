package me.Jackpot.BoatMod;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class LocalVector extends BlockVector {
	public LocalVector(Vector vec){
		super(vec.toBlockVector());
	}
	
	public LocalVector(int setup_x, int setup_y, int setup_z){
		super(setup_x, setup_y, setup_z);
	}
	
	public LocalVector(Vector real, Vector offset, double theta){
		super((real.getBlockX() - offset.getBlockX())*(int)Math.round(Math.cos(theta)) - (real.getBlockZ() - offset.getBlockZ())*(int)Math.round(Math.sin(theta)),
				real.getBlockY() - offset.getBlockY(),
				(real.getBlockX() - offset.getBlockX())*(int)Math.round(Math.sin(theta)) + (real.getBlockZ() - offset.getBlockZ())*(int)Math.round(Math.cos(theta)));
	}
	
	public Vector toReal(Vector offset, double theta){
		return new Vector(getBlockX()*(int)Math.round(Math.cos(-theta)) - getBlockZ()*(int)Math.round(Math.sin(-theta)) + offset.getBlockX(),
				getBlockY() + offset.getBlockY(),
				getBlockX()*(int)Math.round(Math.sin(-theta)) + getBlockZ()*(int)Math.round(Math.cos(-theta)) + offset.getBlockZ());
	}
}
