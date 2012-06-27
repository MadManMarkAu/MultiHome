package net.madmanmarkau.MultiHome.Data;

import java.util.ArrayList;

import net.madmanmarkau.MultiHome.MultiHome;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Base class for home location database objects.
 * @author MadManMarkAu
 */
public abstract class HomeManager {
	protected final MultiHome plugin;
	
	/**
	 * @param plugin The plug-in.
	 */
	public HomeManager(MultiHome plugin) {
		this.plugin = plugin;
	}

	/**
	 * Deletes all homes from the database.
	 */
	abstract public void clearHomes();

	/**
	 * Returns a HomeEntry object for the specified home. If home is not found, returns null. 
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	public final HomeEntry getHome(Player player, String name) {
		return this.getHome(player.getName(), name);
	}

	/**
	 * Returns a HomeEntry object for the specified home. If home is not found, returns null. 
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	abstract public HomeEntry getHome(String player, String name);

	/**
	 * Adds the home location for the specified player. If home location already exists, updates the location.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home.
	 * @param location Location the home.
	 */
	public final void addHome(Player player, String name, Location location) {
		this.addHome(player.getName(), name, location);
	}
	
	/**
	 * Adds the home location for the specified player. If home location already exists, updates the location.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home.
	 * @param location Location the home.
	 */
	abstract public void addHome(String player, String name, Location location);

	/**
	 * Remove an existing home.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	public final void removeHome(Player player, String name) {
		this.removeHome(player.getName(), name);
	}
	
	/**
	 * Remove an existing home.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	abstract public void removeHome(String player, String name);
	
	/**
	 * Check the home database for a player.
	 * @param player Player to check database for.
	 * @return boolean True if player exists in database, otherwise false.
	 */
	public final boolean getUserExists(Player player) {
		return this.getUserExists(player.getName());
	}
	
	/**
	 * Check the home database for a player.
	 * @param player Player to check database for.
	 * @return boolean True if player exists in database, otherwise false.
	 */
	abstract public boolean getUserExists(String player);

	/**
	 * Get the number of homes a player has set.
	 * @param player Player to check home list for.
	 * @return int Number of home locations set.
	 */
	public final int getUserHomeCount(Player player) {
		return this.getUserHomeCount(player.getName());
	}

	/**
	 * Get the number of homes a player has set.
	 * @param player Player to check home list for.
	 * @return int Number of home locations set.
	 */
	abstract public int getUserHomeCount(String player);
	
	/**
	 * Retrieve a list of player home locations from the database. If player not found, returns a blank list.
	 * @param player Player to retrieve home list for.
	 * @return ArrayList<HomeEntry> List of home locations.
	 */
	public final ArrayList<HomeEntry> listUserHomes(Player player) {
		return this.listUserHomes(player.getName());
	}
	
	/**
	 * Retrieve a list of player home locations from the database. If player not found, returns a blank list.
	 * @param player Player to retrieve home list for.
	 * @return ArrayList<HomeEntry> List of home locations.
	 */
	abstract public ArrayList<HomeEntry> listUserHomes(String player);
	
	/**
	 * Imports the list of home locations passed. Does not overwrite existing home locations.
	 * @param homes List of players and homes to import.
	 * @param overwrite True to overwrite existing entries.
	 */
	abstract public void importHomes(ArrayList<HomeEntry> homes, boolean overwrite);
}
