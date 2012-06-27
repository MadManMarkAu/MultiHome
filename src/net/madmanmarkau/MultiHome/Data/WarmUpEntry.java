package net.madmanmarkau.MultiHome.Data;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class WarmUpEntry {
	private final String player;
	private final Date expiry;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private String world;
	private double cost;
	
	public WarmUpEntry(String player, Date expiry, String world, double x, double y, double z, float pitch, float yaw, double cost) {
		this.player = player;
		this.expiry = expiry;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
		this.setCost(cost);
	}
	
	public WarmUpEntry(String player, Date expiry, Location location, double cost) {
		this.player = player;
		this.expiry = expiry;
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
		this.world = location.getWorld().getName();
		this.setCost(cost);
	}

	public String getPlayer() {
		return player;
	}

	public Date getExpiry() {
		return expiry;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}

	public String getWorld() {
		return world;
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
	
	public void setLocation(Location location) {
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
	}

	public Location getLocation(Server server) {
		World world = server.getWorld(this.world);
		if (world != null) {
			return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
		}
		return null;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}
