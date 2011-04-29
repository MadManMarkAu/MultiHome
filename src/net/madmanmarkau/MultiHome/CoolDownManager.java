package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author MadManMarkAu
 */
public class CoolDownManager {
	MultiHome plugin;
	
    private File cooldownFile;
	private HashMap<String, Date> homeCooldowns = new HashMap<String, Date>();
	private boolean enableAutoSave = true;
	private boolean saveRequired = false;
	
	public CoolDownManager(File cooldownFile, MultiHome plugin) {
		this.cooldownFile = cooldownFile;
		this.plugin = plugin;
	}
	
	/**
	 * Save cooldowns list to file. Clears the saveRequired flag.
	 */
	public void loadCooldowns() {
		loadCooldownsLocal();
		updateCooldownExpiry();
	}
	
	/**
	 * Save cooldowns list to file. Clears the saveRequired flag.
	 */
	public void saveCooldowns() {
		updateCooldownExpiry();
		saveCooldownsLocal();
	}

	/**
	 * Enable auto-saving when changes to the cooldown list are made.
	 */
	public void enableAutoSave() {
		this.enableAutoSave = true;
		
		if (this.saveRequired) {
			this.saveCooldownsLocal();
		}
	}

	/**
	 * Disable auto-saving when changes to the cooldown list are made.
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
	 * Clears all current cooldowns.
	 */
	public void clearCooldowns() {
		this.homeCooldowns.clear();
	}

	/**
	 * Returns the expiry time for the specified cooldown. If cooldown is not found, returns null. 
	 * @param player Player to retrieve cooldown for.
	 * @return Date object for this cooldown. Otherwise null.
	 */
	public Date getCooldown(String player) {
		updateCooldownExpiry();
		if (this.homeCooldowns.containsKey(player.toLowerCase())) {
			return this.homeCooldowns.get(player.toLowerCase());
		}
		
		return null;
	}

	/**
	 * Adds a new cooldown or updates an existing one.
	 * @param player Player to set cooldown on.
	 * @param expiry Date object for when this cooldown expires.
	 */
	public void addCooldown(String player, Date expiry) {
		this.homeCooldowns.put(player.toLowerCase(), expiry);
		this.saveRequired = true;

		updateCooldownExpiry();

		if (this.enableAutoSave && this.saveRequired) {
			this.saveCooldownsLocal();
		}
	}

	/**
	 * Remove an existing cooldown.
	 * @param player Player to remove cooldown from.
	 */
	public void removeCooldown(String player) {
		this.homeCooldowns.remove(player.toLowerCase());
		this.saveRequired = true;
		
		if (this.enableAutoSave && this.saveRequired) {
			this.saveCooldownsLocal();
		}
	}

	/**
	 * Scans through the cooldown list, removing expired cooldowns.
	 */
	private void updateCooldownExpiry() {
		Date now = new Date();

		// Remove expired cooldowns.
		ArrayList<String> removeList = new ArrayList<String>();
		for (Entry<String, Date> entry : this.homeCooldowns.entrySet()) {
			if (entry.getValue().getTime() <= now.getTime()) {
				removeList.add(entry.getKey());
			}
		}
		
		for (String entry : removeList) {
			this.homeCooldowns.remove(entry);
			this.saveRequired = true;
		}
		
		if (this.saveRequired && this.enableAutoSave) {
			this.saveCooldownsLocal();
		}
	}

	/**
	 * Saves cooldowns to data folder.
	 */
	private void saveCooldownsLocal() {
		try {
			FileWriter fstream = new FileWriter(this.cooldownFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user cooldown times." + Util.newLine());
			writer.write("# <username>;<expiry>" + Util.newLine());
			writer.write(Util.newLine());

			for (Entry<String, Date> entry : this.homeCooldowns.entrySet()) {
				writer.write(entry.getKey().toLowerCase() + ";" + Long.toString(entry.getValue().getTime()) + Util.newLine());
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the cooldowns file.", this.plugin);
			e.printStackTrace();
		}
		
		this.saveRequired = false;
	}

	/**
	 * Load the cooldown list from file.
	 */
	private void loadCooldownsLocal() {
		// Create cooldown file if not exist
		if (!this.cooldownFile.exists()) {
			try {
				FileWriter fstream = new FileWriter(this.cooldownFile);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# Stores user cooldown times." + Util.newLine());
				out.write("# <username>;<expiry>" + Util.newLine());
				out.write(Util.newLine());
				
				out.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not write the deafult cooldown file. Plugin disabled.", this.plugin);
				e.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		}

		boolean oldAutoSave = this.enableAutoSave;
		this.enableAutoSave = false;
		Date now = new Date();
		
		try {
			FileReader fstream = new FileReader(this.cooldownFile);
			BufferedReader reader = new BufferedReader(fstream);

			String line = reader.readLine().trim();

			this.clearCooldowns();

			while (line != null) {
				if (!line.startsWith("#") && line.length() > 0) {
					String[] values = line.split(";");

					try {
						if (values.length == 2) {
							Date expiry = new Date(Long.parseLong(values[1]));
							
							if (expiry.getTime() > now.getTime()) {
								addCooldown(values[0].toLowerCase(), expiry);
							}
						}
					} catch (Exception e) {
						// This entry failed. Ignore and continue.
						if (line!=null) {
							Messaging.logWarning("Failed to load cooldown timer! Line: " + line, this.plugin);
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
