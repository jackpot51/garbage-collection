package me.Jackpot.BoatMod;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Furnace;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public class BlockData{
	Material _type;
	byte _data;
	ArrayList<Object> _extra = new ArrayList<Object>();
	public BlockData(Block b){
		updateData(b);
	}
	public BlockData(Material m, byte d){
		_type = m;
		_data = d;
	}
	public void clearBlock(Block b){
		if(b.getState() instanceof ContainerBlock){
			ContainerBlock container = (ContainerBlock) b.getState();
			container.getInventory().clear();
		}
	}
	public void setBlock(Block b){
		Material m = _type;
		b.setTypeIdAndData(m.getId(), _data, true);
		int extrai = 0;
		if(b.getState() instanceof Sign){
			Sign sign = (Sign)b.getState();
			String lines[] = (String[]) _extra.get(extrai);
			extrai++;
			for(int i = 0; i < lines.length; i++){
				sign.setLine(i, lines[i]);
			}
		}
		if(b.getState() instanceof ContainerBlock){
			ContainerBlock container = (ContainerBlock)b.getState();
			container.getInventory().setContents((ItemStack[])_extra.get(extrai));
			extrai++;
		}
		if(b.getState() instanceof Furnace){
			Furnace furnace = (Furnace)b.getState();
			furnace.setBurnTime((Short)_extra.get(extrai));
			extrai++;
			furnace.setCookTime((Short)_extra.get(extrai));
			extrai++;
		}
		if(b.getState() instanceof NoteBlock){
			NoteBlock noteblock = (NoteBlock)b.getState();
			noteblock.setNote((Byte)_extra.get(extrai));
			extrai++;
		}
	}
	public void updateData(Block b){
		Material m = b.getType();
		_type = m;
		_data = b.getData();
		_extra.clear();
		if(b.getState() instanceof Sign){
			Sign sign = (Sign)b.getState();
			_extra.add(sign.getLines());
		}
		if(b.getState() instanceof ContainerBlock){
			ContainerBlock container = (ContainerBlock)b.getState();
			_extra.add(container.getInventory().getContents());
		}
		if(b.getState() instanceof Furnace){
			Furnace furnace = (Furnace)b.getState();
			_extra.add((Short)furnace.getBurnTime());
			_extra.add((Short)furnace.getCookTime());
		}
		if(b.getState() instanceof NoteBlock){
			NoteBlock noteblock = (NoteBlock)b.getState();
			_extra.add((Byte)noteblock.getNote());
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
}