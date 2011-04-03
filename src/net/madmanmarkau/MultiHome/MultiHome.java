package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MultiHome extends JavaPlugin {
	public final Logger log = Logger.getLogger("Minecraft");
    public PermissionHandler Permissions;
	public PluginDescriptionFile pdfFile;

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
		this.homesPath = "plugins" + File.separator + pdfFile.getName() + File.separator;

		loadHomes();
		setupPermissions();

		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " loaded");
	}

	public void setupPermissions() {
		Plugin perm = this.getServer().getPluginManager().getPlugin("Permissions");
			
		if (this.Permissions == null) {
			if (perm!= null) {
				this.getServer().getPluginManager().enablePlugin(perm);
				this.Permissions = ((Permissions) perm).getHandler();
			}
			else {
				log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + "not enabled. Permissions not detected");
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
					World world = null;
					String name = "";

					try {
						if (values.length == 7)
						{
							X = Double.parseDouble(values[1]);
							Y = Double.parseDouble(values[2]);
							Z = Double.parseDouble(values[3]);
							pitch = Float.parseFloat(values[4]);
							yaw = Float.parseFloat(values[5]);

							world = getServer().getWorld(values[6]);
						} else if (values.length == 8) {
							X = Double.parseDouble(values[1]);
							Y = Double.parseDouble(values[2]);
							Z = Double.parseDouble(values[3]);
							pitch = Float.parseFloat(values[4]);
							yaw = Float.parseFloat(values[5]);

							world = getServer().getWorld(values[6]);
							name = values[7];
						}

						if (world != null) {
							ArrayList<HomeLocation> homeList;

							if (!homeLocations.containsKey(values[0])) {
								homeList = new ArrayList<HomeLocation>();
							} else {
								homeList = homeLocations.get(values[0]);
							}

							homeList.add(new HomeLocation(name, new Location(world, X, Y, Z, yaw, pitch)));

							homeLocations.put(values[0], homeList);
						}
					} catch (Exception e) {
						// This entry failed. Ignore and continue.
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
					Location loc = thisLocation.getHomeLocation();

					writer.write(entry.getKey() + ";" + loc.getX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";"
							+ loc.getPitch() + ";" + loc.getYaw() + ";"
							+ loc.getWorld().getName() + ";" + thisLocation.getHomeName());
					writer.newLine();
				}
			}
			writer.close();
		} catch (Exception e) {
			log.severe(pdfFile.getName() + " could not read the homes file.");
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public Location getPlayerHomeLocation(Player player, String name) {
		if (!homeLocations.containsKey(player.getName())) {
			return null;
		}

		ArrayList<HomeLocation> thisLocationList = homeLocations.get(player.getName());

		for (HomeLocation thisLocation : thisLocationList) {
			if (thisLocation.getHomeName().compareToIgnoreCase(name) == 0) {
				return thisLocation.getHomeLocation();
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

	public ArrayList<HomeLocation> listPlayerHomeLocations(String player) {
		ArrayList<HomeLocation> thisLocationList;

		if (!homeLocations.containsKey(player)) {
			thisLocationList = new ArrayList<HomeLocation>();
		} else {
			thisLocationList = homeLocations.get(player);
		}

		return thisLocationList;
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
				if (loc.getWorld().getName().equals(player.getWorld().getName())) {
					// Direct teleport inside the current world.
					player.teleportTo(loc);
				} else {
					// Indirect teleport between worlds.
					Location playerLoc = player.getLocation();
					
					player.teleportTo(new Location(loc.getWorld(), playerLoc.getX(), playerLoc.getY(), playerLoc.getZ(), playerLoc.getPitch(), playerLoc.getYaw()));
					player.teleportTo(loc);
				}
			}
			return true;
		} else if (cmd.getName().compareToIgnoreCase("multihome_sethome") == 0) {
			if (args.length > 0) {
				if (!this.Permissions.has(player, "multihome.namedhome")) return true;
				setPlayerHomeLocation(player, args[0], player.getLocation());
				player.sendMessage(ChatColor.RED + "Home location [" + args[0] + "] set.");
				log.info(player.getName() + " set home location [" + args[0] +"].");
			} else {
				if (!this.Permissions.has(player, "multihome.home")) return true;
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
					player.sendMessage(ChatColor.RED + "Home location [" + args[0] + "] set.");
					log.info(player.getName() + " deleted home location [" + args[0] +"].");
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
