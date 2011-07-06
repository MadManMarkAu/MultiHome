package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * @author MadManMarkAu
 */
public class WarmUpManager {
	MultiHome plugin;
	
    private File warmupFile;
	private HashMap<String, HomeWarmUp> homeWarmups = new HashMap<String, HomeWarmUp>();
	private boolean enableAutoSave = true;
	private boolean saveRequired = false;
	
	public WarmUpManager(File warmupFile, MultiHome plugin) {
		this.warmupFile = warmupFile;
		this.plugin = plugin;
	}
	
	/**
	 * Save warmups list to file. Clears the saveRequired flag.
	 */
	public void loadWarmups() {
		loadWarmupsLocal();
	}
	
	/**
	 * Save warmups list to file. Clears the saveRequired flag.
	 */
	public void saveWarmups() {
		saveWarmupsLocal();
	}

	/**
	 * Enable auto-saving when changes to the warmup list are made.
	 */
	public void enableAutoSave() {
		this.enableAutoSave = true;
		
		if (this.saveRequired) {
			this.saveWarmupsLocal();
		}
	}

	/**
	 * Disable auto-saving when changes to the warmup list are made.
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
	 * Clears all current warmups.
	 */
	public void clearWarmups() {
		BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
		
		for (Entry<String, HomeWarmUp> entry : this.homeWarmups.entrySet()) {
			scheduler.cancelTask(entry.getValue().getTaskID());
		}
		this.homeWarmups.clear();
	}
	
	/**
	 * Returns a HomeWarmUp object for the specified warmup. If warmup is not found, returns null. 
	 * @param player Player to retrieve warmup for.
	 * @return HomeWarmUp object for this warmup. Otherwise null.
	 */
	public HomeWarmUp getWarmup(String player) {
		if (this.homeWarmups.containsKey(player.toLowerCase())) {
			return this.homeWarmups.get(player.toLowerCase());
		}
		
		return null;
	}

	/**
	 * Adds a new warmup or updates an existing one.
	 * @param player Player to set warmup on.
	 * @param expiry Date object for when this warmup expires.
	 */
	public void addWarmup(String player, HomeWarmUp warmup) {
		BukkitScheduler scheduler = this.plugin.getServer().getScheduler();

		if (this.homeWarmups.containsKey(player.toLowerCase())) {
			// Remove old warmup
			HomeWarmUp oldWarmup = this.homeWarmups.get(player.toLowerCase());

			scheduler.cancelTask(oldWarmup.getTaskID());
			
			this.homeWarmups.remove(player.toLowerCase());
			this.saveRequired = true;
		}

		// Set new warmup
		long delay = (warmup.getExpiry().getTime() - (new Date()).getTime()) / 50;
		if (delay < 1) delay = 1;

		int taskID = scheduler.scheduleSyncDelayedTask(this.plugin, warmup, delay);
		warmup.setTaskID(taskID);
		this.homeWarmups.put(player.toLowerCase(), warmup);
		this.saveRequired = true;
		
		if (this.enableAutoSave && this.saveRequired) {
			this.saveWarmupsLocal();
		}
	}

	/**
	 * Remove an existing warmup.
	 * @param player Player to remove warmup from.
	 */
	public void removeWarmup(String player) {
		if (this.homeWarmups.containsKey(player.toLowerCase())) {
			HomeWarmUp warmup = this.homeWarmups.get(player.toLowerCase());
			BukkitScheduler scheduler = this.plugin.getServer().getScheduler();

			scheduler.cancelTask(warmup.getTaskID());
			
			this.homeWarmups.remove(player.toLowerCase());
			this.saveRequired = true;
			
			if (this.enableAutoSave && this.saveRequired) {
				this.saveWarmupsLocal();
			}
		}
	}
	
	public void callbackTaskComplete(HomeWarmUp warmup) {
		this.homeWarmups.remove(warmup.getPlayer().getName().toLowerCase());
		this.saveRequired = true;
		
		if (this.enableAutoSave && this.saveRequired) {
			this.saveWarmupsLocal();
		}
	}
	
	/**
	 * Save warmups list to file. Clears the saveRequired flag.
	 */
	private void saveWarmupsLocal() {
		try {
			FileWriter fstream = new FileWriter(this.warmupFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user warmup times." + Util.newLine());
			writer.write("# <username>;<expiry>" + Util.newLine());
			writer.write(Util.newLine());

			for (Entry<String, HomeWarmUp> entry : this.homeWarmups.entrySet()) {
				HomeWarmUp home = entry.getValue();
				
				writer.write(entry.getKey().toLowerCase() + ";" + home.getExpiry().getTime() + ";" +
						home.getX() + ";" + home.getY() + ";" + home.getZ() + ";" +
						home.getPitch() + ";" + home.getYaw() + ";" + home.getAmount() + ";" + home.getWorld() + Util.newLine());
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the warmups file.", this.plugin);
			e.printStackTrace();
		}
		
		this.saveRequired = false;
	}

	/**
	 * Load the warmup list from file.
	 * @return True if load succeeds, otherwise false.
	 */
	private void loadWarmupsLocal() {
		// Create warmup file if not exist
		if (!this.warmupFile.exists()) {
			try {
				FileWriter fstream = new FileWriter(this.warmupFile);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# Stores user warmup details." + Util.newLine());
				out.write("# <username>;<executetime>;<x>;<y>;<z>;<pitch>;<yaw>;<world>" + Util.newLine());
				out.write(Util.newLine());
				
				out.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not write the default warmups file. Plugin disabled.", plugin);
				e.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		}

		boolean oldAutoSave = this.enableAutoSave;
		this.enableAutoSave = false;

		try {
			FileReader fstream = new FileReader(this.warmupFile);
			BufferedReader reader = new BufferedReader(fstream);

			String line = reader.readLine().trim();

			this.clearWarmups();

			while (line != null) {
				if (!line.startsWith("#") && line.length() > 0) {
					String[] values = line.split(";");

					try {
						if (values.length == 9) {
							Player player = this.plugin.getServer().getPlayer(values[0]);
							Date expiry = new Date(Long.parseLong(values[1]));
							
							if (player != null && player.getName().compareToIgnoreCase(values[0]) == 0) {
								double x = Double.parseDouble(values[2]);
								double y = Double.parseDouble(values[3]);
								double z = Double.parseDouble(values[4]);
								float pitch = Float.parseFloat(values[5]);
								float yaw = Float.parseFloat(values[6]);
								double amount = Double.parseDouble(values[7]);
								String worldName = values[8];
								
								HomeWarmUp warmup = new HomeWarmUp(this.plugin, player, expiry, x, y, z, pitch, yaw, worldName, amount);
								
								addWarmup(values[0].toLowerCase(), warmup);
							}
							
						}
					} catch (Exception e) {
						// This entry failed. Ignore and continue.
						if (line!=null) {
							Messaging.logWarning("Failed to load warmup timer! Line: " + line, this.plugin);
						}
					}
				}

				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not read the warmups file. Plugin disabled.", plugin);
			e.printStackTrace();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		
		this.enableAutoSave = oldAutoSave;
		this.saveRequired = false;
	}
}
