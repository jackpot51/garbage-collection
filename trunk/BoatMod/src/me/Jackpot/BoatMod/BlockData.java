package me.Jackpot.BoatMod;

import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Furnace;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.Door;
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;

public class BlockData{
	MaterialData md = new MaterialData(Material.AIR);
	Stack<Object> extra = new Stack<Object>();
	public BlockData(){}
	public BlockData(BlockState bs){
		updateData(bs);
	}
	public BlockData(MaterialData newmd){
		md = newmd;
	}
	public static void clearBlock(BlockState bs){
		if(bs instanceof ContainerBlock){
			ContainerBlock container = (ContainerBlock) bs;
			container.getInventory().clear();
		}
	}
	private BlockFace rotate(BlockFace face, double theta){
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
			}
			theta -= Math.PI/2.0;
		}
		if(theta == Math.PI){
			face = face.getOppositeFace();
		}
		return face;
	}
	public void setBlock(Block b, double dtheta){
		b.setType(md.getItemType());
		//set rotation
		if(md instanceof Ladder){
			((Ladder)md).setFacingDirection(rotate(((Ladder)md).getAttachedFace(), dtheta));
		}else if(md instanceof Directional){
			((Directional)md).setFacingDirection(rotate(((Directional)md).getFacing(), dtheta));
		}
		
		//set extra states
		if(md instanceof Door){
			((Door)md).setTopHalf((Boolean)extra.pop());
			((Door)md).setOpen((Boolean)extra.pop());
		}
		b.setData(md.getData(), true);
		BlockState bs = b.getState();
		if(bs instanceof NoteBlock){
			((NoteBlock)bs).setRawNote((Byte)extra.pop());
		}
		if(bs instanceof Furnace){
			((Furnace)bs).setBurnTime((Short)extra.pop());
			((Furnace)bs).setCookTime((Short)extra.pop());
		}
		if(bs instanceof ContainerBlock){
			((ContainerBlock)bs).getInventory().setContents((ItemStack[])extra.pop());
		}
		if(bs instanceof Sign){
			String lines[] = (String[]) extra.pop();
			for(int i = 0; i < lines.length; i++){
				((Sign)bs).setLine(i, lines[i]);
			}
		}

		bs.update(true);
	}
	public void updateData(BlockState bs){
		md = bs.getData();
		//get extra states
		extra.clear();
		if(bs instanceof Sign){
			extra.push(((Sign)bs).getLines());
		}
		if(bs instanceof ContainerBlock){
			extra.push(((ContainerBlock)bs).getInventory().getContents());
		}
		if(bs instanceof Furnace){
			extra.push((Short)((Furnace)bs).getBurnTime());
			extra.push((Short)((Furnace)bs).getCookTime());
		}
		if(bs instanceof NoteBlock){
			extra.push((Byte)((NoteBlock)bs).getRawNote());
		}
		if(md instanceof Door){
			extra.push(((Door)md).isOpen());
			extra.push(((Door)md).isTopHalf());
		}
	}
	public Material getType(){
		return md.getItemType();
	}
}
