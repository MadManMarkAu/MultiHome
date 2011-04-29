package net.madmanmarkau.MultiHome;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class Settings {
	private static Configuration Config;
	private static MultiHome plugin;
	
	// TODO: Use Plugin.getConfiguration();
	
	public static void initialize(MultiHome plugin) {
		Settings.plugin = plugin;
	}
	
    public static void loadSettings(File configFile) {
		// Create configuration file if not exist
		if (!configFile.exists()) {
			try {
				FileWriter fstream = new FileWriter(configFile);
				BufferedWriter out = new BufferedWriter(fstream);

				String newline = System.getProperty("line.separator");
				
				out.write("# MultiHome config file." + newline);
				out.write("#" + newline);
				out.write("# settings:" + newline);
				out.write("#   messages: Plugin messages are stored here. Customize messages using these entries. Missing entries will not be sent." + newline);
				out.write("#     tooManyParameters: Message for when user specifies too many parameters. Variables: none" + newline);
				out.write("#     defaultHomeSetMessage: Message for when deafult home is set. Variables: none" + newline);
				out.write("#     cannotDeleteDefaultHomeMessage: Message for when player tries to delete deafult home. Variables: none" + newline);
				out.write("#     homeSetMessage: Message for when home is set. Variables: {HOME}" + newline);
				out.write("#     homeDeletedMessage: Message for when home deleted. Variables: {NAME}" + newline);
				out.write("#     noHomeMessage: Message for when home not found. Variables: {HOME}" + newline);
				out.write("#     noDefaultHomeMessage: Message for when default home not found." + newline);
				out.write("#     noPlayerMessage: Message for when target player not found. Variables: {PLAYER}'" + newline);
				out.write("#     warmupMessage: Message for when home warmup initiated. Variables: {SECONDS}" + newline);
				out.write("#     warmupCompleteMessage: Message for when home warmup completes. Variables: none" + newline);
				out.write("#     cooldownMessage: Message for when cooldown hasn't expired yet. Variables: {SECONDS}" + newline);
				out.write("#     tooManyHomesMessage: Message for when user tries to set too many homes. Variables: {CURRENT}, {MAX}" + newline);
				out.write("#     homeListMessage: Message for when home locations listed. Variables: {LIST}" + newline);
				out.write("#     homeListOthersMessage: Message for when home locations for another player are listed. Variables: {PLAYER}, {LIST}" + newline);
				out.write("#     homeInviteOwnerMessage: Message to home owner for when invite is granted. Variables: {TARGET} {HOME}" + newline);
				out.write("#     homeInviteTargetMessage: Message to invite target for when invite is granted. Variables: {OWNER} {HOME}" + newline);
				out.write("#     homeInviteTimedOwnerMessage: Message to home owner for when timed invite is granted. Variables: {TARGET} {HOME} {TIME}" + newline);
				out.write("#     homeInviteTimedTargetMessage: Message to invite target for when timed invite is granted. Variables: {OWNER} {HOME} {TIME}" + newline);
				out.write("#     homeUninviteOwnerMessage: Message to home owner for when invite is retracted. Variables: {TARGET} {HOME}" + newline);
				out.write("#     homeUninviteTargetMessage: Message to invite target for when invite is retracted. Variables: {OWNER} {HOME}" + newline);
				out.write("#     homeListInvitesToMe: Message to use when listing invites open to this player. Variables: {TARGET} {LIST}" + newline);
				out.write("#     homeListInvitesToOthers: Message to use when listing invites open to other players. Variables: {OWNER} {LIST}" + newline);
				out.write("#   deafult: Default settings for all users are stored here." + newline);
				out.write("#     warmup: Amount of time to wait before a /home command executes." + newline);
				out.write("#     cooldown: Amount of time to wait before /home can be used again." + newline);
				out.write("#     maxhomes: Maximum number of homes this group may have. Use -1 to signify no limit." + newline);
				out.write("#" + newline);
				out.write("# When editing this file for the first time, please duplicate the groups.default section" + newline);
				out.write("#  for each of your defined Permissions groups." + newline);
				out.write(newline);
				out.write("MultiHome:" + newline);
				out.write("    enableHomeOnDeath: false" + newline);
				out.write("    messages:" + newline);
				out.write("        tooManyParameters: 'Too many parameters.'" + newline);
				out.write("        defaultHomeSetMessage: 'Deafult home set.'" + newline);
				out.write("        cannotDeleteDefaultHomeMessage: 'You cannot delete your default home location.'" + newline);
				out.write("        homeSetMessage: 'Home {HOME} set.'" + newline);
				out.write("        homeDeletedMessage: 'Home {NAME} deleted.'" + newline);
				out.write("        noHomeMessage: 'Home {HOME} not set.'" + newline);
				out.write("        noDefaultHomeMessage: 'Home not set.'" + newline);
				out.write("        noPlayerMessage: 'Player {PLAYER} not found.'" + newline);
				out.write("        warmupMessage: 'Home initiated. Transfer in {SECONDS} seconds.'" + newline);
				out.write("        warmupCompleteMessage: 'Teleporting now!'" + newline);
				out.write("        cooldownMessage: 'You may not teleport yet. Please wait another {SECONDS} seconds.'" + newline);
				out.write("        tooManyHomesMessage: 'Cannot set home location. You have already set {CURRENT} out of {MAX} homes.'" + newline);
				out.write("        homeListMessage: 'Home locations: {LIST}'" + newline);
				out.write("        homeListOthersMessage: 'Home locations for {PLAYER}: {LIST}'" + newline);
				out.write("        homeInviteOwnerMessage: 'Invite extended to {TARGET}.'" + newline);
				out.write("        homeInviteTargetMessage: '{OWNER} invited you to their home. To accept, use this command: /home {OWNER}:{HOME}'" + newline);
				out.write("        homeInviteTimedOwnerMessage: 'Invite extended to {TARGET} for {TIME} seconds.'" + newline);
				out.write("        homeInviteTimedTargetMessage: '{OWNER} invited you to their home for {TIME} seconds. To accept, use this command: /home {OWNER}:{HOME}'" + newline);
				out.write("        homeUninviteOwnerMessage: 'You have retracted your invite for {TARGET} to visit your home: [{HOME}]'" + newline);
				out.write("        homeUninviteTargetMessage: '{OWNER} has retracted their invite to to their home: [{HOME}]'" + newline);
				out.write("        homeListInvitesToMe: 'Invites open to you: {LIST}'" + newline);
				out.write("        homeListInvitesToOthers: 'Invites you have open: {LIST}'" + newline);
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
				Messaging.logWarning("Could not write the default config file.", plugin);
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}

    	// Reading from YML file
		Config = new Configuration(configFile);
		Config.load();
    }

	public static int getSettingInt(Player player, String setting, int defaultValue) {
		// Get the player group
		String playerGroup = Permissions.getGroup(player.getWorld().getName(), player.getName());
		
		if (playerGroup != null) {
			// Player group found
			List<String> keys = Config.getKeys("MultiHome.groups." + playerGroup);
			
			if (keys != null && !keys.isEmpty()) {
				// Settings for player group exists.
				return Config.getInt("MultiHome.groups." + playerGroup + "." + setting, defaultValue);
			}
		}
		
		// Get from default
		return Config.getInt("MultiHome.default." + setting, defaultValue);
	}

	public static String getSettingString(Player player, String setting, String defaultValue) {
		// Get the player group
		String playerGroup = Permissions.getGroup(player.getWorld().getName(), player.getName());
		
		if (playerGroup != null) {
			// Player group found
			List<String> keys = Config.getKeys("MultiHome.groups." + playerGroup);
			
			if (keys != null && !keys.isEmpty()) {
				// Settings for player group exists.
				return Config.getString("MultiHome.groups." + playerGroup + "." + setting, defaultValue);
			}
		}
		
		// Get from default
		return Config.getString("MultiHome.default." + setting, defaultValue);
	}
	
	public static boolean isHomeOnDeathEnabled() {
		return Config.getBoolean("MultiHome.enableHomeOnDeath", false);
	}
	
	public static int getSettingWarmup(Player player) {
		return getSettingInt(player, "warmup", 0);
	}
	
	public static int getSettingCooldown(Player player) {
		return getSettingInt(player, "cooldown", 0);
	}
	
	public static int getSettingMaxHomes(Player player) {
		return getSettingInt(player, "maxhomes", -1);
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
}
