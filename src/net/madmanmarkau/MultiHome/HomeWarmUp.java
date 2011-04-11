package net.madmanmarkau.MultiHome;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HomeWarmUp implements Runnable {
	private Player player;
	private Location location;
	
	public HomeWarmUp(Player player, Location location) {
		this.player = player;
		this.location = location;
	}
	
	@Override
	public void run() {
		// Check that the player is still in fact connected
		if (this.player.isOnline()) {
			player.sendMessage(ChatColor.RED + "Teleporting now!");
			if (location.getWorld().getName().equals(player.getWorld().getName())) {
				// Direct teleport inside the current world.
				player.teleport(location);
			} else {
				// Indirect teleport between worlds.
				Location playerLoc = player.getLocation();
				
				player.teleport(new Location(location.getWorld(), playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getPitch(), playerLoc.getYaw()));
				player.teleport(location);
			}
		}
	}
}
