package net.madmanmarkau.MultiHome;

import java.io.File;

import net.madmanmarkau.MultiHome.Data.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiHome extends JavaPlugin {
	private HomeManager homes;
	private InviteManager invites;
	private WarmUpManager warmups;
	private CoolDownManager cooldowns;

	private String pluginDataPath;
	
	private MultiHomeCommandExecutor commandExecutor;
	private MultiHomePlayerListener playerListener = new MultiHomePlayerListener(this);
	private MultiHomeEntityListener entityListener = new MultiHomeEntityListener(this);
	
	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		warmups.clearWarmups();
		Messaging.logInfo("Version " + this.getDescription().getVersion() + " unloaded.", this);
	}

	@Override
	public void onEnable() {
		String dataStoreMethod;
		pluginDataPath = this.getDataFolder().getAbsolutePath() + File.separator;
		
		File dataPath = new File(pluginDataPath);
		if (!dataPath.exists()) {
			dataPath.mkdirs();
		}

		if (!HomePermissions.initialize(this)) return;
		disableEssentials();
		Settings.initialize(this);
		Settings.loadSettings();
		MultiHomeEconManager.initialize(this);

		dataStoreMethod = Settings.getDataStoreMethod();

		if (dataStoreMethod.compareToIgnoreCase("file") == 0) {
			this.homes = new HomeManagerFile(this);
			this.invites = new InviteManagerFile(this);
			this.warmups = new WarmUpManagerFile(this);
			this.cooldowns = new CoolDownManagerFile(this);

			Messaging.logInfo("Using \"file\" storage method for database.", this);
		} else if (dataStoreMethod.compareToIgnoreCase("sql") == 0) {
			this.homes = new HomeManagerMySQL(this);
			this.invites = new InviteManagerMySQL(this);
			this.warmups = new WarmUpManagerMySQL(this);
			this.cooldowns = new CoolDownManagerMySQL(this);

			Messaging.logInfo("Using \"sql\" storage method for database.", this);
		} else {
			this.homes = new HomeManagerFile(this);
			this.invites = new InviteManagerFile(this);
			this.warmups = new WarmUpManagerFile(this);
			this.cooldowns = new CoolDownManagerFile(this);

			Messaging.logInfo("Unknown storage method. Defaulting to \"file\" storage method for database.", this);
		}

		

		/*ImportData.importHomesFromEssentials(out, this.plugin);
		ImportData.importHomesFromMultipleHomes(out, this.plugin);
		ImportData.importHomesFromMyHome(out, this.plugin);*/

		
		
		setupCommands();
		registerEvents();
		
		Messaging.logInfo("Version " + this.getDescription().getVersion() + " loaded.", this);
	}
	
	private void disableEssentials() {
		// Disable EssentialsHome
		Plugin essentialsHome = getServer().getPluginManager().getPlugin("EssentialsHome");

		if (essentialsHome != null) {
			if (!essentialsHome.isEnabled()) {
				// Load the plugin so we can disable it. Yeah, it's weird, but hopefully works.
				getServer().getPluginManager().enablePlugin(essentialsHome);
			}
			getServer().getPluginManager().disablePlugin(essentialsHome);
		}
	}

    private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(entityListener, this);
	}
    
    private void setupCommands() {
		this.commandExecutor = new MultiHomeCommandExecutor(this);
		
    	getCommand("home").setExecutor(commandExecutor);
    	getCommand("mhome").setExecutor(commandExecutor);
    	getCommand("sethome").setExecutor(commandExecutor);
    	getCommand("msethome").setExecutor(commandExecutor);
    	getCommand("deletehome").setExecutor(commandExecutor);
    	getCommand("mdeletehome").setExecutor(commandExecutor);
    	getCommand("listhomes").setExecutor(commandExecutor);
    	getCommand("mlisthomes").setExecutor(commandExecutor);
    	getCommand("invitehome").setExecutor(commandExecutor);
    	getCommand("minvitehome").setExecutor(commandExecutor);
    	getCommand("invitehometimed").setExecutor(commandExecutor);
    	getCommand("minvitehometimed").setExecutor(commandExecutor);
    	getCommand("uninvitehome").setExecutor(commandExecutor);
    	getCommand("muninvitehome").setExecutor(commandExecutor);
    	getCommand("listinvites").setExecutor(commandExecutor);
    	getCommand("mlistinvites").setExecutor(commandExecutor);
    	getCommand("listmyinvites").setExecutor(commandExecutor);
    	getCommand("mlistmyinvites").setExecutor(commandExecutor);
    }
    
    public HomeManager getHomeManager() {
    	return this.homes;
    }
    
    public InviteManager getInviteManager() {
    	return this.invites;
    }
    
    public WarmUpManager getWarmUpManager() {
    	return this.warmups;
    }
    
    public CoolDownManager getCoolDownManager() {
    	return this.cooldowns;
    }
    
    public String getPluginDataPath() {
    	return this.pluginDataPath;
    }
}
