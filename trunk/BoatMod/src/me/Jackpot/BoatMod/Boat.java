package me.Jackpot.BoatMod;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;
import org.bukkit.material.Diode;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.PressurePlate;
import org.bukkit.material.Rails;
import org.bukkit.material.RedstoneWire;
import org.bukkit.material.SimpleAttachableMaterialData;
import org.bukkit.util.Vector;

public class Boat {
	public static BoatMod plugin;
	ArrayList<LocalVector> _vectors;
	Hashtable<LocalVector, BlockData> _blocks = new Hashtable<LocalVector, BlockData>();
	Hashtable<LocalVector, BlockData> _breakables = new Hashtable<LocalVector, BlockData>();
	ArrayList<LocalVector> _air = new ArrayList<LocalVector>();
	Hashtable<Vector, BlockData> _removed = new Hashtable<Vector, BlockData>();
	int _size = 0;
	double lasttheta = 0;
	Vector _offset;
	Vector _dir;
	int _movespeed = 1;
	int _maxsize;
	World _world;
	Player _captain;
	AutoPilot _autopilot;
	
	public Boat(Block controlblock, Player captain, BoatMod instance){
		plugin = instance;
		_offset = controlblock.getLocation().toVector();
		_dir = GetCompassDirection(captain.getLocation().getDirection(), 0.5);
		_world = controlblock.getWorld();
		_captain = captain;
		_maxsize = plugin.MaxBoatSize(captain);
		if(FindBlocks(controlblock, true) == null){
			Message("Warning! You hit the boat size limit!");
		}
		_vectors = new ArrayList<LocalVector>(_breakables.keySet());
		_vectors.addAll(_blocks.keySet());
		FindAir();
		_vectors.addAll(_air);
		_autopilot = new AutoPilot(this, instance);
	}
	
	public Player getCaptain(){
		return _captain;
	}
	
	public void Message(String msg){
		plugin.Message(_captain, msg);
	}
	
	public void ChangeSpeed(int movespeed){
		if(_movespeed > 0 && _movespeed <= plugin.MaxBoatSpeed(_captain) ){
			_movespeed = movespeed;
			Message("Moving " + _movespeed + " block" + (movespeed>1?"s":"") + " per click.");
		}
	}
	
	public void setSpeed(int movespeed){
		_movespeed = movespeed;
	}
	
	public boolean Move(Vector dir){
		Vector vec = GetCompassDirection(dir, 0.75);
		if(vec.length() == 1){
			for(int i = 0; i < _movespeed; i++){ //it has to be done this way to keep water intact and properly collide
				if(!MoveBlocks(vec, lasttheta)){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	public boolean Rotate(Vector dir){
		Vector vec = GetCompassDirection(dir, 0.5);
		if(vec.length() == 1){
			double theta = 0;
			Vector cross = _dir.clone().crossProduct(vec);
			if(_dir.dot(vec) == -1){
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
	
	private Vector GetCompassDirection(Vector dir, double zone){
		Vector vec = new Vector(0,0,0);
		if(dir.getX() > zone){
			vec.setX(1);
		}
		if(dir.getX() < -zone){
			vec.setX(-1);
		}

		if(dir.getY() > zone){
			vec.setY(1);
		}
		if(dir.getY() < -zone){
			vec.setY(-1);
		}

		if(dir.getZ() > zone){
			vec.setZ(1);
		}
		if(dir.getZ() < -zone){
			vec.setZ(-1);
		}
		return vec;
	}
	
	EnumSet<Material> fluids = EnumSet.of(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);
				
	private boolean CheckFluid(Material m){
		return fluids.contains(m);
	}
	
	/*
	EnumSet<Material> breakable = EnumSet.of(Material.TORCH, Material.LADDER, Material.CAKE_BLOCK, Material.SIGN,
											Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON,
											Material.SIGN_POST, Material.WALL_SIGN, Material.TRAP_DOOR, Material.LEVER,
											Material.REDSTONE_WIRE, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON,
											Material.STONE_BUTTON);
											
	
	private boolean CheckBreakable(Material m){
		return breakable.contains(m);
	}
	*/
	
	private boolean CheckInBoatInit(LocalVector vec){
		return (
				_blocks.containsKey(vec) ||
				_breakables.containsKey(vec)
				);
	}
	
	private boolean CheckInBoat(LocalVector vec){
		return _vectors.contains(vec);
	}
	
	private LocalVector FindBlocks(Block b, boolean recurse){
		Vector real = b.getLocation().toVector();
		LocalVector vec = new LocalVector(real, _offset, 0);
		if(!CheckInBoatInit(vec) && !_removed.containsKey(real)){
			BlockData bd = new BlockData(b.getState());
			if(plugin.CheckBoatable(bd.getType())){
				if(_size < _maxsize){
					if(bd.md instanceof Door){
						Door door = (Door)bd.md;
						if(door.isTopHalf()){
							_breakables.put(vec, bd);
							if(FindBlocks(b.getRelative(BlockFace.DOWN), false) == null){
								return null;
							}
						}else if(!recurse){
							_breakables.put(vec, bd);
						}
					}else if(bd.md instanceof Bed){
						Bed bed = (Bed)bd.md;
						if(bed.isHeadOfBed()){
							_breakables.put(vec, bd);
							if(FindBlocks(b.getRelative(bed.getFacing()), false) == null){
								return null;
							}
						}else if(!recurse){
							_breakables.put(vec, bd);
						}
					}else if(bd.md instanceof SimpleAttachableMaterialData
							|| bd.md instanceof RedstoneWire
							|| bd.md instanceof Rails
							|| bd.md instanceof PressurePlate
							|| bd.md instanceof Diode
							|| bd.md instanceof PistonExtensionMaterial){
						_breakables.put(vec, bd);
					}else{
						_blocks.put(vec, bd);
					}
					_size++;
					if(recurse){
						for(int x = -1; x <= 1; x++){
							for(int y = -1; y <= 1; y++ ){
								for(int z = -1; z <= 1; z++){
									if(x != 0 || y != 0 || z != 0){
										if(FindBlocks(b.getRelative(x, y, z), true) == null){
											return null;
										}
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
		_vectors.removeAll(_air);
		_air.clear();
		int minx = 0;
		int maxx = 0;
		int miny = 0;
		int maxy = 0;
		int minz = 0;
		int maxz = 0;
		for(Iterator<LocalVector> vectors = _vectors.iterator(); vectors.hasNext();){
			LocalVector vec = vectors.next();
			int blockx = vec.getBlockX();
			int blocky = vec.getBlockY();
			int blockz = vec.getBlockZ();
			if(blockx > maxx){
				maxx = blockx;
			}else if(blockx < minx){
				minx = blockx;
			}
			if(blocky > maxy){
				maxy = blocky;
			}else if(blocky < miny){
				miny = blocky;
			}
			if(blockz > maxz){
				maxz = blockz;
			}else if(blockz < minz){
				minz = blockz;
			}
		}
		for(int y = miny; y <= maxy; y++){
			for(int z = minz; z <= maxz; z++){
				int startx = minx;
				int lastx = minx-1;
				boolean hitblock = false;
				Material hitfluid = null;
				for(int x = minx; x <= maxx; x++){
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
						_removed.put(vec.toReal(_offset, 0), new BlockData(new MaterialData(hitfluid)));
					}
					if(!CheckInBoat(vec)){
						_air.add(vec);
					}
				}
			}
		}
	}
	
	private void SetBlock(LocalVector vec, double theta, BlockData bd){
		Vector real = vec.toReal(_offset, theta);
		bd.setBlock(_world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ()), theta-lasttheta);
	}
	
	private Block GetBlock(LocalVector vec, double theta){
		Vector real = vec.toReal(_offset, theta);
		return _world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ());
	}
	
	private BlockData GetSavedData(LocalVector vec){
		if(_breakables.containsKey(vec)){
			return _breakables.get(vec);
		}else if(_blocks.containsKey(vec)){
			return _blocks.get(vec);
		}else{
			return new BlockData();
		}
	}
				
	enum BoatStatus{
		CHANGED, COLLISION;
	}
				
	private EnumSet<BoatStatus> checkStatus(Vector movevec, double theta){
		EnumSet<BoatStatus> status = EnumSet.noneOf(BoatStatus.class);
		int x = movevec.getBlockX();
		int y = movevec.getBlockY();
		int z = movevec.getBlockZ();
		for(Iterator<LocalVector> vectors = _vectors.iterator(); vectors.hasNext();){
			LocalVector vec = vectors.next();
			Block current = GetBlock(vec, lasttheta);
			BlockData bd = GetSavedData(vec);
			if(current.getType() != bd.getType()){
				status.add(BoatStatus.CHANGED);
			}
			bd.updateData(current.getState());
			Block next = GetBlock(vec, theta).getRelative(x, y, z);
			LocalVector nextvec = new LocalVector(next.getLocation().toVector(), _offset, lasttheta);
			if(!CheckFluid(next.getType()) && !CheckInBoat(nextvec)){
				status.add(BoatStatus.COLLISION);
			}
		}
		return status;
	}
	
	private void PlaceBlocks(Iterator<LocalVector> vectors, double theta){
		while(vectors.hasNext()){
			LocalVector vec = vectors.next();
			Vector real = vec.toReal(_offset, theta);
			if(!_removed.containsKey(real)){
				_removed.put(real, new BlockData(GetBlock(vec, theta).getState()));
			}
			SetBlock(vec, theta, GetSavedData(vec));
		}
	}
				
	private boolean MoveBlocks(Vector movevec, double theta){
		EnumSet<BoatStatus> status = checkStatus(movevec, theta);
		if(!status.contains(BoatStatus.COLLISION)){
			//clear old starting with breakables, replace removed blocks
			for(Iterator<LocalVector> vectors = _vectors.iterator(); vectors.hasNext();){
				LocalVector vec = vectors.next();
				BlockData.clearBlock(GetBlock(vec, lasttheta).getState());
				Vector real = vec.toReal(_offset, lasttheta);
				if(_removed.containsKey(real)){
					SetBlock(vec, lasttheta, _removed.get(real));
					_removed.remove(real);
				}else{
					SetBlock(vec, lasttheta, new BlockData());
				}
			}
			//teleport entities
			List<Entity> entities = _world.getEntities();
			for(int i = 0; i < entities.size(); i++){
				Location entityloc = entities.get(i).getLocation().clone();
				Vector entityreal = entityloc.getBlock().getRelative(BlockFace.DOWN).getLocation().toVector();
				LocalVector entitylocal = new LocalVector(entityreal, _offset, lasttheta);
				if(_blocks.containsKey(entitylocal)){
					Vector entitynext = entitylocal.toReal(_offset, theta);
					entities.get(i).teleport(entityloc.add(movevec).add(entitynext).subtract(entityreal));
					entities.get(i).setFallDistance(0);
					entities.get(i).setFireTicks(0);
				}
			}
			_offset.add(movevec);
			//place air
			PlaceBlocks(_air.iterator(), theta);
			//place new nonbreakables, gather removed blocks
			PlaceBlocks(_blocks.keySet().iterator(), theta);
			//place new breakables, gather removed blocks
			PlaceBlocks(_breakables.keySet().iterator(), theta);
			lasttheta = theta;
			return true;
		}
		return false;
	}
}
