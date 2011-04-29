package net.madmanmarkau.MultiHome;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;

public class Permissions {
	private static PermissionHandler Permissions;
	
	public static void initialize(JavaPlugin plugin) {
		Plugin perm = plugin.getServer().getPluginManager().getPlugin("Permissions");
		
		if (Permissions == null) {
			if (perm!= null) {
				plugin.getServer().getPluginManager().enablePlugin(perm);
				Permissions = ((com.nijikokun.bukkit.Permissions.Permissions) perm).getHandler();
			}
			else {
				Messaging.logWarning("Version " + plugin.getDescription().getVersion() + " not enabled. Permissions not detected.", plugin);
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
	}
	
	public static boolean has(Player player, String permission) {
		return Permissions.has(player, permission);
	}
	
	public static String getGroup(String world, String player) {
		return Permissions.getGroup(world, player);
	}
}
