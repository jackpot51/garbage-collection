package me.Jackpot.BoatMod;

import org.bukkit.block.Block;

public class NMSBlockSetter {
	public static boolean setBlockFast(Block b, int blockId, byte data){
		net.minecraft.server.v1_7_R1.World w = ((org.bukkit.craftbukkit.v1_7_R1.CraftWorld)b.getWorld()).getHandle();
		net.minecraft.server.v1_7_R1.Chunk chunk = w.getChunkAt(b.getX() >> 4, b.getZ() >> 4);
		return chunk.a(b.getX() & 0x0f, b.getY(), b.getZ() & 0x0f, net.minecraft.server.v1_7_R1.Block.e(blockId), data);
	}
}
