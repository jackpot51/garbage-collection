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
import org.bukkit.material.Bed;
import org.bukkit.material.Diode;
import org.bukkit.material.Door;
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
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
		this.captain = player;
		this.offset = controlblock.getLocation().toVector();
		this.dir = GetCompassDirection(this.captain.getLocation().getDirection(), 0.5);
		this.world = controlblock.getWorld();
		this.maxsize = plugin.MaxBoatSize(this.captain);
		if(plugin.GetConfig(this.captain, "NeedsPower").equalsIgnoreCase("true")){
			this.needspower = true;
		}else{
			this.needspower = false;
		}
		
		this.good = create(controlblock);
		this.autopilot = new AutoPilot(this, instance);
	}

	Stack<Block> checknext = new Stack<Block>();
	
	public boolean create(Block controlblock){
		this.checknext.clear();
		this.checknext.push(controlblock);
		while(!this.checknext.empty()){
			if(FindBlocks(this.checknext.pop(), true) == null){
				Message("You hit the " + plugin.GetConfig(this.captain, "VehicleName") + " size limit of " + this.maxsize + " or there was an overlap!");
				return false;
			}
		}
		
		FindAir();
		return true;
	}
	
	private boolean CheckInBoat(LocalVector vec){
		return (this.blocks.containsKey(vec) || this.breakables.containsKey(vec));
	}
	
	private LocalVector FindBlocks(Block b, boolean recurse){
		Vector real = b.getLocation().toVector();
		LocalVector vec = new LocalVector(real, this.offset, 0);
		if(!CheckInBoat(vec) && !this.removed.containsKey(real)){
			BlockData bd = new BlockData(b.getState());
			if(plugin.CheckBoatable(bd.getType())){
				if(this.size < this.maxsize && !plugin.CheckIsBoated(real)){
					if(bd.md instanceof Door){
						Door door = (Door)bd.md;
						if(door.isTopHalf()){
							this.breakables.put(vec, bd);
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
							this.breakables.put(vec, bd);
						}
					}
					else if(bd.md instanceof Bed){
						Bed bed = (Bed)bd.md;
						if(bed.isHeadOfBed()){
							if(recurse){
								if(FindBlocks(b.getRelative(bed.getFacing().getOppositeFace()), false) == null){
									return null;
								}
							}
							this.breakables.put(vec, bd);
						}else{
							this.breakables.put(vec, bd);
							if(recurse){
								if(FindBlocks(b.getRelative(bed.getFacing()), false) == null){
									return null;
								}
							}
						}
					}
					else if(bd.md instanceof PistonBaseMaterial){
						PistonBaseMaterial piston = (PistonBaseMaterial)bd.md;
						if(recurse){
							if(FindBlocks(b.getRelative(piston.getFacing()), false) == null){
								return null;
							}
						}
						this.breakables.put(vec, bd);
						Message("Placed piston base at " + this.breakables.size());
					}
					else if(bd.md instanceof PistonExtensionMaterial){
						PistonExtensionMaterial piston = (PistonExtensionMaterial)bd.md;
						this.breakables.put(vec, bd);
						Message("Placed piston extension at " + this.breakables.size());
						if(recurse){
							if(FindBlocks(b.getRelative(piston.getFacing().getOppositeFace()), false) == null){
								return null;
							}
						}
					}
					else if(bd.md instanceof Attachable
							|| bd.md instanceof Ladder
							|| bd.md instanceof RedstoneWire
							|| bd.md instanceof Rails
							|| bd.md instanceof PressurePlate
							|| bd.md instanceof Diode
							){
						this.breakables.put(vec, bd);
					}else{
						this.blocks.put(vec, bd);
					}
					if(vec.getBlockX() > this.max.getBlockX()){
						this.max.setX(vec.getBlockX());
					}else if(vec.getBlockX() < this.min.getBlockX()){
						this.min.setX(vec.getBlockX());
					}
					if(vec.getBlockY() > this.max.getBlockY()){
						this.max.setY(vec.getBlockY());
					}else if(vec.getBlockY() < this.min.getBlockY()){
						this.min.setY(vec.getBlockY());
					}
					if(vec.getBlockZ() > this.max.getBlockZ()){
						this.max.setZ(vec.getBlockZ());
					}else if(vec.getBlockZ() < this.min.getBlockZ()){
						this.min.setZ(vec.getBlockZ());
					}
					this.size++;
					if(recurse){
						for(int x = -1; x <= 1; x++){
							for(int y = -1; y <= 1; y++ ){
								for(int z = -1; z <= 1; z++){
									if(x != 0 || y != 0 || z != 0){
										this.checknext.push(b.getRelative(x, y, z));
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
		return this.captain;
	}
	
	public void Message(String msg){
		plugin.Message(this.captain, msg);
	}
	
	public void ChangeSpeed(int speed){
		if(speed > 0 && speed <= plugin.MaxBoatSpeed(this.captain) ){
			setSpeed(speed);
			Message("Moving " + this.movespeed + " block" + (speed>1?"s":"") + " per click.");
		}
	}
	
	public void setSpeed(int speed){
		this.movespeed = speed;
	}
	
	public boolean Move(Vector direction){
		Vector vec = GetCompassDirection(direction, 0.75);
		if(vec.length() == 1){
			for(int i = 0; i < this.movespeed; i++){ //it has to be done this way to keep water intact and properly collide
				if(!MoveBlocks(vec, this.lasttheta)){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean Rotate(Vector direction){
		Vector vec = GetCompassDirection(direction, 0.5);
		if(vec.length() == 1){
			double theta = 0;
			Vector cross = this.dir.clone().crossProduct(vec);
			if(this.dir.dot(vec) == -1){
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
		
		if(plugin.GetConfig(this.captain, "Vertical").equalsIgnoreCase("false")){
			vec.setY(0);
		}
		return vec;
	}
	
	EnumSet<Material> fluids = EnumSet.of(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);
				
	private boolean CheckFluid(Material m){
		return this.fluids.contains(m);
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
		this.air.clear();
		for(int y = this.min.getBlockY(); y <= this.max.getBlockY(); y++){
			for(int z = this.min.getBlockZ(); z <= this.max.getBlockZ(); z++){
				int startx = this.min.getBlockX();
				int lastx = this.min.getBlockX()-1;
				boolean hitblock = false;
				Material hitfluid = null;
				for(int x = this.min.getBlockX(); x <= this.max.getBlockX(); x++){
					LocalVector vec = new LocalVector(new Vector(x, y, z));
					if(CheckInBoat(vec)){
						if(hitfluid == null){
							for(Iterator<Material> materials = this.fluids.iterator(); materials.hasNext();){
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
						this.removed.put(vec.toReal(this.offset, 0), new BlockData(new MaterialData(hitfluid)));
					}
					if(!CheckInBoat(vec)){
						this.air.add(vec);
					}
				}
			}
		}
	}
	
	private void SetBlock(LocalVector vec, double theta, BlockData bd){
		Vector real = vec.toReal(this.offset, theta);
		bd.setBlock(this.world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ()), theta-this.lasttheta);
	}
	
	private Block GetBlock(LocalVector vec, double theta){
		Vector real = vec.toReal(this.offset, theta);
		return this.world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ());
	}
	
	private BlockData GetSavedData(LocalVector vec){
		if(this.breakables.containsKey(vec)){
			return this.breakables.get(vec);
		}else if(this.blocks.containsKey(vec)){
			return this.blocks.get(vec);
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
			Block current = GetBlock(vec, this.lasttheta);
			BlockData bd = GetSavedData(vec);
			if(current.getType() != bd.getType()){
				status.add(BoatStatus.CHANGED);
			}
			bd.updateData(current.getState());
			power += bd.power;
			
			Block next = GetBlock(vec, theta).getRelative(x, y, z);
			LocalVector nextvec = new LocalVector(next.getLocation().toVector(), this.offset, this.lasttheta);
			if(!CheckFluid(next.getType()) && !CheckInBoat(nextvec)){
				status.add(BoatStatus.COLLISION);
			}
		}
		if(power >= this.movespeed){
			status.add(BoatStatus.POWERED);
		}
		return status;
	}
	
	private void PlaceBlocks(ArrayList<LocalVector> vectors, double theta){
		for(int i = 0; i < vectors.size(); i++){
			LocalVector vec = vectors.get(i);
			Vector real = vec.toReal(this.offset, theta);
			if(!this.removed.containsKey(real)){
				this.removed.put(real, new BlockData(GetBlock(vec, theta).getState()));
			}
			SetBlock(vec, theta, GetSavedData(vec));
		}
	}
	
	private void ClearBlocks(ArrayList<LocalVector> vectors, double theta){
		for(int i = 0; i < vectors.size(); i++){
			LocalVector vec = vectors.get(i);
			BlockData.clearBlock(GetBlock(vec, theta).getState());
			Vector real = vec.toReal(this.offset, theta);
			if(this.removed.containsKey(real)){
				SetBlock(vec, theta, this.removed.get(real));
				this.removed.remove(real);
			}else{
				SetBlock(vec, theta, new BlockData());
			}
		}
	}
	
	private boolean MoveBlocks(Vector movevec, double theta){
		ArrayList<LocalVector> vectors = new ArrayList<LocalVector>(this.breakables.keySet());
		vectors.addAll(this.blocks.keySet());
		vectors.addAll(this.air);
		EnumSet<BoatStatus> status = checkStatus(vectors, movevec, theta);
		if(this.needspower && !status.contains(BoatStatus.POWERED)){
			this.autopilot.stop();
			Message("You need at least " + this.movespeed + " powered furnaces to move!");
		}else if(!status.contains(BoatStatus.COLLISION)){
			//clear old starting with breakables, replace removed blocks
			ClearBlocks(vectors, this.lasttheta);
			
			//teleport entities
			List<Entity> entities = this.world.getEntities();
			for(int i = 0; i < entities.size(); i++){
				Location entityloc = entities.get(i).getLocation().clone();
				Vector entityreal = entityloc.getBlock().getRelative(BlockFace.DOWN).getLocation().toVector();
				LocalVector entitylocal = new LocalVector(entityreal, this.offset, this.lasttheta);
				if(this.blocks.containsKey(entitylocal)){
					Vector entitynext = entitylocal.toReal(this.offset, theta);
					entities.get(i).teleport(entityloc.add(movevec).add(entitynext).subtract(entityreal));
					entities.get(i).setFallDistance(0);
					entities.get(i).setFireTicks(0);
				}
			}
			
			this.offset.add(movevec);

			//place new blocks, gather removed blocks
			Collections.reverse(vectors);
			PlaceBlocks(vectors, theta);
			this.lasttheta = theta;
			return true;
		}
		return false;
	}
}
