package net.madmanmarkau.MultiHome;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class HomeWarmUp implements Runnable {
	private MultiHome plugin;
	private Player player;
	private Date expiry;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private String world;
	private int taskID;
	private boolean taskExecuted = false;
	private double amount;
	
	public HomeWarmUp(MultiHome plugin, Player player, Date expiry, double x, double y, double z, float pitch, float yaw, String world, double amount) {
		this.plugin = plugin;
		this.player = player;
		this.expiry = expiry;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
		this.amount = amount;
	}
	
	public HomeWarmUp(MultiHome plugin, Player player, Date expiry, Location location, double amount) {
		this.plugin = plugin;
		this.player = player;
		this.expiry = expiry;
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
		this.world = location.getWorld().getName();
		this.amount = amount;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public Date getExpiry() {
		return expiry;
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getZ() {
		return z;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getYaw() {
		return yaw;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public String getWorld() {
		return world;
	}

	public double getAmount() {
		return amount;
	}
	
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public int getTaskID() {
		return taskID;
	}

	public boolean isTaskExecuted() {
		return taskExecuted;
	}
	
	public void setLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
		this.world = location.getWorld().getName();
	}

	public Location getLocation() {
		World world = this.player.getServer().getWorld(this.world);
		if (world != null) {
			return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
		}
		return null;
	}

	@Override
	public void run() {
		// Check that the player is still in fact connected
		if (this.player.isOnline()) {
			Location location = this.getLocation();
			if (location != null) {
				//Economy check before we warp the player home in case they've lost money since the warmup was created.
				if (Settings.isEconomyEnabled()){
					if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
						Settings.sendMessageNotEnoughMoney(player, amount);
						this.taskExecuted = true;
						this.plugin.warmups.callbackTaskComplete(this);
						return;
					} else
						Settings.sendMessageDeductForHome(player, amount);
				}

				Settings.sendMessageWarmupComplete(this.player);
				
				Util.teleportPlayer(this.player, location, plugin);

				int cooldownTime = Settings.getSettingCooldown(player);
				if (cooldownTime > 0) plugin.cooldowns.addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
			}
		}
		this.taskExecuted = true;
		
		this.plugin.warmups.callbackTaskComplete(this);
	}
}
