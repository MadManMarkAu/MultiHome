package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author MadManMarkAu
 */
public class InviteManager {
	MultiHome plugin;
	
    private File invitesFile;
	private HashMap<String, ArrayList<HomeInvite>> homeInvites = new HashMap<String, ArrayList<HomeInvite>>();
	private boolean enableAutoSave = true;
	private boolean saveRequired = false;
	
	public InviteManager(File invitesFile, MultiHome plugin) {
		this.invitesFile = invitesFile;
		this.plugin = plugin;
	}

	/**
	 * Save invites list to file. Clears the saveRequired flag.
	 */
	public void saveInvites() {
		updateInviteExpiry();
		saveInvitesLocal();
	}

	/**
	 * Load the invites list from file.
	 * @return True if load succeeds, otherwise false.
	 */
	public void loadInvites() {
		loadInvitesLocal();
		updateInviteExpiry();
	}
	
	/**
	 * Enable auto-saving when changes to the invites list are made.
	 */
	public void enableAutoSave() {
		this.enableAutoSave = true;
		
		if (this.saveRequired) {
			this.saveInvitesLocal();
		}
	}

	/**
	 * Disable auto-saving when changes to the invites list are made.
	 */
	public void disableAutoSave() {
		this.enableAutoSave = false;
	}

	/**
	 * Query the auto-save status.
	 */
	public boolean getAutoSave() {
		return this.enableAutoSave;
	}
	
	/**
	 * Query whether or not data save is required.
	 */
	public boolean getSaveRequired(){
		return this.saveRequired;
	}
	
	/**
	 * Clears all current invites.
	 */
	public void clearInvites() {
		this.homeInvites.clear();
	}
	
	/**
	 * Returns a HomeInvite object for the specified invite. If invite is not found, returns null. 
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @return HomeInvite object for this invite. Otherwise null.
	 */
	public HomeInvite getInvite(String owner, String home, String target) {
		updateInviteExpiry();

		if (this.homeInvites.containsKey(owner.toLowerCase())) {
			ArrayList<HomeInvite> invites = this.homeInvites.get(owner.toLowerCase());
			
			for (HomeInvite thisInvite : invites) {
				if (thisInvite.getInviteHome().compareToIgnoreCase(home) == 0 && (thisInvite.getInviteTarget().compareToIgnoreCase("*") == 0 || thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0)) {
					return thisInvite;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	public void addInvite(String owner, String home, String target) {
		this.addInvite(owner.toLowerCase(), home.toLowerCase(), target.toLowerCase(), null, null);
	}

	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param expiry Date object for when this invite expires. Use null to specify no expiry.
	 */
	public void addInvite(String owner, String home, String target, Date expiry) {
		this.addInvite(owner.toLowerCase(), home.toLowerCase(), target.toLowerCase(), expiry, null);
	}

	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param reason String containing the invitation text/reason.
	 */
	public void addInvite(String owner, String home, String target, String reason) {
		this.addInvite(owner.toLowerCase(), home.toLowerCase(), target.toLowerCase(), null, reason);
	}
	
	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param expiry Date object for when this invite expires. Use null to specify no expiry.
	 * @param reason String containing the invitation text/reason.
	 */
	public void addInvite(String owner, String home, String target, Date expiry, String reason) {
		ArrayList<HomeInvite> invites;
		if (this.homeInvites.containsKey(owner.toLowerCase())) {
			invites = this.homeInvites.get(owner.toLowerCase());
		} else {
			invites = new ArrayList<HomeInvite>();
		}

		boolean inviteSet = false;
		
		for (int index = 0; index < invites.size(); index++) {
			HomeInvite thisInvite = invites.get(index);
			if (thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0 && thisInvite.getInviteHome().compareToIgnoreCase(home) == 0) {
				thisInvite.setInviteExpires(expiry);;
				thisInvite.setInviteReason(reason);
				invites.set(index, thisInvite);
				this.saveRequired = true;
				inviteSet = true;
			}
		}
		
		if (!inviteSet) {
			HomeInvite invite = new HomeInvite(owner.toLowerCase(), home.toLowerCase(), target.toLowerCase(), expiry, reason);
			invites.add(invite);
			this.saveRequired = true;
		}
		
		this.homeInvites.put(owner.toLowerCase(), invites);
		
		updateInviteExpiry();
		
		if (this.enableAutoSave) {
			this.saveInvitesLocal();
		}
	}

	/**
	 * Remove an existing invite.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	public void removeInvite(String owner, String home, String target) {
		if (this.homeInvites.containsKey(owner.toLowerCase())) {
			ArrayList<HomeInvite> playerInviteList = this.homeInvites.get(owner.toLowerCase());
			ArrayList<HomeInvite> removeList = new ArrayList<HomeInvite>();
			
			for (HomeInvite thisInvite : playerInviteList) {
				if (thisInvite.getInviteHome().compareToIgnoreCase(home) == 0 && thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0) {
					removeList.add(thisInvite);
					this.saveRequired = true;
				}
			}
			
			playerInviteList.removeAll(removeList);
			
			this.homeInvites.put(owner.toLowerCase(), playerInviteList);
			
			if (this.enableAutoSave && this.saveRequired) {
				this.saveInvitesLocal();
			}
		}
	}

	/**
	 * Returns a list of home locations the specified player may visit.
	 * @param target Player to list invites for.
	 * @return ArrayList<HomeInvite> containing list of invites.
	 */
	public ArrayList<HomeInvite> getListPlayerInvitesToMe(String target) {
		updateInviteExpiry();

		ArrayList<HomeInvite> activeInvites = new ArrayList<HomeInvite>();
		
		for (Entry<String, ArrayList<HomeInvite>> thisEntry : this.homeInvites.entrySet()) {
			for (HomeInvite thisInvite : thisEntry.getValue()) {
				if (thisInvite.getInviteTarget().compareToIgnoreCase("*") == 0 || thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0) {
					activeInvites.add(thisInvite);
				}
			}
		}
		
		return activeInvites;
	}

	/**
	 * Returns a list of invites the owner has given to others.
	 * @param owner Player to list invites for.
	 * @return ArrayList<HomeInvite> containing list of invites.
	 */
	public ArrayList<HomeInvite> getListPlayerInvitesToOthers(String owner) {
		updateInviteExpiry();

		if (this.homeInvites.containsKey(owner.toLowerCase())) {
			return this.homeInvites.get(owner.toLowerCase());
		}

		return new ArrayList<HomeInvite>();
	}
	
	/**
	 * Scans through the invites list, removing expired invites.
	 */
	public void updateInviteExpiry() {
		Date now = new Date();

		// Remove expired invites.
		for (Entry<String, ArrayList<HomeInvite>> entry : this.homeInvites.entrySet()) {
			ArrayList<HomeInvite> invites = entry.getValue();
			ArrayList<HomeInvite> removeList = new ArrayList<HomeInvite>();
			
			for (HomeInvite thisInvite : invites) {
				if (thisInvite.getInviteExpires() != null) {
					if (thisInvite.getInviteExpires().getTime() < now.getTime()) {
						removeList.add(thisInvite);
						this.saveRequired = true;
					}
				}
			}
			
			invites.removeAll(removeList);

			this.homeInvites.put(entry.getKey(), invites);
		}
		
		// Remove empty users.
		ArrayList<String> removeList = new ArrayList<String>();
		for (Entry<String, ArrayList<HomeInvite>> entry : this.homeInvites.entrySet()) {
			ArrayList<HomeInvite> invites = entry.getValue();
			
			if (invites.size() == 0) {
				removeList.add(entry.getKey());
			}
		}
		
		for (String entry : removeList) {
			this.homeInvites.remove(entry);
			this.saveRequired = true;
		}
		
		if (this.saveRequired && this.enableAutoSave) {
			this.saveInvitesLocal();
		}
	}
	
	/**
	 * Save invites list to file. Clears the saveRequired flag.
	 */
	private void saveInvitesLocal() {
		try {
			FileWriter fstream = new FileWriter(this.invitesFile);
			BufferedWriter writer = new BufferedWriter(fstream);

			writer.write("# Stores user home invites." + Util.newLine());
			writer.write("# <owner>;<home>;<target>;[<expiry>];[<reason>]" + Util.newLine());
			writer.write(Util.newLine());

			String owner;
			String home;
			String target;
			String expiry;
			String reason;

			for (Entry<String, ArrayList<HomeInvite>> entry : this.homeInvites.entrySet()) {
				owner = entry.getKey();
				for (HomeInvite thisInvite : entry.getValue()) {
					home = thisInvite.getInviteHome();
					target = thisInvite.getInviteTarget();
					expiry = "";
					if (thisInvite.getInviteExpires() != null) expiry = Long.toString(thisInvite.getInviteExpires().getTime());
					reason = "";
					if (thisInvite.getInviteReason() != null && thisInvite.getInviteReason().length() > 0) reason = thisInvite.getInviteReason();

					writer.write(owner.toLowerCase() + ";" + home.toLowerCase() + ";" + target.toLowerCase() + ";" + expiry + ";" + reason + Util.newLine());
				}
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the invites file.", this.plugin);
			e.printStackTrace();
		}
		
		this.saveRequired = false;
	}

	/**
	 * Load the invites list from file.
	 * @return True if load succeeds, otherwise false.
	 */
	private void loadInvitesLocal() {
		// Create homes file if not exist
		if (!invitesFile.exists()) {
			try {
				FileWriter fstream = new FileWriter(this.invitesFile);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# Stores user home invites." + Util.newLine());
				out.write("# <owner>;<home>;<target>;[<expiry>];[<reason>]" + Util.newLine());
				out.write(Util.newLine());

				ImportData.importInvitesFromMyHome(out, this.plugin);
				
				out.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not write the deafult invites file. Plugin disabled.", this.plugin);
				e.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		}

		boolean oldAutoSave = this.enableAutoSave;
		this.enableAutoSave = false;
		
		try {
			FileReader fstream = new FileReader(this.invitesFile);
			BufferedReader reader = new BufferedReader(fstream);

			String line = reader.readLine().trim();

			this.clearInvites();
			
			while (line != null) {
				if (!line.startsWith("#") && line.length() > 0) {
					String[] values = line.split(";");
					String owner;
					String home;
					String target;
					Date expiry;
					String reason;

					try {
						if (values.length == 3 || (values.length == 4 && values[3].length() == 0)) {
							owner = values[0].toLowerCase();
							home = values[1].toLowerCase();
							target = values[2].toLowerCase();
							
							addInvite(owner, home, target);
						} else if (values.length == 4 && values[3].length() > 0) {
							owner = values[0].toLowerCase();
							home = values[1].toLowerCase();
							target = values[2].toLowerCase();
							expiry = new Date(Long.parseLong(values[3]));

							addInvite(owner, home, target, expiry);
						} else if (values.length >= 5) {
							owner = values[0].toLowerCase();
							home = values[1].toLowerCase();
							target = values[2].toLowerCase();
							expiry = new Date(Long.parseLong(values[3]));
							reason = Util.joinString(values, 4, ";");

							addInvite(owner, home, target, expiry, reason);
						}
					} catch (Exception e) {
						// This entry failed. Ignore and continue.
						if (line!=null) {
							Messaging.logWarning("Failed to load invite list! Line: " + line, this.plugin);
						}
					}
				}

				line = reader.readLine();
			}

			reader.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not read the invite list. Plugin disabled.", this.plugin);
			e.printStackTrace();
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		
		this.enableAutoSave = oldAutoSave;
		this.saveRequired = false;
	}
}
