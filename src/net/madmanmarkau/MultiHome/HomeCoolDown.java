package net.madmanmarkau.MultiHome;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Static class to store cooldown timers.
 * @author MadManMarkAu
 */
public class HomeCoolDown {
	private static HashMap<String, Date> cooldownTime = new HashMap<String, Date>();
	
	/**
	 * Query whether or not a players cooldown timer has expired. Send a message to the player if it hasn't.
	 * @param player (Player) Player to query for cooldown.
	 * @return (boolean) True indicating player cooldown has expired, otherwise false.
	 */
	public static boolean userMayTeleport(Player player) {
		Date time = null;
		
		// Find player in the list
		if (cooldownTime.containsKey(player.getName())) {
			time = cooldownTime.get(player.getName());
		}
		
		if (time != null) {
			// Player found. "time" contains when the player may teleport next.
			Date now = new Date();
			
			if (now.compareTo(time) < 0) {
				// Player needs more cool-down time.
				player.sendMessage(ChatColor.RED + "You may not teleport yet. Please wait another " + Math.round((time.getTime() - now.getTime()) / 1000) + " seconds.");
				return false;
			}
			
			// Player is allowed to teleport. Remove them from the cooldown list.
			cooldownTime.remove(player.getName());
		}
		
		return true;
	}
	
	/**
	 * Adds a new (or updates an existing) cooldown timer.
	 * @param player (String) Player to apply cooldown to.
	 * @param timeInSeconds (int) Time (in seconds) this cooldown should last for.
	 */
	public static void addCooldown(String player, int timeInSeconds) {
		// Check that it's actually worth saving the information
		if (timeInSeconds > 0) {
			// Check for an existing cooldown timer.
			if (cooldownTime.containsKey(player)) {
				// Remove it.
				cooldownTime.remove(player);
			}
			
			Date now = new Date();
			
			// Add new cooldown timer.
			Calendar cal = new GregorianCalendar();
			cal.setTime(now);
			cal.add(Calendar.SECOND, timeInSeconds);
			cooldownTime.put(player, cal.getTime());
		}
	}
	
	/**
	 * Removes a cooldown timer.
	 * @param player (String) Player to remove cooldown from.
	 */
	public static void removeCooldown(String player) {
		// Check that the cooldown exists.
		if (cooldownTime.containsKey(player)) {
			// Remove the cooldown timer
			cooldownTime.remove(player);
		}
	}
	
	/**
	 * Clears all the cooldown timers.
	 */
	public static void clearCooldowns() {
		// No checks, just dump the cooldown timers.
		cooldownTime.clear();
	}
}
