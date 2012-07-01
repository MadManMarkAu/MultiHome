package net.madmanmarkau.MultiHome.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import net.madmanmarkau.MultiHome.Messaging;
import net.madmanmarkau.MultiHome.MultiHome;
import net.madmanmarkau.MultiHome.Util;

/**
 * @author MadManMarkAu
 */
public class InviteManagerFile extends InviteManager {
    private File invitesFile;
	private HashMap<String, ArrayList<InviteEntry>> inviteEntries = new HashMap<String, ArrayList<InviteEntry>>();
	
	public InviteManagerFile(MultiHome plugin) {
		super(plugin);
		this.invitesFile = new File(plugin.getDataFolder(), "invites.txt");
		
		loadInvites();
	}

	@Override
	public void clearInvites() {
		this.inviteEntries.clear();

		saveInvites();
	}

	@Override
	public InviteEntry getInvite(String owner, String home, String target) {
		updateInviteExpiry();

		if (this.inviteEntries.containsKey(owner.toLowerCase())) {
			ArrayList<InviteEntry> invites = this.inviteEntries.get(owner.toLowerCase());
			
			for (InviteEntry thisInvite : invites) {
				if (thisInvite.getInviteHome().compareToIgnoreCase(home) == 0 && (thisInvite.getInviteTarget().compareToIgnoreCase("*") == 0 || thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0)) {
					return thisInvite;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void addInvite(String owner, String home, String target, Date expiry, String reason) {
		boolean inviteSet = false;

		ArrayList<InviteEntry> invites;
		if (this.inviteEntries.containsKey(owner.toLowerCase())) {
			invites = this.inviteEntries.get(owner.toLowerCase());
		} else {
			invites = new ArrayList<InviteEntry>();
		}
		
		for (int index = 0; index < invites.size(); index++) {
			InviteEntry thisInvite = invites.get(index);
			if (thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0 && thisInvite.getInviteHome().compareToIgnoreCase(home) == 0) {
				thisInvite.setInviteSource(owner);
				thisInvite.setInviteHome(home);
				thisInvite.setInviteTarget(target);
				thisInvite.setInviteExpires(expiry);
				thisInvite.setInviteReason(reason);
				invites.set(index, thisInvite);
				inviteSet = true;
			}
		}
		
		if (!inviteSet) {
			InviteEntry invite = new InviteEntry(owner, home, target, expiry, reason);
			invites.add(invite);
		}
		
		this.inviteEntries.put(owner.toLowerCase(), invites);
		
		updateInviteExpiry();
		saveInvites();
	}

	@Override
	public void removeInvite(String owner, String home, String target) {
		if (this.inviteEntries.containsKey(owner.toLowerCase())) {
			ArrayList<InviteEntry> playerInviteList = this.inviteEntries.get(owner.toLowerCase());
			ArrayList<InviteEntry> removeList = new ArrayList<InviteEntry>();
			
			for (InviteEntry thisInvite : playerInviteList) {
				if (thisInvite.getInviteHome().compareToIgnoreCase(home) == 0 && thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0) {
					removeList.add(thisInvite);
				}
			}
			
			playerInviteList.removeAll(removeList);
			
			this.inviteEntries.put(owner.toLowerCase(), playerInviteList);

			updateInviteExpiry();
			saveInvites();
		}
	}

	@Override
	public ArrayList<InviteEntry> listPlayerInvitesToMe(String target) {
		updateInviteExpiry();

		ArrayList<InviteEntry> activeInvites = new ArrayList<InviteEntry>();
		
		for (Entry<String, ArrayList<InviteEntry>> thisEntry : this.inviteEntries.entrySet()) {
			for (InviteEntry thisInvite : thisEntry.getValue()) {
				if (thisInvite.getInviteTarget().compareToIgnoreCase("*") == 0 || thisInvite.getInviteTarget().compareToIgnoreCase(target) == 0) {
					activeInvites.add(thisInvite);
				}
			}
		}
		
		return activeInvites;
	}

	@Override
	public ArrayList<InviteEntry> listPlayerInvitesToOthers(String owner) {
		updateInviteExpiry();

		if (this.inviteEntries.containsKey(owner.toLowerCase())) {
			return this.inviteEntries.get(owner.toLowerCase());
		}

		return new ArrayList<InviteEntry>();
	}


	@Override
	public void importInvites(ArrayList<InviteEntry> invites, boolean overwrite) {
		ArrayList<InviteEntry> playerInvites;

		updateInviteExpiry();
		
		for (InviteEntry thisEntry : invites) {
			// Get the ArrayList of invites for this player
			if (this.inviteEntries.containsKey(thisEntry.getInviteSource().toLowerCase())) {
				playerInvites = this.inviteEntries.get(thisEntry.getInviteSource().toLowerCase());
			} else {
				playerInvites = new ArrayList<InviteEntry>();
			}

			boolean inviteFound = false;
			
			for (int index = 0; index < playerInvites.size(); index++) {
				InviteEntry thisInvite = playerInvites.get(index);
				if (thisInvite.getInviteHome().compareToIgnoreCase(thisEntry.getInviteHome()) == 0 && thisInvite.getInviteTarget().compareToIgnoreCase(thisEntry.getInviteTarget()) == 0) {
					// An existing home was found.
					if (overwrite) {
						thisInvite.setInviteSource(thisEntry.getInviteSource());
						thisInvite.setInviteHome(thisEntry.getInviteHome());
						thisInvite.setInviteTarget(thisEntry.getInviteTarget());
						thisInvite.setInviteExpires(thisEntry.getInviteExpires());
						thisInvite.setInviteReason(thisEntry.getInviteReason());
						playerInvites.set(index, thisInvite);
					}
					
					inviteFound = true;
				}
			}
			
			if (!inviteFound) {
				// No existing location found. Create new entry.
				InviteEntry newInvite = new InviteEntry(thisEntry.getInviteSource(), thisEntry.getInviteHome(), thisEntry.getInviteTarget(), thisEntry.getInviteExpires(), thisEntry.getInviteReason());
				playerInvites.add(newInvite);
			}

			// Replace the ArrayList in the homes HashMap
			this.inviteEntries.remove(thisEntry.getInviteSource().toLowerCase());
			this.inviteEntries.put(thisEntry.getInviteSource().toLowerCase(), playerInvites);
		}

		// Save
		updateInviteExpiry();
		saveInvites();
	}

	/**
	 * Scans through the invites list, removing expired invites.
	 */
	private void updateInviteExpiry() {
		Date now = new Date();

		// Remove expired invites.
		for (Entry<String, ArrayList<InviteEntry>> entry : this.inviteEntries.entrySet()) {
			ArrayList<InviteEntry> invites = entry.getValue();
			ArrayList<InviteEntry> removeList = new ArrayList<InviteEntry>();
			
			for (InviteEntry thisInvite : invites) {
				if (thisInvite.getInviteExpires() != null) {
					if (thisInvite.getInviteExpires().getTime() < now.getTime()) {
						removeList.add(thisInvite);
					}
				}
			}
			
			invites.removeAll(removeList);

			this.inviteEntries.put(entry.getKey(), invites);
		}
		
		// Remove empty users.
		ArrayList<String> removeList = new ArrayList<String>();
		for (Entry<String, ArrayList<InviteEntry>> entry : this.inviteEntries.entrySet()) {
			ArrayList<InviteEntry> invites = entry.getValue();
			
			if (invites.size() == 0) {
				removeList.add(entry.getKey());
			}
		}
		
		for (String entry : removeList) {
			this.inviteEntries.remove(entry);
		}
	}
	
	/**
	 * Save invites list to file. Clears the saveRequired flag.
	 */
	private void saveInvites() {
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

			for (Entry<String, ArrayList<InviteEntry>> entry : this.inviteEntries.entrySet()) {
				for (InviteEntry thisInvite : entry.getValue()) {
					owner = thisInvite.getInviteSource();
					home = thisInvite.getInviteHome();
					target = thisInvite.getInviteTarget();
					expiry = "";
					if (thisInvite.getInviteExpires() != null) expiry = Long.toString(thisInvite.getInviteExpires().getTime());
					reason = "";
					if (thisInvite.getInviteReason() != null && thisInvite.getInviteReason().length() > 0) reason = thisInvite.getInviteReason();

					writer.write(owner + ";" + home + ";" + target + ";" + expiry + ";" + reason + Util.newLine());
				}
			}
			writer.close();
		} catch (Exception e) {
			Messaging.logSevere("Could not write the invites file.", this.plugin);
		}
	}

	/**
	 * Load the invites list from file.
	 */
	private void loadInvites() {
		this.clearInvites();

		// Create homes file if not exist
		if (!invitesFile.exists()) {
			try {
				FileWriter fstream = new FileWriter(this.invitesFile);
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# Stores user home invites." + Util.newLine());
				out.write("# <owner>;<home>;<target>;[<expiry>];[<reason>]" + Util.newLine());
				out.write(Util.newLine());
				out.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not write the deafult invites file.", this.plugin);
				return;
			}
		} else {
			try {
				FileReader fstream = new FileReader(this.invitesFile);
				BufferedReader reader = new BufferedReader(fstream);
	
				String line = reader.readLine().trim();
	
				this.inviteEntries.clear();
				
				while (line != null) {
					if (!line.startsWith("#") && line.length() > 0) {
						InviteEntry thisInvite;
						
						thisInvite = parseInviteLine(line);
						
						if (thisInvite != null) {
							ArrayList<InviteEntry> inviteList;
	
							// Find HashMap entry for player
							if (!this.inviteEntries.containsKey(thisInvite.getInviteSource().toLowerCase())) {
								inviteList = new ArrayList<InviteEntry>();
							} else {
								// Player not exist. Create dummy entry.
								inviteList = this.inviteEntries.get(thisInvite.getInviteSource().toLowerCase());
							}
							
							// Don't save if this is a duplicate entry.
							boolean save = true;
							for (InviteEntry invite : inviteList) {
								if (invite.getInviteSource().compareToIgnoreCase(thisInvite.getInviteSource()) == 0) {
									save = false;
								}
							}
							
							if (save) {
								inviteList.add(thisInvite);
							}
	
							this.inviteEntries.put(thisInvite.getInviteSource().toLowerCase(), inviteList);
						}
					}
	
					line = reader.readLine();
				}
	
				reader.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not read the invite list.", this.plugin);
			}
		}
	}

	private InviteEntry parseInviteLine(String line) {
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
				
				return new InviteEntry(owner, home, target);
			} else if (values.length == 4 && values[3].length() > 0) {
				owner = values[0].toLowerCase();
				home = values[1].toLowerCase();
				target = values[2].toLowerCase();
				expiry = new Date(Long.parseLong(values[3]));

				return new InviteEntry(owner, home, target, expiry);
			} else if (values.length >= 5) {
				owner = values[0].toLowerCase();
				home = values[1].toLowerCase();
				target = values[2].toLowerCase();
				expiry = new Date(Long.parseLong(values[3]));
				reason = Util.joinString(values, 4, ";");

				return new InviteEntry(owner, home, target, expiry, reason);
			}
		} catch (Exception e) {
			// This entry failed. Ignore and continue.
			if (line!=null) {
				Messaging.logWarning("Failed to load invite list! Line: " + line, this.plugin);
			}
		}
		
		return null;
	}
}
