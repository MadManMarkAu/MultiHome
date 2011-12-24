package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {
	private static YamlConfiguration Config;
	private static MultiHome plugin;
	private static boolean permissiveGroupHandling = true;
	
	public static void initialize(MultiHome plugin) {
		Settings.plugin = plugin;
	}
	
    public static void loadSettings(File configFile) {
		// Create configuration file if not exist
		if (!configFile.exists()) {
			try {
				configFile.getParentFile().mkdirs();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(plugin.getResource("config.yml")));
				BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
				String line;
				
				while ((line = in.readLine()) != null) {
					out.write(line + Util.newLine());
				}
				
				in.close();
				out.close();
			} catch (Exception e) {
				Messaging.logWarning("Could not write the default config file.", plugin);
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}

    	// Reading from YML file
		Config = new YamlConfiguration();
		try {
			Config.load(configFile);
		} catch (Exception e) {
			Messaging.logSevere("Could not load the configuration file: " + e.getMessage(), plugin);
		}
		
		permissiveGroupHandling = isPermissiveGroupHandlingEnabled();
		
    }

/*
	public static int getSettingInt(Player player, String setting, int defaultValue) {
		// Get the player group
		String playerGroup = HomePermissions.getGroup(player.getWorld().getName(), player.getName());
		
		if (playerGroup != null) {
			// Player group found
			if (Config.isSet("MultiHome.groups." + playerGroup + "." + setting)) {
				// Settings for player group exists.
				return Config.getInt("MultiHome.groups." + playerGroup + "." + setting, defaultValue);
			}
		}
		
		// Get from default
		return Config.getInt("MultiHome.default." + setting, defaultValue);
	}
	*/
	public static int getSettingInt(Player player, String setting, int defaultValue, boolean findMax, boolean negativeMax) {
		// Get the player group
		String[] playerGroups = HomePermissions.getGroups(player.getWorld().getName(), player.getName());
		
		List <Integer> settings = new ArrayList<Integer>();
		
		if (playerGroups != null)
		{
			for (int i=0; i<playerGroups.length; i++)
				
			// Player group found
			if (Config.isSet("MultiHome.groups." + playerGroups[i] + "." + setting))
				// Settings for player group exists.
				settings.add(Config.getInt("MultiHome.groups." + playerGroups[i] + "." + setting, defaultValue));
		}
		if (settings.size() == 0)
		// Get from default
			settings.add(Config.getInt("MultiHome.default." + setting, defaultValue));
		
		int settingValue = settings.get(0);
		for (int i=1; i<settings.size(); i++)
		{
			int test=settings.get(i);
			if ((test == -1) && findMax && negativeMax)
				return -1;
			if ((settings.get(i) > settingValue)==(findMax))
			{
				settingValue = settings.get(i);
			}
		}
		return settingValue;
	}

	public static boolean getSettingBoolean(Player player, String setting, boolean defaultValue, boolean findTrue) {
		// Get the player group
		String[] playerGroups = HomePermissions.getGroups(player.getWorld().getName(), player.getName());
		
		boolean settingValue = !findTrue; // If true trumps false, start with false and look for true.  If false trumps true, start with true and look for false.
		
		if (playerGroups != null)
		{
			for (int i=0; i<playerGroups.length; i++)
				
			// Player group found
			if (Config.isSet("MultiHome.groups." + playerGroups[i] + "." + setting))
				// Settings for player group exists.
				if (Config.getBoolean("MultiHome.groups." + playerGroups[i] + "." + setting, defaultValue)!=settingValue)
					return !settingValue; // If it's different from the starting point, then it trumps the starting point and should be returned
			return settingValue; // Otherwise, nothing was found so return the starting value; 
		}
		return defaultValue; // None of the groups even had this setting; return default.
	}

/*	public static String getSettingString(Player player, String setting, String defaultValue) {
		// Get the player group
		String playerGroup = HomePermissions.getGroup(player.getWorld().getName(), player.getName());
		
		if (playerGroup != null) {
			// Player group found
			if (Config.isSet("MultiHome.groups." + playerGroup + "." + setting)) {
				// Settings for player group exists.
				return Config.getString("MultiHome.groups." + playerGroup + "." + setting, defaultValue);
			}
		}
		
		// Get from default
		return Config.getString("MultiHome.default." + setting, defaultValue);
	}
	*/
	
	public static boolean isHomeOnDeathEnabled() {
		return Config.getBoolean("MultiHome.enableHomeOnDeath", false);
	}

	public static boolean isEconomyEnabled() {
		return Config.getBoolean("MultiHome.enableEconomy", false);
	}

	/**
	 * JOREN
	 * 
	 * Returns true if region is blocked
	 */
	
	public static boolean isRegionBlocked(String world, String region)
	{
		return Config.getBoolean("MultiHome.denyregions." + world + "." + region, false);
	}
	
	public static boolean isPermissiveGroupHandlingEnabled() {
		return Config.getBoolean("MultiHome.permissiveGroupHandling", true);
	}
	
	/*
	 * /JOREN
	 */

	public static int getSetNamedHomeCost(Player player) {
		return getSettingInt(player, "setNamedHomeCost", 0, !permissiveGroupHandling, false);
	}

	public static int getSetHomeCost(Player player) {
		return getSettingInt(player, "setHomeCost", 0, !permissiveGroupHandling, false);
	}

	public static int getHomeCost (Player player) {
		return getSettingInt(player, "homeCost", 0, !permissiveGroupHandling, false);
	}

	public static int getNamedHomeCost(Player player) {
		return getSettingInt(player, "namedHomeCost", 0, !permissiveGroupHandling, false);
	}
	
	public static int getOthersHomeCost(Player player) {
		return getSettingInt(player, "othersHomeCost", 0, !permissiveGroupHandling, false);
	}

	public static int getSettingWarmup(Player player) {
		return getSettingInt(player, "warmup", 0, !permissiveGroupHandling, false);
	}
	
	public static int getSettingCooldown(Player player) {
		return getSettingInt(player, "cooldown", 0, !permissiveGroupHandling, false);
	}
	
	public static int getSettingMaxHomes(Player player) {
		return getSettingInt(player, "maxhomes", -1, !permissiveGroupHandling, true);
	}
	
	public static boolean getSettingDisrupt(Player player) {
//		return getSettingInt(player, "disruptWarmup", 1) == 1 ? true : false;
		return getSettingBoolean(player, "disruptWarmup", false, permissiveGroupHandling);
	}
	
	public static void sendMessageTooManyParameters(CommandSender sender) {
		String message = Config.getString("MultiHome.messages.tooManyParameters", null);

		if (message != null) Messaging.sendSuccess(sender, message);
	}

	public static void sendMessageDefaultHomeSet(CommandSender sender) {
		String message = Config.getString("MultiHome.messages.defaultHomeSetMessage", null);

		if (message != null) Messaging.sendSuccess(sender, message);
	}

	public static void sendMessageCannotDeleteDefaultHome(CommandSender sender) {
		String message = Config.getString("MultiHome.messages.cannotDeleteDefaultHomeMessage", null);

		if (message != null) Messaging.sendError(sender, message);
	}
	
	public static void sendMessageHomeSet(CommandSender sender, String home) {
		String message = Config.getString("MultiHome.messages.homeSetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{HOME\\}", home));
		}
	}
	
	public static void sendMessageHomeDeleted(CommandSender sender, String home) {
		String message = Config.getString("MultiHome.messages.homeDeletedMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{NAME\\}", home)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageWarmup(CommandSender sender, int timeLeft) {
		String message = Config.getString("MultiHome.messages.warmupMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{SECONDS\\}", Integer.toString(timeLeft)));
		}
	}

	public static void sendMessageWarmupComplete(CommandSender sender) {
		String message = Config.getString("MultiHome.messages.warmupCompleteMessage", null);

		if (message != null) Messaging.sendSuccess(sender, message);
	}

	public static void sendMessageWarmupDisrupted(CommandSender sender) {
		String message = Config.getString("MultiHome.messages.warmupDisruptedMessage", null);

		if (message != null) Messaging.sendError(sender, message);
	}

	public static void sendMessageCooldown(CommandSender sender, int timeLeft) {
		String message = Config.getString("MultiHome.messages.cooldownMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{SECONDS\\}", Integer.toString(timeLeft)));
		}
	}

	public static void sendMessageMaxHomes(CommandSender sender, int currentHomes, int maxHomes) {
		String message = Config.getString("MultiHome.messages.tooManyHomesMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{CURRENT\\}", Integer.toString(currentHomes))
					.replaceAll("\\{MAX\\}", Integer.toString(maxHomes)));
		}
	}

	public static void sendMessageNoHome(CommandSender sender, String home) {
		String message = Config.getString("MultiHome.messages.noHomeMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageNoDefaultHome(CommandSender sender) {
		String message = Config.getString("MultiHome.messages.noDefaultHomeMessage", null);
		
		if (message != null) Messaging.sendError(sender, message);
	}

	public static void sendMessageNoPlayer(CommandSender sender, String targetPlayer) {
		String message = Config.getString("MultiHome.messages.noPlayerMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{PLAYER\\}", targetPlayer));
		}
	}

	public static void sendMessageHomeList(CommandSender sender, String homeList) {
		String message = Config.getString("MultiHome.messages.homeListMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{LIST\\}", homeList));
		}
	}

	public static void sendMessageOthersHomeList(CommandSender sender, String player, String homeList) {
		String message = Config.getString("MultiHome.messages.homeListOthersMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{PLAYER\\}", player)
					.replaceAll("\\{LIST\\}", homeList));
		}
	}

	public static void sendMessageInviteOwnerHome(CommandSender sender, String target, String home) {
		String message = Config.getString("MultiHome.messages.homeInviteOwnerMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageInviteTargetHome(CommandSender sender, String owner, String home) {
		String message = Config.getString("MultiHome.messages.homeInviteTargetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageInviteTimedOwnerHome(CommandSender sender, String target, String home, int time) {
		String message = Config.getString("MultiHome.messages.homeInviteTimedOwnerMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{HOME\\}", home)
					.replaceAll("\\{TIME\\}", Integer.toString(time)));
		}
	}

	public static void sendMessageInviteTimedTargetHome(CommandSender sender, String owner, String home, int time) {
		String message = Config.getString("MultiHome.messages.homeInviteTimedTargetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{HOME\\}", home)
					.replaceAll("\\{TIME\\}", Integer.toString(time)));
		}
	}

	public static void sendMessageUninviteOwnerHome(CommandSender sender, String target, String home) {
		String message = Config.getString("MultiHome.messages.homeUninviteOwnerMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageUninviteTargetHome(CommandSender sender, String owner, String home) {
		String message = Config.getString("MultiHome.messages.homeUninviteTargetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageInviteListToMe(CommandSender sender, String target, String list) {
		String message = Config.getString("MultiHome.messages.homeListInvitesToMe", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{LIST\\}", list));
		}
	}

	public static void sendMessageInviteListToOthers(CommandSender sender, String owner, String list) {
		String message = Config.getString("MultiHome.messages.homeListInvitesToOthers", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{LIST\\}", list));
		}
	}

	public static void sendMessageNotEnoughMoney(Player player, double amount) {
		String message = Config.getString("MultiHome.messages.econNotEnoughFunds", null);

		if (message != null) {
			Messaging.sendError(player, message.replaceAll("\\{AMOUNT\\}", amount+""));
		}
	}

	public static void sendMessageDeductForHome(Player player, double amount) {
		String message = Config.getString("MultiHome.messages.econDeductedForHome", null);
		if (message != null) {
			Messaging.sendSuccess(player,message.replaceAll("\\{AMOUNT\\}", amount+""));
		}
	}

	public static void sendMessageDeductForSet(Player player, double amount) {
		String message = Config.getString("MultiHome.messages.econDeductedForSet", null);
		if (message != null) {
			Messaging.sendSuccess(player, message.replaceAll("\\{AMOUNT\\}", amount+""));
		}
	}
}
