package net.madmanmarkau.MultiHome.Data;

import java.util.ArrayList;
import java.util.Date;

import net.madmanmarkau.MultiHome.MultiHome;

import org.bukkit.entity.Player;

/**
 * Base class for invite database objects.
 * @author MadManMarkAu
 */
public abstract class InviteManager {
	protected final MultiHome plugin;

	/**
	 * @param plugin The plug-in.
	 */
	public InviteManager(MultiHome plugin) {
		this.plugin = plugin;
	}

	/**
	 * Deletes all invites from the database.
	 */
	abstract public void clearInvites();

	/**
	 * Returns an InviteEntry object for the specified invite. If invite is not found, returns null. 
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	public final InviteEntry getInvite(Player owner, String home, Player target) {
		return this.getInvite(owner.getName(), home, target.getName());
	}

	/**
	 * Returns an InviteEntry object for the specified invite. If invite is not found, returns null. 
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	abstract public InviteEntry getInvite(String owner, String home, String target);
	
	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	public final void addInvite(Player owner, String home, Player target) {
		this.addInvite(owner.getName(), home, target.getName(), null, null);
	}
	
	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	public final void addInvite(String owner, String home, String target) {
		this.addInvite(owner, home, target, null, null);
	}
	
	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param expiry Date object for when this invite expires. Use null to specify no expiry.
	 */
	public final void addInvite(Player owner, String home, Player target, Date expiry) {
		this.addInvite(owner.getName(), home, target.getName(), expiry, null);
	}
	
	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param expiry Date object for when this invite expires. Use null to specify no expiry.
	 */
	public final void addInvite(String owner, String home, String target, Date expiry) {
		this.addInvite(owner, home, target, expiry, null);
	}

	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param reason String containing the invitation text/reason.
	 */
	public final void addInvite(Player owner, String home, Player target, String reason) {
		this.addInvite(owner.getName(), home, target.getName(), null, reason);
	}

	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param reason String containing the invitation text/reason.
	 */
	public final void addInvite(String owner, String home, String target, String reason) {
		this.addInvite(owner, home, target, null, reason);
	}

	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param expiry Date object for when this invite expires. Use null to specify no expiry.
	 * @param reason String containing the invitation text/reason.
	 */
	public final void addInvite(Player owner, String home, Player target, Date expiry, String reason) {
		this.addInvite(owner.getName(), home, target.getName(), expiry, reason);
	}

	/**
	 * Adds a new invite or updates an existing one.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 * @param expiry Date object for when this invite expires. Use null to specify no expiry.
	 * @param reason String containing the invitation text/reason.
	 */
	abstract public void addInvite(String owner, String home, String target, Date expiry, String reason);

	/**
	 * Remove an existing invite.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	public final void removeInvite(Player owner, String home, Player target) {
		this.removeInvite(owner.getName(), home, target.getName());
	}

	/**
	 * Remove an existing invite.
	 * @param owner Owner of the invite.
	 * @param home Name of the owner's home location.
	 * @param target Player the owner is inviting.
	 */
	abstract public void removeInvite(String owner, String home, String target);
	
	/**
	 * Returns a list of home locations the specified player may visit.
	 * @param target Player to list invites for.
	 * @return ArrayList<HomeInvite> containing list of invites.
	 */
	public final ArrayList<InviteEntry> listPlayerInvitesToMe(Player target) {
		return this.listPlayerInvitesToMe(target.getName());
	}

	/**
	 * Returns a list of home locations the specified player may visit.
	 * @param target Player to list invites for.
	 * @return ArrayList<HomeInvite> containing list of invites.
	 */
	abstract public ArrayList<InviteEntry> listPlayerInvitesToMe(String target);	
	
	/**
	 * Returns a list of invites the owner has given to others.
	 * @param owner Player to list invites for.
	 * @return ArrayList<HomeInvite> containing list of invites.
	 */
	public final ArrayList<InviteEntry> listPlayerInvitesToOthers(Player owner) {
		return this.listPlayerInvitesToOthers(owner.getName());
	}
	
	/**
	 * Returns a list of invites the owner has given to others.
	 * @param owner Player to list invites for.
	 * @return ArrayList<HomeInvite> containing list of invites.
	 */
	abstract public ArrayList<InviteEntry> listPlayerInvitesToOthers(String owner);	
	
	/**
	 * Imports the list of invites passed.
	 * @param invites List of InviteEntry objects to import.
	 * @param overwrite True to overwrite existing entries.
	 */
	abstract public void importInvites(ArrayList<InviteEntry> invites, boolean overwrite);
}
