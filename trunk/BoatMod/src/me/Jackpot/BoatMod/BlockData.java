package me.Jackpot.BoatMod;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class BlockData{
	Material _type;
	byte _data;
	Object _extra;
	public BlockData(Block b){
		updateData(b);
	}
	public BlockData(Material m, byte d){
		_type = m;
		_data = d;
	}
	public void clearBlock(Block b){
		Material m = b.getType();
		if(m == Material.CHEST){
			Chest chest = (Chest) b.getState();
			chest.getInventory().clear();
		}
	}
	public void setBlock(Block b){
		Material m = _type;
		b.setTypeIdAndData(m.getId(), _data, false);
		if(m == Material.SIGN || m == Material.WALL_SIGN){
			Sign sign = (Sign)b.getState();
			String lines[] = (String[]) _extra;
			for(int i = 0; i < lines.length; i++){
				sign.setLine(i, lines[i]);
			}
		}
		else if(m == Material.CHEST){
			Chest chest = (Chest)b.getState();
			chest.getInventory().setContents((ItemStack[])_extra);
		}
	}
	public void updateData(Block b){
		Material m = b.getType();
		_type = m;
		_data = b.getData();
		if(m == Material.SIGN || m == Material.WALL_SIGN){
			Sign sign = (Sign)b.getState();
			_extra = sign.getLines();
		}
		else if(m == Material.CHEST){
			Chest chest = (Chest)b.getState();
			_extra = chest.getInventory().getContents();
		}
	}
	public Material getType(){
		return _type;
	}
	public byte getData(){
		return _data;
	}
	public void setData(byte data){
		_data = data;
	}
	public Object getExtra(){
		return _extra;
	}
	public void setExtra(Object extra){
		_extra = extra;
	}
}