package net.madmanmarkau.MultiHome;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MultiHomePlayerListener extends PlayerListener {
	MultiHome plugin;
	
	public MultiHomePlayerListener(MultiHome plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (HomePermissions.has(player, "multihome.homeondeath") && Settings.isHomeOnDeathEnabled()) {
			Location location = plugin.homes.getHome(player, "");
			
			if (location != null) {
				event.setRespawnLocation(location);
			}
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		String player = event.getPlayer().getName();
		
		plugin.warmups.removeWarmup(player);
	}
}
