package net.madmanmarkau.MultiHome;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;

public class Permissions {
	private static PermissionHandler permissions;
	
	public static void initialize(JavaPlugin plugin) {
		Plugin perm = plugin.getServer().getPluginManager().getPlugin("Permissions");
		
		if (permissions == null) {
			if (perm!= null) {
				plugin.getServer().getPluginManager().enablePlugin(perm);
				permissions = ((com.nijikokun.bukkit.Permissions.Permissions) perm).getHandler();
			}
			else {
				Messaging.logWarning("Version " + plugin.getDescription().getVersion() + " not enabled. Permissions not detected.", plugin);
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
	}
	
	public static boolean has(Player player, String permission) {
		return permissions.has(player, permission);
	}
	
	public static String getGroup(String world, String player) {
		String[] groups = permissions.getGroups(world, player);
		
		if (groups.length > 0) {
			return groups[0];
		}
		
		return "";
	}
}
