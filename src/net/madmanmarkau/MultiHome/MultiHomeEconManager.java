package net.madmanmarkau.MultiHome;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.nijikokun.register.payment.*;


public class MultiHomeEconManager {

	public static EconomyHandler handler;
	private static Economy vault = null;
	public static MultiHome plugin;

	public enum EconomyHandler {
		REGISTER, VAULT, NONE
	}

	protected static void initialize(MultiHome plugin) {
		MultiHomeEconManager.plugin = plugin;
		
		if (Settings.isEconomyEnabled()) {
			Plugin pRegister = plugin.getServer().getPluginManager().getPlugin("Register");

			if (pRegister != null && pRegister.getDescription().getVersion().startsWith("1.")) {
				handler = EconomyHandler.REGISTER;
				Messaging.logInfo("Economy enabled using: Register v" + pRegister.getDescription().getVersion(), plugin);
				return;
			}

	        RegisteredServiceProvider<Economy> vaultEconomyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (vaultEconomyProvider != null) {
				handler = EconomyHandler.VAULT;
	        	vault = vaultEconomyProvider.getProvider();
				Messaging.logInfo("Economy enabled using: Vault", plugin);
				return;
	        }
			
			handler = EconomyHandler.NONE;
			Messaging.logWarning("An economy plugin wasn't detected!", plugin);
		} else {
			handler = EconomyHandler.NONE;
		}
	}

	// Determine if player has enough money to cover [amount]
	public static boolean hasEnough(String player, double amount) {
		switch (handler) {
		case REGISTER:
			Method method = Methods.getMethod();
			
			if (method != null) {
				return method.getAccount(player).hasEnough(amount);
			} else {
				return true;
			}
			
		case VAULT:
			if (vault != null) {
				return vault.has(player, amount);
			}
			break;
		}
		
		return true;
	}

	// Remove [amount] from players account
	public static boolean chargePlayer(String player, double amount) {
		switch (handler) {
		case REGISTER:
			if (hasEnough(player, amount)) {
				Method method = Methods.getMethod();
				
				if (method != null) {
					method.getAccount(player).subtract(amount);
				}
				return true;
			} else
				return false;
			
		case VAULT:
			if (vault != null) {
				return vault.bankWithdraw(player, amount).transactionSuccess();
			}
			break;
		}

		return true;
	}

	// Format the monetary amount into a string, according to the configured format
	public static String formatCurrency(double amount) {
		switch (handler) {
		case REGISTER:
			Method method = Methods.getMethod();
			
			if (method != null) {
				return Methods.getMethod().format(amount);
			} else {
				return amount+"";
			}
			
		case VAULT:
			if (vault != null) {
				return vault.format(amount);
			}
			break;
		}

		return amount+"";
	}
}