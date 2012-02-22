package me.Jackpot.BoatMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.Attachable;
import org.bukkit.material.Diode;
import org.bukkit.material.Door;
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressurePlate;
import org.bukkit.material.Rails;
import org.bukkit.material.RedstoneWire;
import org.bukkit.util.Vector;

public class Boat {
	public static BoatMod plugin;
	Hashtable<LocalVector, BlockData> blocks = new Hashtable<LocalVector, BlockData>();
	Hashtable<LocalVector, BlockData> breakables = new Hashtable<LocalVector, BlockData>();
	Hashtable<Vector, BlockData> removed = new Hashtable<Vector, BlockData>();
	ArrayList<LocalVector> air = new ArrayList<LocalVector>();
	int size = 0;
	double lasttheta = 0;
	Vector offset;
	Vector dir;
	LocalVector min = new LocalVector(0,0,0);
	LocalVector max = new LocalVector(0,0,0);
	int movespeed = 1;
	boolean needspower;
	int maxsize;
	World world;
	Player captain;
	AutoPilot autopilot;
	boolean good = false;
	
	public Boat(Block controlblock, Player player, BoatMod instance){
		plugin = instance;
		captain = player;
		offset = controlblock.getLocation().toVector();
		dir = GetCompassDirection(captain.getLocation().getDirection(), 0.5);
		world = controlblock.getWorld();
		Message("Start block of type " + controlblock.getType().name());
		maxsize = plugin.MaxBoatSize(captain);
		if(plugin.GetConfig(captain, "NeedsPower").equalsIgnoreCase("true")){
			needspower = true;
		}else{
			needspower = false;
		}
		
		good = create(controlblock);
		autopilot = new AutoPilot(this, instance);
	}

	Stack<Block> checknext = new Stack<Block>();
	
	public boolean create(Block controlblock){
		checknext.clear();
		checknext.push(controlblock);
		while(!checknext.empty()){
			if(FindBlocks(checknext.pop(), true) == null){
				Message("You hit the " + plugin.GetConfig(captain, "VehicleName") + " size limit of " + maxsize + " or there was an overlap!");
				return false;
			}
		}
		
		FindAir();
		return true;
	}
	
	private boolean CheckInBoat(LocalVector vec){
		return (blocks.containsKey(vec) || breakables.containsKey(vec));
	}
	
	private LocalVector FindBlocks(Block b, boolean recurse){
		Vector real = b.getLocation().toVector();
		LocalVector vec = new LocalVector(real, offset, 0);
		if(!CheckInBoat(vec) && !removed.containsKey(real)){
			BlockData bd = new BlockData(b.getState());
			if(plugin.CheckBoatable(bd.getType())){
				if(size < maxsize && !plugin.CheckIsBoated(real)){
					if(bd.md instanceof Door){
						Door door = (Door)bd.md;
						if(door.isTopHalf()){
							breakables.put(vec, bd);
							if(recurse){
								if(FindBlocks(b.getRelative(BlockFace.DOWN), false) == null){
									return null;
								}
							}
						}else{
							if(recurse){
								if(FindBlocks(b.getRelative(BlockFace.UP), false) == null){
									return null;
								}
							}
							breakables.put(vec, bd);
						}
					}
					/* TODO
					else if(bd.md instanceof Bed){
						Bed bed = (Bed)bd.md;
						if(bed.isHeadOfBed()){
							if(recurse){
								if(FindBlocks(b.getRelative(bed.getFacing().getOppositeFace()), false) == null){
									return null;
								}
							}
							breakables.put(vec, bd);
						}else{
							breakables.put(vec, bd);
							if(recurse){
								if(FindBlocks(b.getRelative(bed.getFacing()), false) == null){
									return null;
								}
							}
						}
					}
					*/
					else if(bd.md instanceof Attachable
							|| bd.md instanceof Ladder
							|| bd.md instanceof RedstoneWire
							|| bd.md instanceof Rails
							|| bd.md instanceof PressurePlate
							|| bd.md instanceof Diode
							){
						breakables.put(vec, bd);
					}else{
						blocks.put(vec, bd);
					}
					if(vec.getBlockX() > max.getBlockX()){
						max.setX(vec.getBlockX());
					}else if(vec.getBlockX() < min.getBlockX()){
						min.setX(vec.getBlockX());
					}
					if(vec.getBlockY() > max.getBlockY()){
						max.setY(vec.getBlockY());
					}else if(vec.getBlockY() < min.getBlockY()){
						min.setY(vec.getBlockY());
					}
					if(vec.getBlockZ() > max.getBlockZ()){
						max.setZ(vec.getBlockZ());
					}else if(vec.getBlockZ() < min.getBlockZ()){
						min.setZ(vec.getBlockZ());
					}
					size++;
					if(recurse){
						for(int x = -1; x <= 1; x++){
							for(int y = -1; y <= 1; y++ ){
								for(int z = -1; z <= 1; z++){
									if(x != 0 || y != 0 || z != 0){
										checknext.push(b.getRelative(x, y, z));
									}
								}
							}
						}
					}
				}else{
					return null;
				}
			}
		}
		return vec;
	}
	
	public Player getCaptain(){
		return captain;
	}
	
	public void Message(String msg){
		plugin.Message(captain, msg);
	}
	
	public void ChangeSpeed(int speed){
		if(speed > 0 && speed <= plugin.MaxBoatSpeed(captain) ){
			setSpeed(speed);
			Message("Moving " + movespeed + " block" + (speed>1?"s":"") + " per click.");
		}
	}
	
	public void setSpeed(int speed){
		movespeed = speed;
	}
	
	public boolean Move(Vector direction){
		Vector vec = GetCompassDirection(direction, 0.75);
		if(vec.length() == 1){
			for(int i = 0; i < movespeed; i++){ //it has to be done this way to keep water intact and properly collide
				if(!MoveBlocks(vec, lasttheta)){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	public boolean Rotate(Vector direction){
		Vector vec = GetCompassDirection(direction, 0.5);
		if(vec.length() == 1){
			double theta = 0;
			Vector cross = dir.clone().crossProduct(vec);
			if(dir.dot(vec) == -1){
				theta = Math.PI;
			}else if(cross.getBlockY() == 1){
				theta = Math.PI/2.0;
			}else if(cross.getBlockY() == -1){
				theta = Math.PI*3.0/2.0;
			}
			return MoveBlocks(new Vector(0,0,0), theta);
		}
		return false;
	}
	
	private Vector GetCompassDirection(Vector direction, double zone){
		Vector vec = new Vector(0,0,0);
		if(direction.getX() > zone){
			vec.setX(1);
		}
		if(direction.getX() < -zone){
			vec.setX(-1);
		}

		if(direction.getY() > zone){
			vec.setY(1);
		}
		if(direction.getY() < -zone){
			vec.setY(-1);
		}

		if(direction.getZ() > zone){
			vec.setZ(1);
		}
		if(direction.getZ() < -zone){
			vec.setZ(-1);
		}
		
		if(plugin.GetConfig(captain, "Vertical").equalsIgnoreCase("false")){
			vec.setY(0);
		}
		return vec;
	}
	
	EnumSet<Material> fluids = EnumSet.of(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);
				
	private boolean CheckFluid(Material m){
		return fluids.contains(m);
	}

	private boolean CheckSurrounding(Material m, LocalVector vec){
		Block b = GetBlock(vec, 0);
		return (
				b.getRelative(0, 0, 0).getType() == m ||
				b.getRelative(0, 0, 1).getType() == m ||
				b.getRelative(0, 0, -1).getType() == m ||
				b.getRelative(1, 0, 0).getType() == m ||
				b.getRelative(-1, 0, 0).getType() == m
				);
	}
	
	private void FindAir(){
		air.clear();
		for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
			for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++){
				int startx = min.getBlockX();
				int lastx = min.getBlockX()-1;
				boolean hitblock = false;
				Material hitfluid = null;
				for(int x = min.getBlockX(); x <= max.getBlockX(); x++){
					LocalVector vec = new LocalVector(new Vector(x, y, z));
					if(CheckInBoat(vec)){
						if(hitfluid == null){
							for(Iterator<Material> materials = fluids.iterator(); materials.hasNext();){
								Material m = materials.next();
								if(m != Material.AIR && CheckSurrounding(m, vec)){
									hitfluid = m;
									break;
								}
							}
						}
						if(!hitblock){
							hitblock = true;
							startx = x;
						}
						lastx = x;
					}
				}
				for(int x = startx; x <= lastx; x++){
					LocalVector vec = new LocalVector(new Vector(x, y, z));
					if(hitfluid != null){
						removed.put(vec.toReal(offset, 0), new BlockData(new MaterialData(hitfluid)));
					}
					if(!CheckInBoat(vec)){
						air.add(vec);
					}
				}
			}
		}
	}
	
	private void SetBlock(LocalVector vec, double theta, BlockData bd){
		Vector real = vec.toReal(offset, theta);
		bd.setBlock(world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ()), theta-lasttheta);
	}
	
	private Block GetBlock(LocalVector vec, double theta){
		Vector real = vec.toReal(offset, theta);
		return world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ());
	}
	
	private BlockData GetSavedData(LocalVector vec){
		if(breakables.containsKey(vec)){
			return breakables.get(vec);
		}else if(blocks.containsKey(vec)){
			return blocks.get(vec);
		}else{
			return new BlockData();
		}
	}
				
	enum BoatStatus{
		CHANGED, COLLISION, POWERED;
	}
				
	private EnumSet<BoatStatus> checkStatus(ArrayList<LocalVector> vectors, Vector movevec, double theta){
		EnumSet<BoatStatus> status = EnumSet.noneOf(BoatStatus.class);
		int x = movevec.getBlockX();
		int y = movevec.getBlockY();
		int z = movevec.getBlockZ();
		int power = 0;
		for(int i = 0; i < vectors.size(); i++){
			LocalVector vec = vectors.get(i);
			Block current = GetBlock(vec, lasttheta);
			BlockData bd = GetSavedData(vec);
			if(current.getType() != bd.getType()){
				status.add(BoatStatus.CHANGED);
			}
			bd.updateData(current.getState());
			power += bd.power;
			
			Block next = GetBlock(vec, theta).getRelative(x, y, z);
			LocalVector nextvec = new LocalVector(next.getLocation().toVector(), offset, lasttheta);
			if(!CheckFluid(next.getType()) && !CheckInBoat(nextvec)){
				status.add(BoatStatus.COLLISION);
			}
		}
		if(power >= movespeed){
			status.add(BoatStatus.POWERED);
		}
		return status;
	}
	
	private void PlaceBlocks(ArrayList<LocalVector> vectors, double theta){
		for(int i = 0; i < vectors.size(); i++){
			LocalVector vec = vectors.get(i);
			Vector real = vec.toReal(offset, theta);
			if(!removed.containsKey(real)){
				removed.put(real, new BlockData(GetBlock(vec, theta).getState()));
			}
			SetBlock(vec, theta, GetSavedData(vec));
		}
	}
	
	private void ClearBlocks(ArrayList<LocalVector> vectors, double theta){
		for(int i = 0; i < vectors.size(); i++){
			LocalVector vec = vectors.get(i);
			BlockData.clearBlock(GetBlock(vec, theta).getState());
			Vector real = vec.toReal(offset, theta);
			if(removed.containsKey(real)){
				SetBlock(vec, theta, removed.get(real));
				removed.remove(real);
			}else{
				SetBlock(vec, theta, new BlockData());
			}
		}
	}
	
	private boolean MoveBlocks(Vector movevec, double theta){
		ArrayList<LocalVector> vectors = new ArrayList<LocalVector>(breakables.keySet());
		vectors.addAll(blocks.keySet());
		vectors.addAll(air);
		EnumSet<BoatStatus> status = checkStatus(vectors, movevec, theta);
		if(needspower && !status.contains(BoatStatus.POWERED)){
			autopilot.stop();
			Message("You need at least " + movespeed + " powered furnaces to move!");
		}else if(!status.contains(BoatStatus.COLLISION)){
			//clear old starting with breakables, replace removed blocks
			ClearBlocks(vectors, lasttheta);
			
			//teleport entities
			List<Entity> entities = world.getEntities();
			for(int i = 0; i < entities.size(); i++){
				Location entityloc = entities.get(i).getLocation().clone();
				Vector entityreal = entityloc.getBlock().getRelative(BlockFace.DOWN).getLocation().toVector();
				LocalVector entitylocal = new LocalVector(entityreal, offset, lasttheta);
				if(blocks.containsKey(entitylocal)){
					Vector entitynext = entitylocal.toReal(offset, theta);
					entities.get(i).teleport(entityloc.add(movevec).add(entitynext).subtract(entityreal));
					entities.get(i).setFallDistance(0);
					entities.get(i).setFireTicks(0);
				}
			}
			
			offset.add(movevec);

			//place new blocks, gather removed blocks
			Collections.reverse(vectors);
			PlaceBlocks(vectors, theta);
			lasttheta = theta;
			return true;
		}
		return false;
	}
}
