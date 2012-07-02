package net.madmanmarkau.MultiHome.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map.Entry;

import net.madmanmarkau.MultiHome.Messaging;
import net.madmanmarkau.MultiHome.MultiHome;
import net.madmanmarkau.MultiHome.Settings;

public class WarmUpManagerMySQL extends WarmUpManager {
	private final String url; // Database URL to connect to.
	private final String user; // MySQL user to connect as.
	private final String password; // Password for MySQL user.

	private HashMap<String, WarmUpTask> warmupEntries = new HashMap<String, WarmUpTask>();
	
	public WarmUpManagerMySQL(MultiHome plugin) {
		super(plugin);

		// Save settings
		this.url = Settings.getDataStoreSettingString("sql", "url");
		this.user = Settings.getDataStoreSettingString("sql", "user");
		this.password = Settings.getDataStoreSettingString("sql", "pass");

		// Test connection
		try {
			Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			} else {
				connection.close();
			}
		} catch (SQLException e) {
			Messaging.logSevere("Failed to contact MySQL server: " + e.getMessage(), this.plugin);
			return;
		}
		
		loadWarmups();
	}
	
	@Override
	public void clearWarmups() {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			// Clear warmups in database
			statement = connection.prepareStatement("DELETE FROM `warmups`;");
			statement.execute();

			// Clear warmup tasks
			for (Entry<String, WarmUpTask> entry : this.warmupEntries.entrySet()) {
				entry.getValue().cancelWarmUp();
			}
			this.warmupEntries.clear();
		} catch (Exception e) {
			Messaging.logSevere("Failed to clear warmups: " + e.getMessage(), this.plugin);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {} // Eat errors
			}
		}
	}

	@Override
	public WarmUpEntry getWarmup(String player) {
		try {
			if (this.warmupEntries.containsKey(player.toLowerCase())) {
				return this.warmupEntries.get(player.toLowerCase()).getWarmup();
			}
		} catch (Exception e) {
			Messaging.logSevere("Failed to get warmup: " + e.getMessage(), this.plugin);
		}
		
		return null;
	}

	@Override
	public void addWarmup(WarmUpEntry warmup) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			// Remove warmup from database
			statement = connection.prepareStatement("DELETE FROM `warmups` WHERE LOWER(`player`) = LOWER(?);");
			statement.setString(1, warmup.getPlayer());
			statement.execute();
			statement.close();
			statement = null;

			// Remove warmup task
			if (this.warmupEntries.containsKey(warmup.getPlayer().toLowerCase())) {
				WarmUpTask task = this.warmupEntries.get(warmup.getPlayer().toLowerCase());
				task.cancelWarmUp();
				this.warmupEntries.remove(warmup.getPlayer().toLowerCase());
			}

			// Insert warmup into database
			statement = connection.prepareStatement("INSERT INTO `warmups` (`player`, `expiry`, `world`, `x`, `y`, `z`, `pitch`, `yaw`, `cost`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
			statement.setString(1, warmup.getPlayer());
			statement.setTimestamp(2, new Timestamp(warmup.getExpiry().getTime()));
			statement.setString(3, warmup.getWorld());
			statement.setDouble(4, warmup.getX());
			statement.setDouble(5, warmup.getY());
			statement.setDouble(6, warmup.getZ());
			statement.setFloat(7, warmup.getPitch());
			statement.setFloat(8, warmup.getYaw());
			statement.setDouble(9, warmup.getCost());
			statement.execute();
			statement.close();
			statement = null;

			// Create warmup task
			this.warmupEntries.put(warmup.getPlayer().toLowerCase(), new WarmUpTask(plugin, warmup));
		} catch (Exception e) {
			Messaging.logSevere("Failed to set warmup: " + e.getMessage(), this.plugin);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {} // Eat errors
			}
		}
	}

	@Override
	public void removeWarmup(String player) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			// Remove warmup from database
			statement = connection.prepareStatement("DELETE FROM `warmups` WHERE LOWER(`player`) = LOWER(?);");
			statement.setString(1, player);
			statement.execute();
			statement.close();
			statement = null;

			// Remove warmup task
			if (this.warmupEntries.containsKey(player.toLowerCase())) {
				this.warmupEntries.get(player.toLowerCase()).cancelWarmUp();
				this.warmupEntries.remove(player.toLowerCase());
			}
		} catch (Exception e) {
			Messaging.logSevere("Failed to remove warmup: " + e.getMessage(), this.plugin);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {} // Eat errors
			}
		}
	}
	
	@Override
	public void taskComplete(WarmUpEntry warmup) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			// Remove warmup from database
			statement = connection.prepareStatement("DELETE FROM `warmups` WHERE LOWER(`player`) = LOWER(?);");
			statement.setString(1, warmup.getPlayer());
			statement.execute();
			statement.close();
			statement = null;

			// Remove warmup task
			if (this.warmupEntries.containsKey(warmup.getPlayer().toLowerCase())) {
				this.warmupEntries.remove(warmup.getPlayer().toLowerCase());
			}
		} catch (Exception e) {
			Messaging.logSevere("Failed to complete warmup: " + e.getMessage(), this.plugin);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {} // Eat errors
			}
		}
	}
	
	/**
	 * Load the warmup list from database.
	 */
	private void loadWarmups() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT * FROM `warmups`;");
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				try {
					WarmUpEntry warmup = new WarmUpEntry(resultSet.getString("player"), 
							resultSet.getTimestamp("timeout"),
							resultSet.getString("world"), 
							resultSet.getDouble("x"),
							resultSet.getDouble("y"),
							resultSet.getDouble("z"),
							resultSet.getFloat("yaw"),
							resultSet.getFloat("pitch"),
							resultSet.getDouble("cost"));
					
					this.warmupEntries.put(resultSet.getString("player").toLowerCase(), new WarmUpTask(this.plugin, warmup));
				} catch (Exception ex) {}

			}
			
		} catch (Exception e) {
			Messaging.logSevere("Failed to load warmups from database: " + e.getMessage(), this.plugin);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {} // Eat errors
			}
		}
	}
}
