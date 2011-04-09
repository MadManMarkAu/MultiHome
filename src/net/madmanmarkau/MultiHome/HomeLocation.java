package net.madmanmarkau.MultiHome;

import org.bukkit.Location;
import org.bukkit.Server;

public class HomeLocation {
	private String homeName = "";
	private String world = "";
	private double X = 0, Y = 0, Z = 0;
	private float yaw = 0, pitch = 0;

	public HomeLocation() {}
	
	public HomeLocation(String world, double X, double Y, double Z, float pitch, float yaw) {
		this.setWorld(world);
		this.setX(X);
		this.setY(Y);
		this.setZ(Z);
		this.setPitch(pitch);
		this.setYaw(yaw);
	}
	
	public HomeLocation(String homeName, String world, double X, double Y, double Z, float pitch, float yaw) {
		this.setHomeName(homeName);
		this.setWorld(world);
		this.setX(X);
		this.setY(Y);
		this.setZ(Z);
		this.setPitch(pitch);
		this.setYaw(yaw);
	}

	public HomeLocation(Location location) {
		this.setWorld(location.getWorld().getName());
		this.setX(location.getX());
		this.setY(location.getY());
		this.setZ(location.getZ());
		this.setPitch(location.getPitch());
		this.setYaw(location.getYaw());
	}
	
	public HomeLocation(String homeName, Location location) {
		this.setHomeName(homeName);
		this.setWorld(location.getWorld().getName());
		this.setX(location.getX());
		this.setY(location.getY());
		this.setZ(location.getZ());
		this.setPitch(location.getPitch());
		this.setYaw(location.getYaw());
	}

	public void setHomeLocation(String world, double X, double Y, double Z, float pitch, float yaw) {
		this.setWorld(world);
		this.setX(X);
		this.setY(Y);
		this.setZ(Z);
		this.setPitch(pitch);
		this.setYaw(yaw);
	}
	
	public String getHomeName() {
		return homeName;
	}
	
	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public Location getHomeLocation(Server server) {
		return new Location(server.getWorld(this.world), this.X, this.Y, this.Z, this.yaw, this.pitch);
	}

	public void setHomeLocation(Location location) {
		this.setWorld(location.getWorld().getName());
		this.setX(location.getX());
		this.setY(location.getY());
		this.setZ(location.getZ());
		this.setPitch(location.getPitch());
		this.setYaw(location.getYaw());
	}
	
	public void setWorld(String world) {
		this.world = world;
	}

	public String getWorld() {
		return world;
	}

	public void setX(double X) {
		this.X = X;
	}

	public double getX() {
		return X;
	}

	public void setY(double Y) {
		this.Y = Y;
	}

	public double getY() {
		return Y;
	}

	public void setZ(double Z) {
		this.Z = Z;
	}

	public double getZ() {
		return Z;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getYaw() {
		return yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}
}
