package net.madmanmarkau.MultiHome.Data;

import java.util.Date;

public class InviteEntry {
	private String inviteSource;
	private String inviteHome;
	private String inviteTarget;
	private Date inviteExpires;
	private String inviteReason;
	
	public InviteEntry(String inviteSource, String inviteHome, String inviteTarget) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = null;
		this.inviteReason = "";
	}
	
	public InviteEntry(String inviteSource, String inviteHome, String inviteTarget, Date inviteExpires) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = inviteExpires;
		this.inviteReason = "";
	}
	
	public InviteEntry(String inviteSource, String inviteHome, String inviteTarget, String inviteReason) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = null;
		this.inviteReason = inviteReason;
	}
	
	public InviteEntry(String inviteSource, String inviteHome, String inviteTarget, Date inviteExpires, String inviteReason) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = inviteExpires;
		this.inviteReason = inviteReason;
	}
	
	public void setInviteSource(String inviteSource) {
		this.inviteSource = inviteSource;
	}

	public String getInviteSource() {
		return this.inviteSource;
	}

	public void setInviteHome(String inviteHome) {
		this.inviteHome = inviteHome;
	}

	public String getInviteHome() {
		return this.inviteHome;
	}

	public void setInviteTarget(String inviteTarget) {
		this.inviteTarget = inviteTarget;
	}
	
	public String getInviteTarget() {
		return this.inviteTarget;
	}

	public void setInviteExpires(Date inviteExpires) {
		this.inviteExpires = inviteExpires;
	}

	public Date getInviteExpires() {
		return this.inviteExpires;
	}

	public void setInviteReason(String inviteReason) {
		this.inviteReason = inviteReason;
	}

	public String getInviteReason() {
		return this.inviteReason;
	}
	
}
