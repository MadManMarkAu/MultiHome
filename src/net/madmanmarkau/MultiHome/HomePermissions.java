package net.madmanmarkau.MultiHome;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijikokun.bukkit.Permissions.Permissions;

/**
*
* @author Sleaker
*
*/
public class HomePermissions {
	private static PermissionsHandler handler;
	private static Plugin permissionPlugin = null;


	private enum PermissionsHandler {
		PERMISSIONSEX, PERMISSIONS, NONE
	}

	public static boolean initialize(JavaPlugin plugin) {
		Plugin perm = plugin.getServer().getPluginManager().getPlugin("Permissions");
		Plugin permex = plugin.getServer().getPluginManager().getPlugin("PermissionsEX");

		if (permex != null) {
			permissionPlugin = permex;
			handler = PermissionsHandler.PERMISSIONSEX;
			return true;
		} else if (perm != null) {
			permissionPlugin = perm;
			handler = PermissionsHandler.PERMISSIONS;
			return true;
		} else {
			handler = PermissionsHandler.NONE;
			Messaging.logWarning(" - A permission plugin was not detected! Disabling.", plugin);
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}

		return false;
	}

	public static boolean has(Player player, String permission) {
		switch (handler) {
		case PERMISSIONSEX:
			return PermissionsEx.getPermissionManager().has(player, permission);
		case PERMISSIONS:
			return ((Permissions) permissionPlugin).getHandler().has(player, permission);
		default:
			return false;
		}
	}

	public static String getGroup(String world, String player) {
		String[] groups = {};
		
		switch (handler) {
			case PERMISSIONSEX:
				groups = PermissionsEx.getPermissionManager().getUser(player).getGroupsNames();
			case PERMISSIONS:
				groups = ((Permissions) permissionPlugin).getHandler().getGroups(world, player);
		}

		return groups.length > 0 ? groups[0] : null;
	}
}
