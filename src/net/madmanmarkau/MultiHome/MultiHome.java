package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import me.taylorkelly.help.Help;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MultiHome extends JavaPlugin {
	public final Logger log = Logger.getLogger("Minecraft");
    public PermissionHandler Permissions;
	public Configuration Config;
	public PluginDescriptionFile pdfFile;
	public PluginLogger pluginLogger; 

    private String homesPath;
	private final String homesFile = "homes.txt";
	private HashMap<String, ArrayList<HomeLocation>> homeLocations = new HashMap<String, ArrayList<HomeLocation>>();

	@Override
	public void onDisable() {
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " unloaded");
	}

	@Override
	public void onEnable() {
		this.pdfFile = this.getDescription();
		this.homesPath = this.getDataFolder().getAbsolutePath() + File.separator;

		setupHelp();
		setupPermissions();
		loadSettings();
		loadHomes();

		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " loaded");
	}
	
    private void setupHelp() {
        Plugin test = getServer().getPluginManager().getPlugin("Help");

        
        if (test != null) {
    		if (!test.isEnabled()) {
    			this.getServer().getPluginManager().enablePlugin(test);
    		}

    		if (test.isEnabled()) {
    			Help helpPlugin = ((Help) test);
	            helpPlugin.registerCommand("home", "Go to default home", this, true, "multihome.home");
	            helpPlugin.registerCommand("home [name]", "Go to named home", this, "multihome.namedhome");
	            helpPlugin.registerCommand("sethome", "Set default home", this, true, "multihome.home");
	            helpPlugin.registerCommand("sethome [name]", "Set named home", this, "multihome.namedhome");
	            helpPlugin.registerCommand("deletehome [name]", "Delete named home", this, "multihome.deletehome");
	            helpPlugin.registerCommand("listhomes", "List your homes", this, "multihome.listhomes.myself");
	            helpPlugin.registerCommand("listhomes [player]", "List [player]'d homes", this, "multihome.listhomes.others");
    		}
        }
    }

    private void loadSettings() {
		File file = new File(this.homesPath);
		if ( !(file.exists()) ) {
			file.mkdir();
		}

		// Create configuration file if not exist
		file = new File(this.homesPath + "/config.yml");
		if ( !file.exists() ) {
			try {
				FileWriter fstream = new FileWriter(this.homesPath + "/config.yml");
				BufferedWriter out = new BufferedWriter(fstream);

				String newline = System.getProperty("line.separator");
				
				out.write("# MultiHome config file." + newline);
				out.write("#" + newline);
				out.write("# settings:" + newline);
				out.write("#   deafult: Default settings for all users are stored here." + newline);
				out.write("#     warmup: Amount of time to wait before a /home command executes." + newline);
				out.write("#     cooldown: Amount of time to wait before /home can be used again." + newline);
				out.write("#     maxhomes: Maximum number of homes this group may have. Use -1 to signify no limit." + newline);
				out.write("#" + newline);
				out.write("# When editing this file for the first time, please duplicate the groups.default section" + newline);
				out.write("#  for each of your defined Permissions groups." + newline);
				out.write(newline);
				out.write("MultiHome:" + newline);
				out.write("    default:" + newline);
				out.write("        warmup: 0" + newline);
				out.write("        cooldown: 0" + newline);
				out.write("        maxhomes: -1" + newline);
				out.write("    groups:" + newline);
				out.write("        default:" + newline);
				out.write("            warmup: 0" + newline);
				out.write("            cooldown: 0" + newline);
				out.write("            maxhomes: -1" + newline);

				out.close();
			} catch (Exception e) {
				log.warning(pdfFile.getName() + " could not write the default config file.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}

    	// Reading from YML file
		Config = new Configuration(new File(this.homesPath + "/config.yml"));
		Config.load();
    }
    
	public void setupPermissions() {
		Plugin perm = this.getServer().getPluginManager().getPlugin("Permissions");
			
		if (this.Permissions == null) {
			if (perm!= null) {
				this.getServer().getPluginManager().enablePlugin(perm);
				this.Permissions = ((Permissions) perm).getHandler();
			}
			else {
				log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " not enabled. Permissions not detected");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}

	public void loadHomes() {
		File file = new File(homesPath);
		if ( !(file.exists()) ) {
			file.mkdir();
		}

		// Create homes file if not exist
		file = new File(homesPath + homesFile);
		if ( !file.exists() ) {
			try {
				FileWriter fstream = new FileWriter(homesPath + homesFile);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# Stores user home locations.\n");
				out.write("# <username>;<x>;<y>;<z>;<pitch>;<yaw>;<world>[;<name>]\n");

				out.close();
			} catch (Exception e) {
				log.warning(pdfFile.getName() + " could not write the default homes file.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}

		try {
			FileReader fstream = new FileReader(homesPath + homesFile);
			BufferedReader reader = new BufferedReader(fstream);

			String line = reader.readLine().trim();

			while (line != null) {
				if (!line.startsWith("#")) {
					String[] values = line.split(";");
					double X = 0, Y = 0, Z = 0;
					float pitch = 0, yaw = 0;
					String world = "";
					String name = "";

					try {
						if (values.length == 7)
						{
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

						if (world != null) {
							ArrayList<HomeLocation> homeList;

							if (!homeLocations.containsKey(values[0])) {
								homeList = new ArrayList<HomeLocation>();
							} else {
								homeList = homeLocations.get(values[0]);
							}

							homeList.add(new HomeLocation(name, world, X, Y, Z, pitch, yaw));

							homeLocations.put(values[0], homeList);
						}
					} catch (Exception e) {
						// This entry failed. Ignore and continue.
						if (line!=null) {
							log.warning("Failed to load home location! Line: " + line);
						}
					}
				}

				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			log.severe(pdfFile.getName() + " could not read the homes file.");
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void saveHomes() {
		File file = new File(homesPath);
		if ( !(file.exists()) ) {
			file.mkdir();
		}

		try {
			FileWriter fstream = new FileWriter(homesPath + homesFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user home locations.");
			writer.newLine();
			writer.write("# <username>;<x>;<y>;<z>;<pitch>;<yaw>;<world>[;<name>]");
			writer.newLine();
			writer.newLine();

			for (Entry<String, ArrayList<HomeLocation>> entry : homeLocations.entrySet()) {
				for (HomeLocation thisLocation : entry.getValue()) {
					writer.write(entry.getKey() + ";" + thisLocation.getX() + ";" + thisLocation.getY() + ";" + thisLocation.getZ() + ";"
							+ thisLocation.getPitch() + ";" + thisLocation.getYaw() + ";"
							+ thisLocation.getWorld() + ";" + thisLocation.getHomeName());
					writer.newLine();
				}
			}
			writer.close();
		} catch (Exception e) {
			log.severe(pdfFile.getName() + " could not write the homes file.");
			e.printStackTrace();
		}
	}

	public Location getPlayerHomeLocation(Player player, String name) {
		if (!homeLocations.containsKey(player.getName())) {
			return null;
		}

		ArrayList<HomeLocation> thisLocationList = homeLocations.get(player.getName());

		for (HomeLocation thisLocation : thisLocationList) {
			if (thisLocation.getHomeName().compareToIgnoreCase(name) == 0) {
				return thisLocation.getHomeLocation(getServer());
			}
		}

		return null;
	}

	public void setPlayerHomeLocation(Player player, String name, Location location) {
		ArrayList<HomeLocation> thisLocationList;
		HomeLocation thisLocation = null;

		if (!homeLocations.containsKey(player.getName())) {
			thisLocationList = new ArrayList<HomeLocation>();
		} else {
			thisLocationList = homeLocations.get(player.getName());
		}

		for (HomeLocation thisLoc : thisLocationList) {
			if (thisLoc.getHomeName().compareToIgnoreCase(name) == 0) {
				thisLocation = thisLoc;
				break;
			}
		}

		if (thisLocation == null) {
			thisLocation = new HomeLocation(name, location);
		} else {
			thisLocation.setHomeLocation(location);
		}

		boolean locationSet = false;
		for (int index = 0; index < thisLocationList.size(); index++) {
			if (thisLocationList.get(index).getHomeName().compareToIgnoreCase(name) == 0) {
				thisLocationList.set(index, thisLocation);
				locationSet = true;
				break;
			}
		}
		if (!locationSet) {
			thisLocationList.add(thisLocation);
		}

		if (homeLocations.containsKey(player.getName())) {
			homeLocations.remove(player.getName());
		}
		homeLocations.put(player.getName(), thisLocationList);
	}

	public boolean deletePlayerHomeLocation(Player player, String name) {
		ArrayList<HomeLocation> thisLocationList;
		HomeLocation thisLocation = null;

		if (!homeLocations.containsKey(player.getName())) {
			thisLocationList = new ArrayList<HomeLocation>();
		} else {
			thisLocationList = homeLocations.get(player.getName());
		}

		for (HomeLocation thisLoc : thisLocationList) {
			if (thisLoc.getHomeName().compareToIgnoreCase(name) == 0) {
				thisLocation = thisLoc;
				break;
			}
		}

		if (thisLocation != null) {
			if (thisLocationList.remove(thisLocation)) {
				homeLocations.put(player.getName(), thisLocationList);
				return true;
			}
		}
		return false;
	}

	public boolean queryPlayerHomeLocationExists(Player player, String name) {
		ArrayList<HomeLocation> thisLocationList;

		if (!homeLocations.containsKey(player.getName())) {
			thisLocationList = new ArrayList<HomeLocation>();
		} else {
			thisLocationList = homeLocations.get(player.getName());
		}

		for (HomeLocation thisLoc : thisLocationList) {
			if (thisLoc.getHomeName().compareToIgnoreCase(name) == 0) {
				return true;
			}
		}

		return false;
	}

	public int getPlayerHomeCount(Player player) {
		ArrayList<HomeLocation> thisLocationList;

		if (!homeLocations.containsKey(player.getName())) {
			thisLocationList = new ArrayList<HomeLocation>();
		} else {
			thisLocationList = homeLocations.get(player.getName());
		}

		return thisLocationList.size();
	}
	
	public ArrayList<HomeLocation> listPlayerHomeLocations(String player) {
		ArrayList<HomeLocation> thisLocationList;

		if (!homeLocations.containsKey(player)) {
			thisLocationList = new ArrayList<HomeLocation>();
		} else {
			thisLocationList = homeLocations.get(player);
		}

		return thisLocationList;
	}

	private int getSetting(Player player, String setting, int defaultValue) {
		// Get the player group
		String playerGroup = Permissions.getGroup(player.getWorld().getName(), player.getName());
		
		if (playerGroup != null) {
			// Player group found
			List<String> keys = Config.getKeys("MultiHome." + playerGroup);
			
			if (keys != null && !keys.isEmpty()) {
				// Settings for player group exists.
				return Config.getInt("MultiHome.groups." + playerGroup + "." + setting, defaultValue);
			}
		}
		
		// Get from default
		return Config.getInt("MultiHome.default." + setting, defaultValue);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (cmd.getName().compareToIgnoreCase("multihome_home") == 0) {
			Location loc = null;

			if (args.length > 0) {
				if (!this.Permissions.has(player, "multihome.namedhome")) return true;
				loc = getPlayerHomeLocation(player, args[0]);
			} else {
				if (!this.Permissions.has(player, "multihome.home")) return true;
				loc = getPlayerHomeLocation(player, "");
			}

			if (loc == null) {
				player.sendMessage(ChatColor.RED + "Home not set.");
			} else {
				if (HomeCoolDown.userMayTeleport(player)) {
					HomeCoolDown.addCooldown(player.getName(), getSetting(player, "cooldown", 0));
					int warmup = getSetting(player, "warmup", 0);
					
					if (warmup >= 0) {
						// Warpup required.
						if (getServer().getScheduler().scheduleSyncDelayedTask(this, new HomeWarmUp(player, loc), warmup * 20) != -1) {
							player.sendMessage(ChatColor.RED + "Home initiated. Transfer in " + warmup + " seconds.");
						} else {
							player.sendMessage(ChatColor.RED + "Home initiation FAILED! Please try again.");
						}
					} else {
						// No warmup needed. Immediately teleport.
						if (loc.getWorld().getName().equals(player.getWorld().getName())) {
							// Direct teleport inside the current world.
							player.teleport(loc);
						} else {
							// Indirect teleport between worlds.
							Location playerLoc = player.getLocation();
							
							player.teleport(new Location(loc.getWorld(), playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getPitch(), playerLoc.getYaw()));
							player.teleport(loc);
						}
					}
				}
			}
			return true;
		} else if (cmd.getName().compareToIgnoreCase("multihome_sethome") == 0) {
			if (args.length > 0) {
				if (!this.Permissions.has(player, "multihome.setnamedhome")) return true;
				int numHomes = getPlayerHomeCount(player);
				int maxHomes = getSetting(player, "maxhomes", -1);
				
				if (numHomes < maxHomes || maxHomes == -1 || queryPlayerHomeLocationExists(player, args[0])) {
					setPlayerHomeLocation(player, args[0], player.getLocation());
					player.sendMessage(ChatColor.RED + "Home location [" + args[0] + "] set.");
					log.info(player.getName() + " set home location [" + args[0] +"].");
				} else {
					player.sendMessage(ChatColor.RED + "Cannot set home location. You have too many set already.");
					log.info(player.getName() + " tried to set home location [" + args[0] +"]. Too many set already.");
				}
			} else {
				if (!this.Permissions.has(player, "multihome.sethome")) return true;
				setPlayerHomeLocation(player, "", player.getLocation());
				player.sendMessage(ChatColor.RED + "Home location set.");
				log.info(player.getName() + " set home location [].");
			}
			saveHomes();
			return true;
		} else if (cmd.getName().compareToIgnoreCase("multihome_deletehome") == 0) {
			if (args.length > 0) {
				if (!this.Permissions.has(player, "multihome.deletehome")) return true;
				if (deletePlayerHomeLocation(player, args[0])) {
					player.sendMessage(ChatColor.RED + "Home location [" + args[0] + "] deleted.");
					log.info(player.getName() + " deleted home location [" + args[0] +"].");
				} else {
					player.sendMessage(ChatColor.RED + "Home not set.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You cannot delete your default home location.");
			}
			saveHomes();
			return true;
		} else if (cmd.getName().compareToIgnoreCase("multihome_listhomes") == 0) {
			ArrayList<HomeLocation> listHomeLocations;
			
			if (args.length > 0) {
				if (!this.Permissions.has(player, "multihome.listhomes.others")) return true;
				listHomeLocations = listPlayerHomeLocations(args[0]);
				log.info(player.getName() + " listed home locations for player " + args[0] +".");
			} else {
				if (!this.Permissions.has(player, "multihome.listhomes.myself")) return true;
				listHomeLocations = listPlayerHomeLocations(player.getName());
			}

			String userResponse = "";
			for (HomeLocation thisLocation : listHomeLocations) {
				if (thisLocation.getHomeName().length() == 0) {
					userResponse = userResponse + ", [Default]";
				} else {
					userResponse = userResponse + ", " + thisLocation.getHomeName();
				}
			}
			if (!userResponse.isEmpty() && userResponse.length() > 2) {
				player.sendMessage(ChatColor.RED + "Home location(s): " + userResponse.substring(2));
			} else {
				player.sendMessage(ChatColor.RED + "No home locations defined.");
			}

			return true;
		}
		return false;
	}
}
