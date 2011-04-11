package net.madmanmarkau.MultiHome;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class MultiHomePlayerListener extends PlayerListener {
	public final Logger log = Logger.getLogger("Minecraft");
	MultiHome plugin;
	
	public MultiHomePlayerListener(MultiHome plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (plugin.Permissions.has(player, "MultiHome.homeondeath") && plugin.Config.getBoolean("MultiHome.enableHomeOnDeath", false)) {
			Location location = plugin.getPlayerHomeLocation(player, "");
			
			if (location != null) {
				event.setRespawnLocation(location);
			}
		}
	}
}
