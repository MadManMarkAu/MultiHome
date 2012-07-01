package net.madmanmarkau.MultiHome.Data;

import java.util.Date;

public class CoolDownEntry {
	private String player;
	private Date expiry;
	
	public CoolDownEntry(String player, Date expiry) {
		this.player = player;
		this.expiry = expiry;
	}
	
	public String getPlayer() {
		return player;
	}

	public void setPlayer(String ownerName) {
		this.player = ownerName;
	}

	public Date getExpiry() {
		return this.expiry;
	}
	
	public void setExpires(Date expires) {
		this.expiry = expires;
	}

}
