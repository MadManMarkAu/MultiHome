package net.madmanmarkau.MultiHome;

import org.bukkit.plugin.Plugin;
import com.nijikokun.register.payment.*;


public class MultiHomeEconManager {

	public static EconomyHandler handler;
	public static MultiHome plugin;

	public enum EconomyHandler {
		REGISTER, NONE
	}

	protected static void initialize(MultiHome plugin) {
		MultiHomeEconManager.plugin = plugin;
		
		if (Settings.isEconomyEnabled()) {
			Plugin pRegister = plugin.getServer().getPluginManager().getPlugin("Register");

			if (pRegister != null && pRegister.getDescription().getVersion().startsWith("1.")) {
				handler = EconomyHandler.REGISTER;
				Messaging.logInfo("Economy enabled using: Register v" + pRegister.getDescription().getVersion(), plugin);
			} else {
				handler = EconomyHandler.NONE;
				Messaging.logWarning("An economy plugin wasn't detected!", plugin);
			}
		} else {
			handler = EconomyHandler.NONE;
		}
	}

	public static boolean hasEnough(String player, double amount) {
		if (handler == EconomyHandler.REGISTER) {
			Method method = Methods.getMethod();
			
			if (method != null) {
				return method.getAccount(player).hasEnough(amount);
			} else {
				return true;
			}
		} else
			return true;
	}

	public static boolean chargePlayer(String player, double amount) {
		if (handler == EconomyHandler.REGISTER) {
			if (hasEnough(player, amount)) {
				Method method = Methods.getMethod();
				
				if (method != null) {
					method.getAccount(player).subtract(amount);
				}
				return true;
			} else
				return false;
		} else
			return true;
	}

	public static String formatCurrency(double amount) {
		if (handler == EconomyHandler.REGISTER) {
			Method method = Methods.getMethod();
		
			if (method != null) {
				return Methods.getMethod().format(amount);
			} else {
				return amount+"";
			}
		} else
			return amount+"";
	}
}