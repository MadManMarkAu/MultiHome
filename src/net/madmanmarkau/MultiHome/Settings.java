package net.madmanmarkau.MultiHome;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Settings {
	private static MultiHome plugin;
	
	public static void initialize(MultiHome plugin) {
		Settings.plugin = plugin;
	}
	
    public static void loadSettings() {
    	plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

	public static int getSettingInt(Player player, String setting, int defaultValue) {
		// Get the player group
		String playerGroup = HomePermissions.getGroup(player);
		
		if (playerGroup != null) {
			// Player group found
			if (plugin.getConfig().isSet("MultiHome.groups." + playerGroup + "." + setting)) {
				// Settings for player group exists.
				return plugin.getConfig().getInt("MultiHome.groups." + playerGroup + "." + setting);
			}
		}
		
		// Get from default
		return plugin.getConfig().getInt("MultiHome.default." + setting, defaultValue);
	}

	public static String getSettingString(Player player, String setting, String defaultValue) {
		// Get the player group
		String playerGroup = HomePermissions.getGroup(player);
		
		if (playerGroup != null) {
			// Player group found
			if (plugin.getConfig().isSet("MultiHome.groups." + playerGroup + "." + setting)) {
				// Settings for player group exists.
				return plugin.getConfig().getString("MultiHome.groups." + playerGroup + "." + setting);
			}
		}
		
		// Get from default
		return plugin.getConfig().getString("MultiHome.default." + setting, defaultValue);
	}
	
	

	public static String getDataStoreSettingString(String storeMethod, String setting) {
		return plugin.getConfig().getString("MultiHome.dataStoreSettings." + storeMethod + "." + setting, "");
	}

	public static String getDataStoreMethod() {
		return plugin.getConfig().getString("MultiHome.dataStoreMethod", "file");
	}

	
	
	public static boolean isHomeOnDeathEnabled() {
		return plugin.getConfig().getBoolean("MultiHome.enableHomeOnDeath", false);
	}

	public static boolean isEconomyEnabled() {
		return plugin.getConfig().getBoolean("MultiHome.enableEconomy", false);
	}

	public static int getSetNamedHomeCost(Player player) {
		return getSettingInt(player, "setNamedHomeCost", 0);
	}

	public static int getSetHomeCost(Player player) {
		return getSettingInt(player, "setHomeCost", 0);
	}

	public static int getHomeCost (Player player) {
		return getSettingInt(player, "homeCost", 0);
	}

	public static int getNamedHomeCost(Player player) {
		return getSettingInt(player, "namedHomeCost", 0);
	}
	
	public static int getOthersHomeCost(Player player) {
		return getSettingInt(player, "othersHomeCost", 0);
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
	
	public static boolean getSettingDisrupt(Player player) {
		return getSettingInt(player, "disruptWarmup", 1) == 1 ? true : false;
	}
	
	public static void sendMessageTooManyParameters(CommandSender sender) {
		String message = plugin.getConfig().getString("MultiHome.messages.tooManyParameters", null);

		if (message != null) Messaging.sendSuccess(sender, message);
	}

	public static void sendMessageDefaultHomeSet(CommandSender sender) {
		String message = plugin.getConfig().getString("MultiHome.messages.defaultHomeSetMessage", null);

		if (message != null) Messaging.sendSuccess(sender, message);
	}

	public static void sendMessageCannotDeleteDefaultHome(CommandSender sender) {
		String message = plugin.getConfig().getString("MultiHome.messages.cannotDeleteDefaultHomeMessage", null);

		if (message != null) Messaging.sendError(sender, message);
	}
	
	public static void sendMessageHomeSet(CommandSender sender, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeSetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{HOME\\}", home));
		}
	}
	
	public static void sendMessageHomeDeleted(CommandSender sender, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeDeletedMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{NAME\\}", home)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageWarmup(CommandSender sender, int timeLeft) {
		String message = plugin.getConfig().getString("MultiHome.messages.warmupMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{SECONDS\\}", Integer.toString(timeLeft)));
		}
	}

	public static void sendMessageWarmupComplete(CommandSender sender) {
		String message = plugin.getConfig().getString("MultiHome.messages.warmupCompleteMessage", null);

		if (message != null) Messaging.sendSuccess(sender, message);
	}

	public static void sendMessageWarmupDisrupted(CommandSender sender) {
		String message = plugin.getConfig().getString("MultiHome.messages.warmupDisruptedMessage", null);

		if (message != null) Messaging.sendError(sender, message);
	}

	public static void sendMessageCooldown(CommandSender sender, int timeLeft) {
		String message = plugin.getConfig().getString("MultiHome.messages.cooldownMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{SECONDS\\}", Integer.toString(timeLeft)));
		}
	}

	public static void sendMessageMaxHomes(CommandSender sender, int currentHomes, int maxHomes) {
		String message = plugin.getConfig().getString("MultiHome.messages.tooManyHomesMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{CURRENT\\}", Integer.toString(currentHomes))
					.replaceAll("\\{MAX\\}", Integer.toString(maxHomes)));
		}
	}

	public static void sendMessageNoHome(CommandSender sender, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.noHomeMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageNoDefaultHome(CommandSender sender) {
		String message = plugin.getConfig().getString("MultiHome.messages.noDefaultHomeMessage", null);
		
		if (message != null) Messaging.sendError(sender, message);
	}

	public static void sendMessageNoPlayer(CommandSender sender, String targetPlayer) {
		String message = plugin.getConfig().getString("MultiHome.messages.noPlayerMessage", null);
		
		if (message != null) {
			Messaging.sendError(sender, message
					.replaceAll("\\{PLAYER\\}", targetPlayer));
		}
	}

	public static void sendMessageHomeList(CommandSender sender, String homeList) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeListMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{LIST\\}", homeList));
		}
	}

	public static void sendMessageOthersHomeList(CommandSender sender, String player, String homeList) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeListOthersMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{PLAYER\\}", player)
					.replaceAll("\\{LIST\\}", homeList));
		}
	}

	public static void sendMessageInviteOwnerHome(CommandSender sender, String target, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeInviteOwnerMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageInviteTargetHome(CommandSender sender, String owner, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeInviteTargetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageInviteTimedOwnerHome(CommandSender sender, String target, String home, int time) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeInviteTimedOwnerMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{HOME\\}", home)
					.replaceAll("\\{TIME\\}", Integer.toString(time)));
		}
	}

	public static void sendMessageInviteTimedTargetHome(CommandSender sender, String owner, String home, int time) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeInviteTimedTargetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{HOME\\}", home)
					.replaceAll("\\{TIME\\}", Integer.toString(time)));
		}
	}

	public static void sendMessageUninviteOwnerHome(CommandSender sender, String target, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeUninviteOwnerMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageUninviteTargetHome(CommandSender sender, String owner, String home) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeUninviteTargetMessage", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{HOME\\}", home));
		}
	}

	public static void sendMessageInviteListToMe(CommandSender sender, String target, String list) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeListInvitesToMe", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{TARGET\\}", target)
					.replaceAll("\\{LIST\\}", list));
		}
	}

	public static void sendMessageInviteListToOthers(CommandSender sender, String owner, String list) {
		String message = plugin.getConfig().getString("MultiHome.messages.homeListInvitesToOthers", null);
		
		if (message != null) {
			Messaging.sendSuccess(sender, message
					.replaceAll("\\{OWNER\\}", owner)
					.replaceAll("\\{LIST\\}", list));
		}
	}

	public static void sendMessageNotEnoughMoney(Player player, double amount) {
		String message = plugin.getConfig().getString("MultiHome.messages.econNotEnoughFunds", null);

		if (message != null) {
			Messaging.sendError(player, message.replaceAll("\\{AMOUNT\\}", amount+""));
		}
	}

	public static void sendMessageDeductForHome(Player player, double amount) {
		String message = plugin.getConfig().getString("MultiHome.messages.econDeductedForHome", null);
		if (message != null) {
			Messaging.sendSuccess(player,message.replaceAll("\\{AMOUNT\\}", amount+""));
		}
	}

	public static void sendMessageDeductForSet(Player player, double amount) {
		String message = plugin.getConfig().getString("MultiHome.messages.econDeductedForSet", null);
		if (message != null) {
			Messaging.sendSuccess(player, message.replaceAll("\\{AMOUNT\\}", amount+""));
		}
	}
}
