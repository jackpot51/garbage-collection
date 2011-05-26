package me.Jackpot.BoatMod;

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
			int _movespeed;
			Vector _offset;
			double _theta;
			Vector _angle;
			static World _world;
			static Player _captain;
			static BoatMod plugin;
			
			public Boat(Block controlblock, Vector angle, World world, Player captain, BoatMod instance){
				_allblocks = new Hashtable<Vector, BlockData>();
				_blocks = new Hashtable<Vector, BlockData>();
				_breakables = new Hashtable<Vector, BlockData>();
				_offset = controlblock.getLocation().toVector();
				_angle = angle;
				_theta = 0;
				_movespeed = 2;
				_world = world;
				_captain = captain;
				_captain.sendMessage("[BoatMod] You are now the captain.");
				plugin = instance;
				FindBlocks(controlblock);
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
						m == Material.WALL_SIGN
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
						CheckBreakable(m)
						);
			}
			
			private Vector GetVector(Location l){
				Vector vec = l.toVector();
				vec.subtract(_offset);
				return RotateVector(vec, -_theta);
			}
			
			//private double GetAngle(Vector dir){
			//	return Math.atan((dir.getZ()-_angle.getZ())/(dir.getX()-_angle.getX()));
			//}
			
			private Vector RotateVector(Vector vec, double theta){
				Vector rot = vec.clone();
				rot.setX(vec.getX()*Math.cos(theta)-vec.getZ()*Math.sin(theta));
				rot.setZ(-vec.getX()*Math.sin(theta)-vec.getZ()*Math.cos(theta));
				return rot;
			}
			
			private Vector GetReal(Vector vec){
				Vector real = RotateVector(vec, _theta);
				real.add(_offset);
				return real;
			}
			
			private Material FindBlocks(Block b){
				if(CheckBoatable(b.getType()) && !_allblocks.containsKey(GetVector(b.getLocation()))){
					BlockData bd = new BlockData(b);
					if(CheckBreakable(b.getType())){
						_breakables.put(GetVector(b.getLocation()), bd);
					}else{
						_blocks.put(GetVector(b.getLocation()), bd);
					}
					_allblocks.put(GetVector(b.getLocation()), bd);
					for(int x = -1; x <= 1; x++){
						for(int y = -1; y <= 1; y++){
							for(int z = -1; z <= 1; z++){
								if(x != 0 || y != 0 || z != 0){
									Material m = FindBlocks(b.getRelative(x, y, z));
									bd.addNeighbor(-x, -y, -z, m);
								}
							}
						}
					}
				}
				return b.getType();
			}
			
			private void SetBlock(Vector vec, BlockData bd){
				Vector real = GetReal(vec);
				bd.setBlock(_world.getBlockAt(real.getBlockX(), real.getBlockY(), real.getBlockZ()));
			}
			
			private Block GetBlock(Vector vec){
				Vector real = GetReal(vec);
				return new Location(_world, real.getX(), real.getY(), real.getZ()).getBlock();
			}
			
			private boolean CheckInBoat(Vector vec){
				return (
						_breakables.containsKey(vec) ||
						_blocks.containsKey(vec)
						);
			}
			
			private BlockData GetSavedData(Vector vec){
				if(_breakables.containsKey(vec)){
					return _breakables.get(vec);
				}else{
					return _blocks.get(vec);
				}
			}
			
			private boolean MoveBlocks(Vector movevec){
				boolean collision = false;
				boolean damaged = false;
				//check for changes and collisions, save new metadata
				for(Enumeration<Vector> vectors = _allblocks.keys(); vectors.hasMoreElements();){
					Vector vec = vectors.nextElement();
					Block current = GetBlock(vec);
					if(current.getType() != GetSavedData(vec).getType()){
						damaged = true;
					}else{
						GetSavedData(vec).updateData(current);
					}
					Block next = current.getRelative(movevec.getBlockX(), movevec.getBlockY(), movevec.getBlockZ());
					if(!CheckFluid(next.getType()) && !CheckInBoat(GetVector(next.getLocation()))){
						if(!collision) _captain.sendMessage("Colliding with " + next.getType().name() + ".");
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
					//clear old breakables
					for(Enumeration<Vector> vectors = _breakables.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						if(_breakables.get(vec).getNeighbor(movevec.getBlockX(), movevec.getBlockY(), movevec.getBlockZ())){
							SetBlock(vec, new BlockData(Material.WATER,(byte) 0));
						}else{
							SetBlock(vec, new BlockData(Material.AIR,(byte) 0));
						}
					}
					//clear old nonbreakables
					for(Enumeration<Vector> vectors = _blocks.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						_blocks.get(vec).clearBlock(GetBlock(vec));
						if(_blocks.get(vec).getNeighbor(movevec.getBlockX(), movevec.getBlockY(), movevec.getBlockZ())){
							SetBlock(vec, new BlockData(Material.WATER,(byte) 0));
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
					//place new nonbreakables
					for(Enumeration<Vector> vectors = _blocks.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						SetBlock(vec, _blocks.get(vec));
					}
					//place new breakables
					for(Enumeration<Vector> vectors = _breakables.keys(); vectors.hasMoreElements();){
						Vector vec = vectors.nextElement();
						SetBlock(vec, _breakables.get(vec));
					}
					return true;
				}
				return false;
			}
}
