package me.Jackpot.BoatMod;

import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.Door;
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;

public class BlockData{
	MaterialData md = new MaterialData(Material.AIR);
	Stack<Object> extra = new Stack<Object>();
	int power = 0;
	public BlockData(){}
	public BlockData(BlockState bs){
		updateData(bs);
	}
	public BlockData(MaterialData newmd){
		this.md = newmd;
	}
	public static void clearBlock(BlockState bs){
		if(bs instanceof InventoryHolder){
			((InventoryHolder)bs).getInventory().clear();
		}
		if(bs instanceof Jukebox){
			((Jukebox)bs).setPlaying(null);
		}
		bs.update(true);
	}
	private static BlockFace rotate(BlockFace start_face, double start_theta){
		BlockFace face = start_face;
		double theta = start_theta;
		if(theta < 0){
			theta += Math.PI*2.0;
		}
		if(theta == Math.PI/2.0 || theta == Math.PI*3.0/2.0){
			switch(face){
				case NORTH:
					face = BlockFace.WEST;
					break;
				case WEST:
					face = BlockFace.SOUTH;
					break;
				case SOUTH:
					face = BlockFace.EAST;
					break;
				case EAST:
					face = BlockFace.NORTH;
					break;

				case NORTH_EAST:
					face = BlockFace.NORTH_WEST;
					break;
				case NORTH_WEST:
					face = BlockFace.SOUTH_WEST;
					break;
				case SOUTH_WEST:
					face = BlockFace.SOUTH_EAST;
					break;
				case SOUTH_EAST:
					face = BlockFace.NORTH_WEST;
					break;
					
				case NORTH_NORTH_EAST:
					face = BlockFace.WEST_NORTH_WEST;
					break;
				case WEST_NORTH_WEST:
					face = BlockFace.SOUTH_SOUTH_WEST;
					break;
				case SOUTH_SOUTH_WEST:
					face = BlockFace.EAST_SOUTH_EAST;
					break;
				case EAST_SOUTH_EAST:
					face = BlockFace.NORTH_NORTH_EAST;
					break;
					
				case NORTH_NORTH_WEST:
					face = BlockFace.WEST_SOUTH_WEST;
					break;
				case WEST_SOUTH_WEST:
					face = BlockFace.SOUTH_SOUTH_EAST;
					break;
				case SOUTH_SOUTH_EAST:
					face = BlockFace.EAST_NORTH_EAST;
					break;
				case EAST_NORTH_EAST:
					face = BlockFace.NORTH_NORTH_WEST;
					break;
				default:
					break;	
			}
			theta -= Math.PI/2.0;
		}
		if(theta == Math.PI){
			face = face.getOppositeFace();
		}
		return face;
	}
	public void setBlock(Block b, double dtheta){
		b.setType(this.md.getItemType());
		//set rotation
		if(this.md instanceof Ladder){
			((Ladder)this.md).setFacingDirection(rotate(((Ladder)this.md).getAttachedFace(), dtheta));
		}else if(this.md instanceof Directional){
			((Directional)this.md).setFacingDirection(rotate(((Directional)this.md).getFacing(), dtheta));
		}
		
		//set extra states
		if(this.md instanceof Door){
			((Door)this.md).setTopHalf((Boolean)this.extra.pop());
			((Door)this.md).setOpen((Boolean)this.extra.pop());
		}
		b.setData(this.md.getData(), true);
		BlockState bs = b.getState();
		if(bs instanceof BrewingStand){
			((BrewingStand)bs).setBrewingTime((Integer)this.extra.pop());
		}
		if(bs instanceof Jukebox){
			((Jukebox)bs).setPlaying((Material)this.extra.pop());
		}
		if(bs instanceof NoteBlock){
			((NoteBlock)bs).setRawNote((Byte)this.extra.pop());
		}
		if(bs instanceof Furnace){
			((Furnace)bs).setCookTime((Short)this.extra.pop());
			((Furnace)bs).setBurnTime((Short)this.extra.pop());
		}
		if(bs instanceof InventoryHolder){
			((InventoryHolder)bs).getInventory().setContents((ItemStack[])this.extra.pop());
		}
		if(bs instanceof Sign){
			String lines[] = (String[]) this.extra.pop();
			for(int i = 0; i < lines.length; i++){
				((Sign)bs).setLine(i, lines[i]);
			}
		}

		bs.update(true);
	}
	public void updateData(BlockState bs){
		this.md = bs.getData();
		//get extra states
		this.extra.clear();
		if(bs instanceof Sign){
			this.extra.push(((Sign)bs).getLines());
		}
		if(bs instanceof InventoryHolder){
			this.extra.push(((InventoryHolder)bs).getInventory().getContents());
		}
		if(bs instanceof Furnace){
			this.extra.push(((Furnace)bs).getBurnTime());
			this.extra.push(((Furnace)bs).getCookTime());
			if(((Furnace)bs).getBurnTime() > 0){
				this.power=1;
			}else{
				this.power=0;
			}
		}
		if(bs instanceof NoteBlock){
			this.extra.push(((NoteBlock)bs).getRawNote());
		}
		if(bs instanceof Jukebox){
			this.extra.push(((Jukebox)bs).getPlaying());
		}
		if(bs instanceof BrewingStand){
			this.extra.push(((BrewingStand)bs).getBrewingTime());
		}
		if(this.md instanceof Door){
			this.extra.push(((Door)this.md).isOpen());
			this.extra.push(((Door)this.md).isTopHalf());
		}
	}
	public Material getType(){
		return this.md.getItemType();
	}
}
