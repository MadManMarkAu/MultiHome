package net.madmanmarkau.MultiHome;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
* @author Sleaker
*/
public class MultiHomeEntityListener implements Listener {
	MultiHome plugin;

	MultiHomeEntityListener(MultiHome plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player))
			return;
		else {
			Player player = (Player) event.getEntity();
			if (plugin.getWarmUpManager().getWarmup(player.getName().toLowerCase()) != null && Settings.getSettingDisrupt(player)) {
				plugin.getWarmUpManager().removeWarmup(player.getName().toLowerCase());
				Settings.sendMessageWarmupDisrupted(player);
			}
		}
	}
}

