package net.madmanmarkau.MultiHome.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.madmanmarkau.MultiHome.Messaging;
import net.madmanmarkau.MultiHome.MultiHome;
import net.madmanmarkau.MultiHome.Util;

import org.bukkit.Location;

/**
 * Manages a database of player home locations.
 * @author MadManMarkAu
 */

public class HomeManagerFile extends HomeManager {
    private final File homesFile;
	private HashMap<String, ArrayList<HomeEntry>> homeEntries = new HashMap<String, ArrayList<HomeEntry>>();
	
	public HomeManagerFile(MultiHome plugin) {
		super(plugin);
		this.homesFile = new File(plugin.getDataFolder(), "homes.txt");
		
		loadHomes();
	}

	@Override
	public void clearHomes() {
		this.homeEntries.clear();

		saveHomes();
	}

	@Override
	public HomeEntry getHome(String player, String name) {
		if (this.homeEntries.containsKey(player.toLowerCase())) {
			ArrayList<HomeEntry> homes = this.homeEntries.get(player.toLowerCase());
	
			for (HomeEntry thisLocation : homes) {
				if (thisLocation.getHomeName().compareToIgnoreCase(name) == 0) {
					return thisLocation;
				}
			}
		}

		return null;
	}

	@Override
	public void addHome(String player, String name, Location location) {
		ArrayList<HomeEntry> homes;
		
		// Get the ArrayList of homes for this player
		if (this.homeEntries.containsKey(player.toLowerCase())) {
			homes = this.homeEntries.get(player.toLowerCase());
		} else {
			homes = new ArrayList<HomeEntry>();
		}

		boolean homeSet = false;
		
		for (int index = 0; index < homes.size(); index++) {
			HomeEntry thisHome = homes.get(index);
			if (thisHome.getHomeName().compareToIgnoreCase(name) == 0) {
				// An existing home was found. Overwrite it.
				thisHome.setOwnerName(player);
				thisHome.setHomeName(name);
				thisHome.setHomeLocation(location);
				homes.set(index, thisHome);
				homeSet = true;
			}
		}
		
		if (!homeSet) {
			// No existing location found. Create new entry.
			HomeEntry home = new HomeEntry(player, name.toLowerCase(), location);
			homes.add(home);
		}
		
		// Replace the ArrayList in the homes HashMap
		this.homeEntries.remove(player.toLowerCase());
		this.homeEntries.put(player.toLowerCase(), homes);

		// Save
		this.saveHomes();
	}

	@Override
	public void removeHome(String player, String name) {
		if (this.homeEntries.containsKey(player.toLowerCase())) {
			ArrayList<HomeEntry> playerHomeList = this.homeEntries.get(player.toLowerCase());
			ArrayList<HomeEntry> removeList = new ArrayList<HomeEntry>();

			// Find all homes matching "name"
			for (HomeEntry thisHome : playerHomeList) {
				if (thisHome.getHomeName().compareToIgnoreCase(name) == 0) {
					// Found match. Mark it for deletion.
					removeList.add(thisHome);
				}
			}

			// Remove all matching homes.
			playerHomeList.removeAll(removeList);

			// Replace the ArrayList in the homes HashMap
			this.homeEntries.remove(player.toLowerCase());
			if (!playerHomeList.isEmpty()) {
				this.homeEntries.put(player.toLowerCase(), playerHomeList);
			}

			// Save
			this.saveHomes();
		}
	}

	@Override
	public boolean getUserExists(String player) {
		return this.homeEntries.containsKey(player.toLowerCase());
	}

	@Override
	public int getUserHomeCount(String player) {
		if (this.homeEntries.containsKey(player.toLowerCase())) {
			return this.homeEntries.get(player.toLowerCase()).size();
		} else {
			return 0;
		}
	}

	@Override
	public ArrayList<HomeEntry> listUserHomes(String player) {
		if (this.homeEntries.containsKey(player.toLowerCase())) {
			return this.homeEntries.get(player.toLowerCase());
		} else {
			return new ArrayList<HomeEntry>();
		}
	}

	@Override
	public void importHomes(ArrayList<HomeEntry> homes, boolean overwrite) {
		ArrayList<HomeEntry> playerHomes;

		for (HomeEntry thisEntry : homes) {
			// Get the ArrayList of homes for this player
			if (this.homeEntries.containsKey(thisEntry.getOwnerName().toLowerCase())) {
				playerHomes = this.homeEntries.get(thisEntry.getOwnerName().toLowerCase());
			} else {
				playerHomes = new ArrayList<HomeEntry>();
			}

			boolean homeFound = false;
			
			for (int index = 0; index < playerHomes.size(); index++) {
				HomeEntry thisHome = playerHomes.get(index);
				if (thisHome.getHomeName().compareToIgnoreCase(thisEntry.getHomeName()) == 0) {
					// An existing home was found.
					if (overwrite) {
						thisHome.setOwnerName(thisEntry.getOwnerName());
						thisHome.setHomeName(thisEntry.getHomeName());
						thisHome.setHomeLocation(thisEntry.getHomeLocation(plugin.getServer()));
						playerHomes.set(index, thisHome);
					}
					
					homeFound = true;
				}
			}
			
			if (!homeFound) {
				// No existing location found. Create new entry.
				HomeEntry newHome = new HomeEntry(thisEntry.getOwnerName(), thisEntry.getHomeName(), thisEntry.getHomeLocation(plugin.getServer()));
				playerHomes.add(newHome);
			}

			// Replace the ArrayList in the homes HashMap
			this.homeEntries.remove(thisEntry.getOwnerName().toLowerCase());
			this.homeEntries.put(thisEntry.getOwnerName().toLowerCase(), playerHomes);
		}

		// Save
		this.saveHomes();
	}

	
	
	/**
	 * Save homes list to file. Clears the saveRequired flag.
	 */
	private void saveHomes() {
		try {
			FileWriter fstream = new FileWriter(this.homesFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user home locations." + Util.newLine());
			writer.write("# <username>;<x>;<y>;<z>;<pitch>;<yaw>;<world>[;<name>]" + Util.newLine());
			writer.write(Util.newLine());

			for (Entry<String, ArrayList<HomeEntry>> entry : this.homeEntries.entrySet()) {
				for (HomeEntry thisHome : entry.getValue()) {
					writer.write(thisHome.getOwnerName() + ";" + thisHome.getX() + ";" + thisHome.getY() + ";" + thisHome.getZ() + ";"
							+ thisHome.getPitch() + ";" + thisHome.getYaw() + ";"
							+ thisHome.getWorld() + ";" + thisHome.getHomeName() + Util.newLine());
				}
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the homes file.", this.plugin);
		}
	}

	/**
	 * Load the homes list from file.
	 */
	private void loadHomes() {
		if (this.homesFile.exists()) {
			try {
				FileReader fstream = new FileReader(this.homesFile);
				BufferedReader reader = new BufferedReader(fstream);
	
				String line = reader.readLine().trim();
	
				this.homeEntries.clear();
	
				while (line != null) {
					if (!line.startsWith("#") && line.length() > 0) {
						HomeEntry thisHome;
						
						thisHome = parseHomeLine(line);
						
						if (thisHome != null) {
							ArrayList<HomeEntry> homeList;
	
							// Find HashMap entry for player
							if (!this.homeEntries.containsKey(thisHome.getOwnerName().toLowerCase())) {
								homeList = new ArrayList<HomeEntry>();
							} else {
								// Player not exist. Create dummy entry.
								homeList = this.homeEntries.get(thisHome.getOwnerName().toLowerCase());
							}
							
							// Don't save if this is a duplicate entry.
							boolean save = true;
							for (HomeEntry home : homeList) {
								if (home.getHomeName().compareToIgnoreCase(thisHome.getHomeName()) == 0) {
									save = false;
								}
							}
							
							if (save) {
								homeList.add(thisHome);
							}
	
							this.homeEntries.put(thisHome.getOwnerName().toLowerCase(), homeList);
						}
					}
	
					line = reader.readLine();
				}
	
				reader.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not read the homes file.", this.plugin);
				return;
			}
		}
		
		saveHomes();
	}


	private HomeEntry parseHomeLine(String line) {
		String[] values = line.split(";");
		double X = 0, Y = 0, Z = 0;
		float pitch = 0, yaw = 0;
		String world = "";
		String name = "";
		String player = "";

		try {
			if (values.length == 7) {
				player = values[0];
				X = Double.parseDouble(values[1]);
				Y = Double.parseDouble(values[2]);
				Z = Double.parseDouble(values[3]);
				pitch = Float.parseFloat(values[4]);
				yaw = Float.parseFloat(values[5]);

				world = values[6];
				name = "";
			} else if (values.length == 8) {
				player = values[0];
				X = Double.parseDouble(values[1]);
				Y = Double.parseDouble(values[2]);
				Z = Double.parseDouble(values[3]);
				pitch = Float.parseFloat(values[4]);
				yaw = Float.parseFloat(values[5]);

				world = values[6];
				name = values[7];
			}
		} catch (Exception e) {
			// This entry failed. Ignore and continue.
			if (line!=null) {
				Messaging.logWarning("Failed to load home location! Line: " + line, this.plugin);
			}
		}

		if (values.length == 7 || values.length == 8) {
			return new HomeEntry(player, name, world, X, Y, Z, pitch, yaw);
		}
		
		return null;
	}
}
