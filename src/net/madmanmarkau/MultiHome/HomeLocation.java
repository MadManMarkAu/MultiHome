package net.madmanmarkau.MultiHome;

import org.bukkit.Location;

public class HomeLocation {
	private String homeName = "";
	private Location homeLocation;
	
	public HomeLocation() {}
	public HomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation;
	}
	public HomeLocation(String homeName, Location homeLocation) {
		this.homeLocation = homeLocation;
		this.homeName = homeName;
	}
	
	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}
	
	public String getHomeName() {
		return homeName;
	}

	public void setHomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation;
	}

	public Location getHomeLocation() {
		return homeLocation;
	}
}
