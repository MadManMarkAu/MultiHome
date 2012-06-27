package net.madmanmarkau.MultiHome.Data;

import net.madmanmarkau.MultiHome.MultiHome;

/**
 * @author MadManMarkAu
 */
public abstract class WarmUpManager {
	MultiHome plugin;
	
	public WarmUpManager(MultiHome plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Clears all current warmups.
	 */
	public abstract void clearWarmups();
	
	/**
	 * Returns a WarmUpEntry object for the specified warmup. If warmup is not found, returns null. 
	 * @param player Player to retrieve warmup for.
	 * @return WarmUpEntry object for this warmup. Otherwise null.
	 */
	public abstract WarmUpEntry getWarmup(String player);

	/**
	 * Adds a new warmup or updates an existing one.
	 * @param expiry Date object for when this warmup expires.
	 */
	public abstract void addWarmup(WarmUpEntry warmup);

	/**
	 * Remove an existing warmup.
	 * @param player Player to remove warmup from.
	 */
	public abstract void removeWarmup(String player);
	
	abstract void taskComplete(WarmUpEntry warmup);
}
