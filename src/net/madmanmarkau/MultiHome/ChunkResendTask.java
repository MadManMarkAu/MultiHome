package net.madmanmarkau.MultiHome;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkResendTask implements Runnable {
	Location loc;
	
	public ChunkResendTask(Location loc) {
		this.loc = loc;
	}
	
	@Override
	public void run() {
		Chunk chunk = this.loc.getWorld().getChunkAt(this.loc);

		this.loc.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
	}
}
