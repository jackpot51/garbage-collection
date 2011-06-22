package me.Jackpot.BoatMod;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class Boat {
			ArrayList<BlockVector> _vectors;
			Hashtable<BlockVector, BlockData> _blocks;
			Hashtable<BlockVector, BlockData> _breakables;
			Hashtable<BlockVector, BlockData> _removed;
			ArrayList<BlockVector> _air;
			int _size;
			BlockVector _offset;
			int _movespeed;
			int _maxsize;
			//double _theta;
			//Vector _angle;
			World _world;
			Player _captain;
			BoatMod plugin;
			
			public Boat(Block controlblock, Player captain, BoatMod instance){
				_blocks = new Hashtable<BlockVector, BlockData>();
				_breakables = new Hashtable<BlockVector, BlockData>();
				_removed = new Hashtable<BlockVector, BlockData>();
				_air = new ArrayList<BlockVector>();
				_size = 0;
				_offset = controlblock.getLocation().toVector().toBlockVector();
				_movespeed = 1;
				_world = controlblock.getWorld();
				_captain = captain;
				plugin = instance;
				_maxsize = plugin.MaxBoatSize(captain);
				if(!FindBlocks(controlblock)){
					Message("Warning! You hit the boat size limit!");
				}
				_vectors = new ArrayList<BlockVector>(_breakables.keySet());
				_vectors.addAll(_blocks.keySet());
				FindAir();
			}
			
			public Player getCaptain(){
				return _captain;
			}
			
			public void Message(String msg){
				plugin.Message(_captain, msg);
			}
			
			public void changeSpeed(){
				if(_movespeed < plugin.MaxBoatSpeed(_captain)){
					_movespeed++;
					Message("Moving " + _movespeed + " blocks per click.");
				}else{
					_movespeed = 1;
					Message("Moving " + _movespeed + " block per click.");
				}
			}
			
			public void setSpeed(int movespeed){
				_movespeed = movespeed;
			}
			
			public boolean Move(Vector dir){
				BlockVector vec = GetCompassDirection(dir);
				if(vec.length() == 1){
					for(int i = 0; i < _movespeed; i++){ //it has to be done this way to keep water intact and properly collide
						if(!MoveBlocks(vec)){
							return false;
						}
					}
					return true;
				}else{
					return false;
				}
			}
			
			private BlockVector GetCompassDirection(Vector dir){
				BlockVector vec = new BlockVector(0,0,0);
				if(dir.getX() > 0.75){
					vec.setX(1);
				}
				if(dir.getX() < -0.75){
					vec.setX(-1);
				}

				if(dir.getY() > 0.75){
					vec.setY(1);
				}
				if(dir.getY() < -0.75){
					vec.setY(-1);
				}

				if(dir.getZ() > 0.75){
					vec.setZ(1);
				}
				if(dir.getZ() < -0.75){
					vec.setZ(-1);
				}
				return vec;
			}
			
			private boolean CheckFluid(Material m){
				return (
						m == Material.AIR ||
						m == Material.WATER ||
						m == Material.STATIONARY_WATER ||
						m == Material.LAVA ||
						m == Material.STATIONARY_LAVA
						);
			}
			
			private boolean CheckBreakable(Material m){
				return (
						m == Material.TORCH ||
						m == Material.LADDER ||
						m == Material.WOODEN_DOOR ||
						m == Material.CAKE_BLOCK ||
						m == Material.SIGN ||
						m == Material.SIGN_POST ||
						m == Material.WALL_SIGN ||
						m == Material.TRAP_DOOR ||
						m == Material.REDSTONE_WIRE ||
						m == Material.REDSTONE_TORCH_OFF ||
						m == Material.REDSTONE_TORCH_ON ||
						m == Material.STONE_BUTTON
						);
			}

			private boolean CheckBoatable(Material m){
				return plugin.CheckBoatable(m);
			}
			
			private BlockVector GetVector(Location l){
				BlockVector vec = l.toVector().toBlockVector();
				vec.subtract(_offset);
				return vec;
			}
			
			private BlockVector GetReal(BlockVector vec){
				BlockVector real = vec.clone();
				real.add(_offset);
				return real;
			}
			
			private boolean CheckInBoatInit(BlockVector vec){
				return (
						_blocks.containsKey(vec) ||
						_breakables.containsKey(vec)
						);
			}
			
			private boolean CheckInBoat(BlockVector vec){
				return _vectors.contains(vec);
			}
			
			private boolean FindBlocks(Block b){
				boolean success = true;
				BlockVector vec = GetVector(b.getLocation());
				if(!CheckInBoatInit(vec) && !_removed.containsKey(GetReal(vec))){
					BlockData bd = new BlockData(b);
					if(CheckBoatable(b.getType())){
						if(_size < _maxsize){
							if(CheckBreakable(b.getType())){
								_breakables.put(vec, bd);
							}else{
								_blocks.put(vec, bd);
							}
							_size++;
							for(int x = -1; x <= 1; x++){
								for(int y = -1; y <= 1; y++){
									for(int z = -1; z <= 1; z++){
										if(x != 0 || y != 0 || z != 0){
											if(!FindBlocks(b.getRelative(x, y, z))){
												success = false;
											}
										}
									}
								}
							}
						}else{
							success=false;
						}
					}else{
						_removed.put(GetReal(vec), bd);
					}
				}
				return success;
			}
			
			private boolean CheckSurrounding(Material m, BlockVector vec){
				Block b = GetBlock(vec);
				return (
						b.getRelative(0, 0, 0).getType() == m ||
						b.getRelative(0, 0, 1).getType() == m ||
						b.getRelative(0, 0, -1).getType() == m ||
						b.getRelative(1, 0, 0).getType() == m ||
						b.getRelative(-1, 0, 0).getType() == m
						);
			}
			
			private void FindAir(){
				_air.clear();
				int minx = 0;
				int maxx = 0;
				int miny = 0;
				int maxy = 0;
				int minz = 0;
				int maxz = 0;
				for(Iterator<BlockVector> vectors = _vectors.iterator(); vectors.hasNext();){
					BlockVector vec = vectors.next();
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
						int startx = 0;
						int lastx = 0;
						boolean hitblock = false;
						Material hitfluid = null;
						for(int x = minx; x <= maxx; x++){
							BlockVector blockvec = new BlockVector(x, y, z);
							if(CheckInBoat(blockvec)){
								if(hitfluid == null){
									if(CheckSurrounding(Material.WATER, blockvec)){
										hitfluid = Material.WATER;
									}else if(CheckSurrounding(Material.STATIONARY_WATER, blockvec)){
										hitfluid = Material.STATIONARY_WATER;
									}else if(CheckSurrounding(Material.LAVA, blockvec)){
										hitfluid = Material.LAVA;
									}else if(CheckSurrounding(Material.STATIONARY_LAVA, blockvec)){
										hitfluid = Material.STATIONARY_LAVA;
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
							BlockVector airvec = new BlockVector(x, y, z);
							if(hitfluid != null){
								_removed.put(GetReal(airvec), new BlockData(hitfluid, (byte) 0));
							}
							if(!CheckInBoat(airvec)){
								_air.add(airvec);
							}
						}
					}
				}
			}
			
			private void SetBlock(BlockVector vec, BlockData bd){
				BlockVector real = GetReal(vec);
				bd.setBlock(_world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ()));
			}
			
			private Block GetBlock(BlockVector vec){
				BlockVector real = GetReal(vec);
				return _world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ());
			}
			
			private BlockData GetSavedData(BlockVector vec){
				if(_breakables.containsKey(vec)){
					return _breakables.get(vec);
				}else{
					return _blocks.get(vec);
				}
			}
			
			private boolean CheckAir(BlockVector vec, BlockVector movevec){
				BlockVector airvec = vec.clone();
				airvec.subtract(movevec);
				return _air.contains(airvec);
			}
						
			private boolean MoveBlocks(BlockVector movevec){
				boolean collision = false;
				boolean damaged = false;
				int x = movevec.getBlockX();
				int y = movevec.getBlockY();
				int z = movevec.getBlockZ();
				//ArrayList<Chunk> chunks = new ArrayList<Chunk>();
				//check for changes and collisions, save new metadata, get chunks
				for(Iterator<BlockVector> vectors = _vectors.iterator(); vectors.hasNext();){
					BlockVector vec = vectors.next();
					Block current = GetBlock(vec);
					if(current.getType() != GetSavedData(vec).getType()){
						damaged = true;
					}
					GetSavedData(vec).updateData(current);
					Block next = current.getRelative(x, y, z);
					if(!CheckFluid(next.getType()) && !CheckInBoat(GetVector(next.getLocation()))){
						collision = true;
					}
					//if(!chunks.contains(next.getChunk())){
					//	chunks.add(next.getChunk());
					//}
				}
				if(collision){
					Message("Collision detected, not moving!");
				}
				if(damaged){
					Message("Boat blocks have changed!");
				}
				if(!collision){
					//clear old starting with breakables, replace removed blocks
					for(Iterator<BlockVector> vectors = _vectors.iterator(); vectors.hasNext();){
						BlockVector vec = vectors.next();
						GetSavedData(vec).clearBlock(GetBlock(vec));
						if(!CheckAir(vec, movevec) && _removed.containsKey(GetReal(vec))){
							SetBlock(vec, _removed.get(GetReal(vec)));
							_removed.remove(GetReal(vec));
						}else{
							SetBlock(vec, new BlockData(Material.AIR,(byte) 0));
						}
					}
					//teleport entities
					List<Entity> entities = _world.getEntities();
					for(int i = 0; i < entities.size(); i++){
						Location entityloc = entities.get(i).getLocation().clone();
						if(_blocks.containsKey(GetVector(entityloc.getBlock().getFace(BlockFace.DOWN).getLocation()))){
							entityloc.setX(entityloc.getX() + movevec.getBlockX());
							entityloc.setY(entityloc.getY() + movevec.getBlockY());
							entityloc.setZ(entityloc.getZ() + movevec.getBlockZ());
							entities.get(i).teleport(entityloc);
						}
					}
					_offset.add(movevec);
					//place new nonbreakables, gather removed blocks
					for(Enumeration<BlockVector> vectors = _blocks.keys(); vectors.hasMoreElements();){
						BlockVector vec = vectors.nextElement();
						BlockVector real = GetReal(vec);
						if(!_removed.containsKey(real)){
							_removed.put(real, new BlockData(GetBlock(vec)));
						}
						SetBlock(vec, _blocks.get(vec));
					}
					//place new breakables, gather removed blocks
					for(Enumeration<BlockVector> vectors = _breakables.keys(); vectors.hasMoreElements();){
						BlockVector vec = vectors.nextElement();
						BlockVector real = GetReal(vec);
						if(!_removed.containsKey(real)){
							_removed.put(real, new BlockData(GetBlock(vec)));
						}
						SetBlock(vec, _breakables.get(vec));
					}
					/*for(Iterator<Chunk> chunkiterator = chunks.iterator(); chunkiterator.hasNext();){
						Chunk chunk = chunkiterator.next();
						_world.refreshChunk(chunk.getX(), chunk.getZ());
					}*/
					return true;
				}
				return false;
			}
}