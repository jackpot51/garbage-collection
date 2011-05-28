package me.Jackpot.BoatMod;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Boat {
			Hashtable<Vector, BlockData> _allblocks;
			Hashtable<Vector, BlockData> _blocks;
			Hashtable<Vector, BlockData> _breakables;
			Hashtable<Vector, BlockData> _removed;
			ArrayList<Vector> _air;
			int _movespeed;
			Vector _offset;
			//double _theta;
			//Vector _angle;
			static World _world;
			static Player _captain;
			static BoatMod plugin;
			
			public Boat(Block controlblock, World world, Player captain, BoatMod instance){
				_blocks = new Hashtable<Vector, BlockData>();
				_breakables = new Hashtable<Vector, BlockData>();
				_removed = new Hashtable<Vector, BlockData>();
				_air = new ArrayList<Vector>();
				_offset = controlblock.getLocation().toVector();
				//_angle = angle;
				//_theta = 0;
				_movespeed = 1;
				_world = world;
				_captain = captain;
				_captain.sendMessage("[BoatMod] You are now the captain.");
				plugin = instance;
				FindBlocks(controlblock);
				_allblocks = new Hashtable<Vector, BlockData>();
				_allblocks.putAll(_breakables);
				_allblocks.putAll(_blocks);
				FindAir();
				_captain.sendMessage("[BoatMod] You now have control of " + (_blocks.size() + _breakables.size()) + " blocks.");
			}
			
			public Player getCaptain(){
				return _captain;
			}
			
			public void changeSpeed(){
				if(_movespeed < 16){
					_movespeed *= 2;
					_captain.sendMessage("[BoatMod] Moving " + _movespeed + " blocks per click.");
				}else{
					_movespeed = 1;
					_captain.sendMessage("[BoatMod] Moving " + _movespeed + " block per click.");
				}
			}
			
			public void Move(Vector vec){
				for(int i = 0; i < _movespeed; i++){ //it has to be done this way to keep water intact and properly collide
					if(!MoveBlocks(vec)){
						break;
					}
				}
				
			}
			
			private boolean CheckFluid(Material m){
				return (
						m == Material.AIR ||
						m == Material.WATER ||
						m == Material.STATIONARY_WATER
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
						m == Material.TRAP_DOOR
						);
			}

			private boolean CheckBoatable(Material m){
				return (
						m == Material.FENCE ||
						m == Material.WOOD ||
						m == Material.WOOL ||
						m == Material.LOG ||
						m == Material.WOOD_STAIRS ||
						m == Material.GLASS ||
						m == Material.BED_BLOCK ||
						m == Material.BOOKSHELF ||
						m == Material.CHEST ||
						m == Material.WORKBENCH ||
						m == Material.STEP ||
						m == Material.GLOWSTONE ||
						CheckBreakable(m)
						);
			}
			
			private Vector GetVector(Location l){
				Vector vec = l.toVector();
				vec.subtract(_offset);
				return vec; //RotateVector(vec, -_theta);
			}
			
			//private double GetAngle(Vector dir){
			//	return Math.atan((dir.getZ()-_angle.getZ())/(dir.getX()-_angle.getX()));
			//}
			
			//private Vector RotateVector(Vector vec, double theta){
			//	Vector rot = vec.clone();
			//	rot.setX(vec.getX()*Math.cos(theta)-vec.getZ()*Math.sin(theta));
			//	rot.setZ(-vec.getX()*Math.sin(theta)-vec.getZ()*Math.cos(theta));
			//	return rot;
			//}
			
			private Vector GetReal(Vector vec){
				Vector real = vec.clone();//RotateVector(vec, _theta);
				real.add(_offset);
				return real;
			}
			
			private boolean CheckInBoat(Vector vec){
				return (
						_breakables.containsKey(vec) ||
						_blocks.containsKey(vec)
						);
			}
			
			private void FindBlocks(Block b){
				Vector vec = GetVector(b.getLocation());
				if(!CheckInBoat(vec) && !_removed.containsKey(GetReal(vec))){
					BlockData bd = new BlockData(b);
					if(CheckBoatable(b.getType())){
						if(CheckBreakable(b.getType())){
							_breakables.put(vec, bd);
						}else{
							_blocks.put(vec, bd);
						}
						for(int x = -1; x <= 1; x++){
							for(int y = -1; y <= 1; y++){
								for(int z = -1; z <= 1; z++){
									if(x != 0 || y != 0 || z != 0){
										FindBlocks(b.getRelative(x, y, z));
									}
								}
							}
						}
					}else{
						_removed.put(GetReal(vec), bd);
					}
				}
			}
			
			private boolean CheckSurroundingWater(Vector vec){
				Block b = GetBlock(vec);
				return (
						b.getRelative(0, 0, 1).getType() == Material.STATIONARY_WATER ||
						b.getRelative(0, 0, 1).getType() == Material.WATER ||
						b.getRelative(0, 0, -1).getType() == Material.STATIONARY_WATER ||
						b.getRelative(0, 0, -1).getType() == Material.WATER ||
						b.getRelative(1, 0, 0).getType() == Material.STATIONARY_WATER ||
						b.getRelative(1, 0, 0).getType() == Material.WATER ||
						b.getRelative(-1, 0, 0).getType() == Material.STATIONARY_WATER ||
						b.getRelative(-1, 0, 0).getType() == Material.WATER
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
				for(Enumeration<Vector> vectors = _allblocks.keys(); vectors.hasMoreElements();){
					Vector vec = vectors.nextElement();
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
						boolean hitwater = false;
						for(int x = minx; x <= maxx; x++){
							Vector blockvec = new Vector(x, y, z);
							if(CheckInBoat(blockvec)){
								if(CheckSurroundingWater(blockvec)){
									hitwater = true;
								}
								if(!hitblock){
									hitblock = true;
									startx = x;
									lastx = x;
								}
								if(hitblock){
									lastx = x;
								}
							}
						}
						for(int x = startx; x <= lastx; x++){
							Vector airvec = new Vector(x, y, z);
							if(hitwater){
								_removed.put(GetReal(airvec), new BlockData(Material.STATIONARY_WATER, (byte) 0));
							}
							if(!CheckInBoat(airvec)){
								_air.add(airvec);
							}
						}
					}
				}
			}
			
			private void SetBlock(Vector vec, BlockData bd){
				Vector real = GetReal(vec);
				bd.setBlock(_world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ()));
			}
			
			private Block GetBlock(Vector vec){
				Vector real = GetReal(vec);
				return new Location(_world, real.getX(), real.getY(), real.getZ()).getBlock();
			}
			
			private BlockData GetSavedData(Vector vec){
				if(_breakables.containsKey(vec)){
					return _breakables.get(vec);
				}else{
					return _blocks.get(vec);
				}
			}
			
			private boolean CheckAir(Vector vec, Vector movevec){
				Vector airvec = vec.clone();
				airvec.subtract(movevec);
				return _air.contains(airvec);
			}
						
			private boolean MoveBlocks(Vector movevec){
				boolean collision = false;
				boolean damaged = false;
				int x = movevec.getBlockX();
				int y = movevec.getBlockY();
				int z = movevec.getBlockZ();
				//check for changes and collisions, save new metadata
				for(Enumeration<Vector> vectors = _allblocks.keys(); vectors.hasMoreElements();){
					Vector vec = vectors.nextElement();
					Block current = GetBlock(vec);
					if(current.getType() != GetSavedData(vec).getType()){
						damaged = true;
					}else{
						GetSavedData(vec).updateData(current);
					}
					Block next = current.getRelative(x, y, z);
					if(!CheckFluid(next.getType()) && !CheckInBoat(GetVector(next.getLocation()))){
						collision = true;
					}
				}
				if(collision){
					_captain.sendMessage("[BoatMod] Collision detected, not moving!");
				}
				if(damaged){
					_captain.sendMessage("[BoatMod] Boat blocks have changed!");
				}
				if(!collision && !damaged){
					//clear old starting with breakables, replace removed blocks
					for(Enumeration<Vector> vectors = _allblocks.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						_allblocks.get(vec).clearBlock(GetBlock(vec));
						if(!CheckAir(vec, movevec) && _removed.containsKey(GetReal(vec))){
							SetBlock(vec, _removed.get(GetReal(vec)));
							_removed.remove(GetReal(vec));
						}else{
							SetBlock(vec, new BlockData(Material.AIR,(byte) 0));
						}
					}
					//teleport players
					Player players[] = plugin.getServer().getOnlinePlayers();
					for(int i = 0; i < players.length; i++){
						Location playerloc = players[i].getLocation();
						playerloc.setY(playerloc.getY() - 1);
						if(_blocks.containsKey(GetVector(playerloc.getBlock().getLocation()))){
							playerloc.setX(playerloc.getX() + movevec.getBlockX());
							playerloc.setY(playerloc.getY() + 1 + movevec.getBlockY());
							playerloc.setZ(playerloc.getZ() + movevec.getBlockZ());
							players[i].teleport(playerloc);
						}
					}
					_offset.add(movevec);
					//place new nonbreakables, gather removed blocks
					for(Enumeration<Vector> vectors = _blocks.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						Vector real = GetReal(vec);
						if(!_removed.containsKey(real)){
							_removed.put(real, new BlockData(GetBlock(vec)));
						}
						SetBlock(vec, _blocks.get(vec));
					}
					//place new breakables, gather removed blocks
					for(Enumeration<Vector> vectors = _breakables.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						Vector real = GetReal(vec);
						if(!_removed.containsKey(real)){
							_removed.put(real, new BlockData(GetBlock(vec)));
						}
						SetBlock(vec, _breakables.get(vec));
					}
					return true;
				}
				return false;
			}
}
