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
import org.bukkit.material.Ladder;
import org.bukkit.material.MaterialData;

public class BlockData{
	MaterialData _data;
	Stack<Object> _extra = new Stack<Object>();
	public BlockData(Block b){
		updateData(b);
	}
	public BlockData(Material m){
		_data = new MaterialData(m, (byte) 0);
	}
	public static void clearBlock(Block b){
		if(b.getState() instanceof ContainerBlock){
			ContainerBlock container = (ContainerBlock) b.getState();
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
		MaterialData md = _data;
		b.setTypeId(md.getItemTypeId());
		//set rotation
		if(md instanceof Ladder){
			((Ladder)md).setFacingDirection(rotate(((Ladder)md).getAttachedFace() ,dtheta));
		}
		else if(md instanceof Directional){
			((Directional)md).setFacingDirection(rotate(((Directional)md).getFacing(), dtheta));
		}
		//set extra states
		BlockState bs = b.getState();
		if(bs instanceof NoteBlock){
			((NoteBlock)bs).setRawNote((Byte)_extra.pop());
		}
		if(bs instanceof Furnace){
			((Furnace)bs).setBurnTime((Short)_extra.pop());
			((Furnace)bs).setCookTime((Short)_extra.pop());
		}
		if(bs instanceof ContainerBlock){
			((ContainerBlock)bs).getInventory().setContents((ItemStack[])_extra.pop());
		}
		if(bs instanceof Sign){
			String lines[] = (String[]) _extra.pop();
			for(int i = 0; i < lines.length; i++){
				((Sign)bs).setLine(i, lines[i]);
			}
		}
		b.setData(md.getData(), true);
	}
	public void updateData(Block b){
		BlockState bs = b.getState();
		_data = bs.getData();
		//get extra states
		_extra.clear();
		if(bs instanceof Sign){
			_extra.push(((Sign)bs).getLines());
		}
		if(bs instanceof ContainerBlock){
			_extra.push(((ContainerBlock)bs).getInventory().getContents());
		}
		if(bs instanceof Furnace){
			_extra.push((Short)((Furnace)bs).getBurnTime());
			_extra.push((Short)((Furnace)bs).getCookTime());
		}
		if(bs instanceof NoteBlock){
			_extra.push((Byte)((NoteBlock)bs).getRawNote());
		}
	}
	public Material getType(){
		return _data.getItemType();
	}
}
