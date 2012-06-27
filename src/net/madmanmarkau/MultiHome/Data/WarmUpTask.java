package net.madmanmarkau.MultiHome.Data;

import java.util.Date;

import net.madmanmarkau.MultiHome.MultiHome;
import net.madmanmarkau.MultiHome.MultiHomeEconManager;
import net.madmanmarkau.MultiHome.Settings;
import net.madmanmarkau.MultiHome.Util;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

/*
 * This class should not be visible outside the Data package.
 */

class WarmUpTask implements Runnable {
	private final MultiHome plugin;
	private final WarmUpEntry warmup;
	private final int taskId;
	
	public WarmUpTask(MultiHome plugin, WarmUpEntry warmup) {
		this.plugin = plugin;
		this.warmup = warmup;

		long delay = (this.warmup.getExpiry().getTime() - (new Date()).getTime()) / 50;
		if (delay < 1) delay = 1;

		this.taskId = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this, delay);
	}

	public void cancelWarmUp() {
		this.plugin.getServer().getScheduler().cancelTask(taskId);
	}

	public WarmUpEntry getWarmup() {
		return this.warmup;
	}
	
	@Override
	public void run() {
		Server server;
		Player player;
		
		server = this.plugin.getServer();
		player = server.getPlayerExact(this.warmup.getPlayer());

		if (player != null && player.isOnline()) {
			Location location = this.warmup.getLocation(server);
			if (location != null) {
				//Economy check before we warp the player home in case they've lost money since the warmup was created.
				if (Settings.isEconomyEnabled()){
					if (!MultiHomeEconManager.chargePlayer(player.getName(), this.warmup.getCost())) {
						Settings.sendMessageNotEnoughMoney(player, this.warmup.getCost());
						this.plugin.getWarmUpManager().taskComplete(this.warmup);
						return;
					} else
						Settings.sendMessageDeductForHome(player, this.warmup.getCost());
				}

				Settings.sendMessageWarmupComplete(player);
				
				Util.teleportPlayer(player, location, this.plugin);

				int cooldownTime = Settings.getSettingCooldown(player);
				if (cooldownTime > 0) this.plugin.getCoolDownManager().addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
			}
		}
		
		this.plugin.getWarmUpManager().taskComplete(this.warmup);
	}
}
