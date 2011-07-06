package net.madmanmarkau.MultiHome;

import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;


public class MultiHomeEconManager {

	public static EconomyHandler handler;
	public static MultiHome plugin;

	public enum EconomyHandler {
		ICONOMY5, NONE
	}

	protected static void initialize(MultiHome plugin) {
		MultiHomeEconManager.plugin = plugin;
		Plugin iConomy5 = null;
		if (plugin.getServer().getPluginManager().getPlugin("iConomy").getDescription().getVersion().contains("5.")) {
			iConomy5 = plugin.getServer().getPluginManager().getPlugin("iConomy");
		}

		if (iConomy5 != null && Settings.isEconomyEnabled()) {
			handler = EconomyHandler.ICONOMY5;
			String version = iConomy5.getDescription().getVersion();
			Messaging.logInfo("Economy enabled using: iConomy v" + version, plugin);
		} else {
			handler = EconomyHandler.NONE;
			Messaging.logInfo("An economy plugin wasn't detected or MultiHome is not set to use economy", plugin);
		}
	}

	public static boolean hasEnough(String player, double amount) {
		if (handler == EconomyHandler.ICONOMY5) {
			return iConomy.getAccount(player).getHoldings().hasEnough(amount);
		} else
			return true;
	}

	public static boolean chargePlayer(String player, double amount) {
		if (handler == EconomyHandler.ICONOMY5) {
			if (hasEnough(player, amount)) {
				iConomy.getAccount(player).getHoldings().subtract(amount);
				return true;
			} else
				return false;
		} else
			return true;
	}

	public static String formatCurrency(double amount) {
		if (handler == EconomyHandler.ICONOMY5)
			return iConomy.format(amount);
		else
			return amount+"";
	}
}