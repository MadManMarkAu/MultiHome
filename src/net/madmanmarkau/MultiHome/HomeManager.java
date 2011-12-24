package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/*
 * JOREN
 */

import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

/*
 * /JOREN
 */


/**
 * Manages a database of player home locations.
 * @author MadManMarkAu
 */
public class HomeManager {
	MultiHome plugin;
	
    private File homesFile;
	private HashMap<String, ArrayList<HomeLocation>> homeLocations = new HashMap<String, ArrayList<HomeLocation>>();
	private boolean enableAutoSave = true;
	private boolean saveRequired = false;
	
	public HomeManager(File homesFile, MultiHome plugin) {
		this.homesFile = homesFile;
		this.plugin = plugin;
	}

	/**
	 * Save homes list to file. Clears the saveRequired flag.
	 */
	public void saveHomes() {
		saveHomesLocal();
	}

	/**
	 * Load the homes list from file.
	 */
	public void loadHomes() {
		loadHomesLocal();
	}

	/**
	 * Enable auto-saving when changes to the homes list are made.
	 */
	public void enableAutoSave() {
		this.enableAutoSave = true;
		
		if (this.saveRequired) {
			this.saveHomesLocal();
		}
	}

	/**
	 * Disable auto-saving when changes to the homes list are made.
	 */
	public void disableAutoSave() {
		this.enableAutoSave = false;
	}

	/**
	 * Query the auto-save status.
	 */
	public boolean getAutoSave() {
		return this.enableAutoSave;
	}
	
	/**
	 * Query whether or not data save is required.
	 */
	public boolean getSaveRequired(){
		return this.saveRequired;
	}
	
	/**
	 * Clears all current homes.
	 */
	public void clearHomes() {
		this.homeLocations.clear();
	}

	/**
	 * Returns a HomeLocation object for the specified home. If home is not found, returns null. 
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	public Location getHome(Player player, String name) {
		return this.getHome(player.getName().toLowerCase(), name.toLowerCase());
	}

	/**
	 * Returns a HomeLocation object for the specified home. If home is not found, returns null. 
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	public Location getHome(String player, String name) {
		if (this.homeLocations.containsKey(player.toLowerCase())) {
			ArrayList<HomeLocation> homes = this.homeLocations.get(player.toLowerCase());
	
			for (HomeLocation thisLocation : homes) {
				if (thisLocation.getHomeName().compareToIgnoreCase(name) == 0) {
					/*
					 * JOREN
					 */
					if (plugin.getServer().getWorld(thisLocation.getWorld())==null)
					{
						Messaging.logWarning(player + "'s home \"" + thisLocation.getHomeName() + "\" is located in a world \"" + thisLocation.getWorld() + "\" which does not exist.", plugin);
						return null;
					}
					/*
					 * /JOREN
					 */
					return thisLocation.getHomeLocation(plugin.getServer());
				}
			}
		}

		return null;
	}

	/*
	 * JOREN
	 */
	
	/**
	 * If the home is inside of a denied region, returns false
	 */
	
	public boolean validHomeRegion(Player player, String name) {
		Location home = getHome(player, name);
		
		if (home == null)
			return false;
		
		WorldGuardPlugin wg = plugin.getWorldGuard();
		if (wg != null)
		{
			Vector pt = toVector(home); // This also takes a location
			LocalPlayer lp = wg.wrapPlayer(player);
			 
			RegionManager regionManager = wg.getRegionManager(home.getWorld());
			ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
			if (set.isMemberOfAll(lp))
				return true; //if they are members of a region, they can use a /home to go there and ignore this check.
			for (Iterator<ProtectedRegion> i = set.iterator(); i.hasNext();)
			{
				ProtectedRegion pr = i.next();
				if (Settings.isRegionBlocked(home.getWorld().getName(), pr.getId()))
				{
					Messaging.logInfo("Player's home " + name + " was rejected; it is inside of denied region " + pr.getId(), plugin);
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean validHomeTowny(Player player, String name) {
		Location home = getHome(player, name);
		
		if (home == null)
			return false;

		Towny towny = plugin.getTowny();
		
		if (towny != null)
		{
			Coord c = Coord.parseCoord(home);
			try{
				towny.getTownyUniverse();
				WorldCoord wc = new WorldCoord(TownyUniverse.getWorld(player.getWorld().getName()), c);
				TownBlock tb = wc.getTownBlock();
				Town t = tb.getTown();
				if (t.hasResident(player.getName()))
					return true; // Members of a town can home to any plot in the town
				else
				{
					Resident owner;
					try{
						owner = tb.getResident();
					} catch (NotRegisteredException e)
					{
						owner = t.getMayor(); // If plot is not sold, treat the mayor as owner
					}
					Resident homer = towny.getTownyUniverse().getResident(player.getName());
					if (owner.hasFriend(homer)||owner.equals(homer))
						return true; // Either the person is friends with or IS the owner.  Yes, an owner should always be a member of the town, but they may choose to change that someday?
					Messaging.logInfo("Player's home " + name + " was rejected; they are not a resident of " + t.getName() + " nor are they friends with plot owner " + owner.getName(), plugin);
					return false;
				}
			}
			catch (NotRegisteredException e)
			{
				return true; // Assuming plot is not part of Towny
			}
		}
		return true;
	}
	
	/*
	 * /JOREN
	 */
	
	/**
	 * Adds the home location for the specified player. If home location already exists, updates the location.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home.
	 * @param location Location the home.
	 */
	public void addHome(Player player, String name, Location location) {
		this.addHome(player.getName().toLowerCase(), name.toLowerCase(), location);
	}
	
	/**
	 * Adds the home location for the specified player. If home location already exists, updates the location.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home.
	 * @param location Location the home.
	 */
	public void addHome(String player, String name, Location location) {
		ArrayList<HomeLocation> homes;
		if (this.homeLocations.containsKey(player.toLowerCase())) {
			homes = this.homeLocations.get(player.toLowerCase());
		} else {
			homes = new ArrayList<HomeLocation>();
			this.saveRequired = true;
		}

		boolean homeSet = false;
		
		for (int index = 0; index < homes.size(); index++) {
			HomeLocation thisHome = homes.get(index);
			if (thisHome.getHomeName().compareToIgnoreCase(name) == 0) {
				thisHome.setHomeLocation(location);
				homes.set(index, thisHome);
				this.saveRequired = true;
				homeSet = true;
			}
		}
		
		if (!homeSet) {
			HomeLocation home = new HomeLocation(name.toLowerCase(), location);
			homes.add(home);
			this.saveRequired = true;
		}
		
		this.homeLocations.remove(player);
		this.homeLocations.put(player.toLowerCase(), homes);

		if (this.saveRequired && this.enableAutoSave) {
			this.saveHomesLocal();
		}
	}

	/**
	 * Remove an existing home.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	public void removeHome(Player player, String name) {
		removeHome(player.getName().toLowerCase(), name.toLowerCase());
	}
	
	/**
	 * Remove an existing home.
	 * @param player Owner of the home.
	 * @param name Name of the owner's home location.
	 */
	public void removeHome(String player, String name) {
		if (this.homeLocations.containsKey(player.toLowerCase())) {
			ArrayList<HomeLocation> playerHomeList = this.homeLocations.get(player.toLowerCase());
			ArrayList<HomeLocation> removeList = new ArrayList<HomeLocation>();

			for (HomeLocation thisHome : playerHomeList) {
				if (thisHome.getHomeName().compareToIgnoreCase(name) == 0) {
					removeList.add(thisHome);
					this.saveRequired = true;
				}
			}

			playerHomeList.removeAll(removeList);

			this.homeLocations.put(player.toLowerCase(), playerHomeList);

			if (this.enableAutoSave && this.saveRequired) {
				this.saveHomesLocal();
			}
		}
	}
	
	/**
	 * Check the home database for a player.
	 * @param player Player to check database for.
	 * @return boolean True if player exists in database, otherwise false.
	 */
	public boolean getUserExists(Player player) {
		return this.getUserExists(player.getName().toLowerCase());
	}
	
	/**
	 * Check the home database for a player.
	 * @param player Player to check database for.
	 * @return boolean True if player exists in database, otherwise false.
	 */
	public boolean getUserExists(String player) {
		return this.homeLocations.containsKey(player.toLowerCase());
	}

	/**
	 * Get the number of homes a player has set.
	 * @param player Player to check home list for.
	 * @return int Number of home locations set.
	 */
	public int getUserHomeCount(Player player) {
		return this.getUserHomeCount(player.getName().toLowerCase());
	}

	/**
	 * Get the number of homes a player has set.
	 * @param player Player to check home list for.
	 * @return int Number of home locations set.
	 */
	public int getUserHomeCount(String player) {
		if (this.homeLocations.containsKey(player.toLowerCase())) {
			return this.homeLocations.get(player.toLowerCase()).size();
		} else {
			return 0;
		}
	}
	
	/**
	 * Retrieve a list of player home locations from the database. If player not found, returns a blank list.
	 * @param player Player to retrieve home list for.
	 * @return ArrayList<HomeLocation> List of home locations.
	 */
	public ArrayList<HomeLocation> listUserHomes(Player player) {
		return this.listUserHomes(player.getName().toLowerCase());
	}
	
	/**
	 * Retrieve a list of player home locations from the database. If player not found, returns a blank list.
	 * @param player Player to retrieve home list for.
	 * @return ArrayList<HomeLocation> List of home locations.
	 */
	public ArrayList<HomeLocation> listUserHomes(String player) {
		if (this.homeLocations.containsKey(player.toLowerCase())) {
			return this.homeLocations.get(player.toLowerCase());
		} else {
			return new ArrayList<HomeLocation>();
		}
	}
	
	/**
	 * Save homes list to file. Clears the saveRequired flag.
	 */
	private void saveHomesLocal() {
		try {
			FileWriter fstream = new FileWriter(this.homesFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user home locations." + Util.newLine());
			writer.write("# <username>;<x>;<y>;<z>;<pitch>;<yaw>;<world>[;<name>]" + Util.newLine());
			writer.write(Util.newLine());

			for (Entry<String, ArrayList<HomeLocation>> entry : this.homeLocations.entrySet()) {
				for (HomeLocation thisLocation : entry.getValue()) {
					writer.write(entry.getKey().toLowerCase() + ";" + thisLocation.getX() + ";" + thisLocation.getY() + ";" + thisLocation.getZ() + ";"
							+ thisLocation.getPitch() + ";" + thisLocation.getYaw() + ";"
							+ thisLocation.getWorld() + ";" + thisLocation.getHomeName().toLowerCase() + Util.newLine());
				}
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the homes file.", this.plugin);
			e.printStackTrace();
		}
		
		this.saveRequired = false;
	}

	/**
	 * Load the homes list from file.
	 */
	private void loadHomesLocal() {
		// Create homes file if not exist
		if (!this.homesFile.exists()) {
			try {
				FileWriter fstream = new FileWriter(this.homesFile);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# Stores user home locations." + Util.newLine());
				out.write("# <username>;<x>;<y>;<z>;<pitch>;<yaw>;<world>[;<name>]" + Util.newLine());
				out.write(Util.newLine());

				ImportData.importHomesFromEssentials(out, this.plugin);
				ImportData.importHomesFromMultipleHomes(out, this.plugin);
				ImportData.importHomesFromMyHome(out, this.plugin);
				
				out.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not write the deafult homes file. Plugin disabled.", this.plugin);
				e.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		}

		boolean oldAutoSave = this.enableAutoSave;
		this.enableAutoSave = false;

		try {
			FileReader fstream = new FileReader(this.homesFile);
			BufferedReader reader = new BufferedReader(fstream);

			String line = reader.readLine().trim();

			this.clearHomes();

			while (line != null) {
				if (!line.startsWith("#") && line.length() > 0) {
					String[] values = line.split(";");
					double X = 0, Y = 0, Z = 0;
					float pitch = 0, yaw = 0;
					String world = "";
					String name = "";

					try {
						if (values.length == 7) {
							X = Double.parseDouble(values[1]);
							Y = Double.parseDouble(values[2]);
							Z = Double.parseDouble(values[3]);
							pitch = Float.parseFloat(values[4]);
							yaw = Float.parseFloat(values[5]);

							world = values[6];
						} else if (values.length == 8) {
							X = Double.parseDouble(values[1]);
							Y = Double.parseDouble(values[2]);
							Z = Double.parseDouble(values[3]);
							pitch = Float.parseFloat(values[4]);
							yaw = Float.parseFloat(values[5]);

							world = values[6];
							name = values[7];
						}

						if (values.length == 7 || values.length == 8) {
							ArrayList<HomeLocation> homeList;
	
							if (!this.homeLocations.containsKey(values[0].toLowerCase())) {
								homeList = new ArrayList<HomeLocation>();
							} else {
								homeList = homeLocations.get(values[0].toLowerCase());
							}
							
							// Don't save if this is a duplicate entry.
							boolean save = true;
							for (HomeLocation home : homeList) {
								if (home.getHomeName().compareToIgnoreCase(name) == 0) {
									save = false;
								}
							}
							
							if (save) {
								homeList.add(new HomeLocation(name.toLowerCase(), world, X, Y, Z, pitch, yaw));
							}
	
							homeLocations.put(values[0].toLowerCase(), homeList);
						}
					} catch (Exception e) {
						// This entry failed. Ignore and continue.
						if (line!=null) {
							Messaging.logWarning("Failed to load home location! Line: " + line, this.plugin);
						}
					}
				}

				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not read the homes file. Plugin disabled.", this.plugin);
			e.printStackTrace();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		
		this.enableAutoSave = oldAutoSave;
		this.saveRequired = false;
	}
}
