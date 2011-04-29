package net.madmanmarkau.MultiHome;

import java.util.Date;

public class HomeInvite {
	private String inviteSource;
	private String inviteHome;
	private String inviteTarget;
	private Date inviteExpires;
	private String inviteReason;
	
	public HomeInvite(String inviteSource, String inviteHome, String inviteTarget) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = null;
		this.inviteReason = "";
	}
	
	public HomeInvite(String inviteSource, String inviteHome, String inviteTarget, Date inviteExpires) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = inviteExpires;
		this.inviteReason = "";
	}
	
	public HomeInvite(String inviteSource, String inviteHome, String inviteTarget, String inviteReason) {
		this.inviteSource = inviteSource;
		this.inviteHome = inviteHome;
		this.inviteTarget = inviteTarget;
		this.inviteExpires = null;
		this.inviteReason = inviteReason;
	}
	
	public HomeInvite(String inviteSource, String inviteHome, String inviteTarget, Date inviteExpires, String inviteReason) {
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
		return inviteSource;
	}

	public void setInviteHome(String inviteHome) {
		this.inviteHome = inviteHome;
	}

	public String getInviteHome() {
		return inviteHome;
	}

	public void setInviteTarget(String inviteTarget) {
		this.inviteTarget = inviteTarget;
	}
	
	public String getInviteTarget() {
		return inviteTarget;
	}

	public void setInviteExpires(Date inviteExpires) {
		this.inviteExpires = inviteExpires;
	}

	public Date getInviteExpires() {
		return inviteExpires;
	}

	public void setInviteReason(String inviteReason) {
		this.inviteReason = inviteReason;
	}

	public String getInviteReason() {
		return inviteReason;
	}
	
}
