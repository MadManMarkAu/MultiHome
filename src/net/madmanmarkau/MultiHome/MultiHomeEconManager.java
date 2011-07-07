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
		
		if (Settings.isEconomyEnabled()) {
			Plugin iConomy5 = plugin.getServer().getPluginManager().getPlugin("iConomy");

			if (iConomy5 != null && iConomy5.getDescription().getVersion().startsWith("5.")) {
				handler = EconomyHandler.ICONOMY5;
				Messaging.logInfo("Economy enabled using: iConomy v" + iConomy5.getDescription().getVersion(), plugin);
			} else {
				handler = EconomyHandler.NONE;
				Messaging.logWarning("An economy plugin wasn't detected!", plugin);
			}
		} else {
			handler = EconomyHandler.NONE;
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