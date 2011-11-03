package net.madmanmarkau.MultiHome;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Messaging {
	public static void sendError(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.RED + message);
	}

	public static void sendError(Player player, String message) {
		player.sendMessage(ChatColor.RED + message);
	}

	public static void sendSuccess(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.GOLD + message);
	}

	public static void sendSuccess(Player player, String message) {
		player.sendMessage(ChatColor.GOLD + message);
	}
	
	public static void logSevere(String message, JavaPlugin plugin) {
		Logger log = Logger.getLogger("Minecraft");
		
		log.log(Level.SEVERE, "[" + plugin.getDescription().getName() + "] " + message);
	}
	
	public static void logWarning(String message, JavaPlugin plugin) {
		Logger log = Logger.getLogger("Minecraft");
		
		log.log(Level.WARNING, "[" + plugin.getDescription().getName() + "] " + message);
	}
	
	public static void logInfo(String message, JavaPlugin plugin) {
		Logger log = Logger.getLogger("Minecraft");
		
		log.log(Level.INFO, "[" + plugin.getDescription().getName() + "] " + message);
	}
	
	public static void logFine(String message, JavaPlugin plugin) {
		Logger log = Logger.getLogger("Minecraft");
		
		log.log(Level.FINE, "[" + plugin.getDescription().getName() + "] " + message);
	}
	
	public static void broadcast(String message, JavaPlugin plugin) {
		plugin.getServer().broadcastMessage(message);
	}
}
