package net.madmanmarkau.MultiHome;

import net.madmanmarkau.MultiHome.Data.HomeEntry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MultiHomePlayerListener implements Listener {
	MultiHome plugin;
	
	public MultiHomePlayerListener(MultiHome plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (HomePermissions.has(player, "multihome.homeondeath") && Settings.isHomeOnDeathEnabled()) {
			HomeEntry homeEntry = plugin.getHomeManager().getHome(player, "");
			
			if (homeEntry != null) {
				event.setRespawnLocation(homeEntry.getHomeLocation(plugin.getServer()));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		String player = event.getPlayer().getName();
		
		plugin.getWarmUpManager().removeWarmup(player);
	}
}
