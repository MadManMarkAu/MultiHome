package net.madmanmarkau.MultiHome.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import net.madmanmarkau.MultiHome.Messaging;
import net.madmanmarkau.MultiHome.MultiHome;
import net.madmanmarkau.MultiHome.Util;

public class WarmUpManagerFile extends WarmUpManager {
    private final File warmupsFile;
	private HashMap<String, WarmUpTask> warmupEntries = new HashMap<String, WarmUpTask>();
	
	public WarmUpManagerFile(MultiHome plugin) {
		super(plugin);
		this.warmupsFile = new File(plugin.getDataFolder(), "warmups.txt");
		
		loadWarmups();
	}
	
	@Override
	public void clearWarmups() {
		try {
			for (Entry<String, WarmUpTask> entry : this.warmupEntries.entrySet()) {
				entry.getValue().cancelWarmUp();
			}
	
			this.warmupEntries.clear();
	
			saveWarmups();
		} catch (Exception e) {
			Messaging.logSevere("Failed to clear warmups: " + e.getMessage(), this.plugin);
		}
	}

	@Override
	public WarmUpEntry getWarmup(String player) {
		try {
			if (this.warmupEntries.containsKey(player.toLowerCase())) {
				return this.warmupEntries.get(player.toLowerCase()).getWarmup();
			}
		} catch (Exception e) {
			Messaging.logSevere("Failed to get warmup: " + e.getMessage(), this.plugin);
		}
		
		return null;
	}

	@Override
	public void addWarmup(WarmUpEntry warmup) {
		try {
			if (this.warmupEntries.containsKey(warmup.getPlayer().toLowerCase())) {
				// Remove old warmup
				WarmUpTask task = this.warmupEntries.get(warmup.getPlayer().toLowerCase());
				task.cancelWarmUp();
				this.warmupEntries.remove(warmup.getPlayer().toLowerCase());
			}
	
			// Set new warmup
			this.warmupEntries.put(warmup.getPlayer().toLowerCase(), new WarmUpTask(plugin, warmup));
	
			saveWarmups();
		} catch (Exception e) {
			Messaging.logSevere("Failed to add warmup: " + e.getMessage(), this.plugin);
		}
	}

	@Override
	public void removeWarmup(String player) {
		try {
			if (this.warmupEntries.containsKey(player.toLowerCase())) {
				this.warmupEntries.get(player.toLowerCase()).cancelWarmUp();
				
				this.warmupEntries.remove(player.toLowerCase());
	
				saveWarmups();
			}
		} catch (Exception e) {
			Messaging.logSevere("Failed to remove warmup: " + e.getMessage(), this.plugin);
		}
	}
	
	@Override
	public void taskComplete(WarmUpEntry warmup) {
		try {
			this.warmupEntries.remove(warmup.getPlayer().toLowerCase());
	
			saveWarmups();
		} catch (Exception e) {
			Messaging.logSevere("Failed to complete warmup: " + e.getMessage(), this.plugin);
		}
	}
	
	/**
	 * Save warmups list to file. Clears the saveRequired flag.
	 */
	private void saveWarmups() {
		try {
			FileWriter fstream = new FileWriter(this.warmupsFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user warmup times." + Util.newLine());
			writer.write("# <username>;<expiry>;<X>;<Y>;<Z>;<pitch>;<yaw>;<world>;<cost>" + Util.newLine());
			writer.write(Util.newLine());

			for (Entry<String, WarmUpTask> entry : this.warmupEntries.entrySet()) {
				WarmUpEntry home = entry.getValue().getWarmup();
				
				writer.write(home.getPlayer() + ";" + home.getExpiry().getTime() + ";" +
						home.getX() + ";" + home.getY() + ";" + home.getZ() + ";" +
						home.getPitch() + ";" + home.getYaw() + ";" + home.getWorld() + ";" + home.getCost() + Util.newLine());
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the warmups file.", this.plugin);
		}
	}

	/**
	 * Load the warmup list from file.
	 * @return True if load succeeds, otherwise false.
	 */
	private void loadWarmups() {
		try {
			if (this.warmupsFile.exists()) {
				FileReader fstream = new FileReader(this.warmupsFile);
				BufferedReader reader = new BufferedReader(fstream);
				
				for (Entry<String, WarmUpTask> entry : this.warmupEntries.entrySet()) {
					entry.getValue().cancelWarmUp();
				}

				this.warmupEntries.clear();
	
				String line = reader.readLine().trim();
	
				while (line != null) {
					if (!line.startsWith("#") && line.length() > 0) {
						String[] values = line.split(";");
	
						try {
							if (values.length == 9) {
								String player = values[0];
								Date expiry = new Date(Long.parseLong(values[1]));
								double x = Double.parseDouble(values[2]);
								double y = Double.parseDouble(values[3]);
								double z = Double.parseDouble(values[4]);
								float pitch = Float.parseFloat(values[5]);
								float yaw = Float.parseFloat(values[6]);
								String worldName = values[7];
								double amount = Double.parseDouble(values[8]);

								if (!this.warmupEntries.containsKey(player.toLowerCase())) {
									this.warmupEntries.put(player.toLowerCase(), new WarmUpTask(plugin, new WarmUpEntry(player, expiry, worldName, x, y, z, pitch, yaw, amount)));
								}
							}
						} catch (Exception e) {
						}
					}
	
					line = reader.readLine();
				}
	
				reader.close();
			}
		} catch (Exception e) {
			Messaging.logSevere("Could not read the warmups file: " + e.getMessage(), plugin);
		}
	}
}
