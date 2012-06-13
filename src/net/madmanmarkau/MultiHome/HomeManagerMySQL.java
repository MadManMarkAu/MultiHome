package net.madmanmarkau.MultiHome;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;


public class HomeManagerMySQL extends HomeManager {
	private final String url; // Database URL to connect to.
	private final String user; // MySQL user to connect as.
	private final String password; // Password for MySQL user.

	public HomeManagerMySQL(MultiHome plugin) {
		super(plugin);

		// Save settings
		this.url = Settings.getDataStoreSettingString("sql", "url");
		this.user = Settings.getDataStoreSettingString("sql", "user");
		this.password = Settings.getDataStoreSettingString("sql", "pass");

		// Test connection
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			} else {
				connection.close();
			}
		} catch (SQLException e) {
			Messaging.logSevere("Failed to contact MySQL server!", this.plugin);
			e.printStackTrace();
		}
	}

	@Override
	public void clearHomes() {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("DELETE FROM `homes`;");
			statement.execute();
		} catch (SQLException e) {
			Messaging.logSevere("Failed to clear home locations!", this.plugin);
			e.printStackTrace();
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
	public Location getHome(String player, String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT * FROM `homes` WHERE `owner` = ? AND `home` = ?;");
			statement.setString(1, player.toLowerCase());
			statement.setString(2, name.toLowerCase());
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				//String ownerName, String homeName, String world, double X, double Y, double Z, float pitch, float yaw
				
				World world;
				
				try {
					world = plugin.getServer().getWorld(resultSet.getString("world"));
					
					if (world != null) {
						return new Location(world, 
											resultSet.getDouble("x"), 
											resultSet.getDouble("y"), 
											resultSet.getDouble("z"), 
											resultSet.getFloat("yaw"), 
											resultSet.getFloat("pitch"));
					}
				} catch (Exception ex) {}

			}
			
		} catch (SQLException e) {
			Messaging.logSevere("Failed to get home location!", this.plugin);
			e.printStackTrace();
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

		return null;
	}

	@Override
	public void addHome(String player, String name, Location location) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		boolean exists = false;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT COUNT(*) FROM `homes` WHERE `owner` = ? AND `home` = ?;");
			statement.setString(1, player.toLowerCase());
			statement.setString(2, name.toLowerCase());
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				exists = resultSet.getInt(1) > 0;
			}

			if (exists) {
				statement = connection.prepareStatement("UPDATE `homes` SET `world` = ?, `x` = ?, `y` = ?, `z` = ?, `pitch` = ?, `yaw` = ? WHERE `owner` = ? AND `home` = ?");

				statement.setString(1, location.getWorld().getName());
				statement.setDouble(2, location.getX());
				statement.setDouble(3, location.getY());
				statement.setDouble(4, location.getZ());
				statement.setFloat(5, location.getPitch());
				statement.setFloat(6, location.getYaw());
				statement.setString(7, player.toLowerCase());
				statement.setString(8, name.toLowerCase());
				statement.execute();
			} else {
				statement = connection.prepareStatement("INSERT INTO `homes`(`owner`, `home`, `world`, `x`, `y`, `z`, `pitch`, `yaw`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

				statement.setString(1, player.toLowerCase());
				statement.setString(2, name.toLowerCase());
				statement.setString(3, location.getWorld().getName());
				statement.setDouble(4, location.getX());
				statement.setDouble(5, location.getY());
				statement.setDouble(6, location.getZ());
				statement.setFloat(7, location.getPitch());
				statement.setFloat(8, location.getYaw());
				statement.execute();
			}

		} catch (SQLException e) {
			Messaging.logSevere("Failed to add home location!", this.plugin);
			e.printStackTrace();
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
	public void removeHome(String player, String name) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("DELETE FROM `homes` WHERE `owner` = ? AND `home` = ?;");
			statement.setString(1, player.toLowerCase());
			statement.setString(2, name.toLowerCase());
			statement.execute();
		} catch (SQLException e) {
			Messaging.logSevere("Failed to remove home location!", this.plugin);
			e.printStackTrace();
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
	public boolean getUserExists(String player) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT COUNT(`id`) FROM `homes` WHERE `owner` = ?;");
			statement.setString(1, player.toLowerCase());
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				return resultSet.getInt(1) > 0;
			}
		} catch (SQLException e) {
			Messaging.logSevere("Failed to determine if user exists!", this.plugin);
			e.printStackTrace();
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
		
		return false;
	}

	@Override
	public int getUserHomeCount(String player) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT COUNT(`id`) FROM `homes` WHERE `owner` = ?;");
			statement.setString(1, player.toLowerCase());
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				return resultSet.getInt(1);
			}
		} catch (SQLException e) {
			Messaging.logSevere("Failed to determine if user exists!", this.plugin);
			e.printStackTrace();
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
		
		return 0;
	}

	@Override
	public ArrayList<HomeEntry> listUserHomes(String player) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<HomeEntry> output = new ArrayList<HomeEntry> ();

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT * FROM `homes` WHERE `owner` = ?;");
			statement.setString(1, player.toLowerCase());
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				do {
					output.add(new HomeEntry(resultSet.getString("owner").toLowerCase(), 
							resultSet.getString("home").toLowerCase(), 
							resultSet.getString("world").toLowerCase(), 
							resultSet.getDouble("x"), 
							resultSet.getDouble("y"), 
							resultSet.getDouble("z"), 
							resultSet.getFloat("yaw"), 
							resultSet.getFloat("pitch")));
				} while (resultSet.next());
			}
			
		} catch (SQLException e) {
			Messaging.logSevere("Failed to get all home locations for player!", this.plugin);
			e.printStackTrace();
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

		return output;
	}

	@Override
	public void importHomes(ArrayList<HomeEntry> homes, boolean overwrite) {
		Connection connection = null;
		PreparedStatement statementExists = null;
		PreparedStatement statementInsert = null;
		PreparedStatement statementUpdate = null;
		ResultSet resultSet = null;
		boolean recordExists;

		try {
			connection = DriverManager.getConnection(url, user, password);
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statementExists = connection.prepareStatement("SELECT COUNT(`id`) FROM `homes` WHERE `owner` = ? AND `home` = ?;");
			statementInsert = connection.prepareStatement("INSERT INTO `homes`(`owner`, `home`, `world`, `x`, `y`, `z`, `pitch`, `yaw`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
			statementUpdate = connection.prepareStatement("UPDATE `homes` SET `owner` = ?, `home` = ?, `world` = ?, `x` = ?, `y` = ?, `z` = ?, `pitch` = ?, `yaw` = ? WHERE `owner` = ? AND `home` = ?;");
		
			for (HomeEntry thisEntry : homes) {
				// Determine if entry exists.
				recordExists = false;
				statementExists.setString(0,  thisEntry.getOwnerName().toLowerCase());
				statementExists.setString(1,  thisEntry.getHomeName().toLowerCase());
				resultSet = statementExists.executeQuery();
				if (resultSet.first()) {
					recordExists = resultSet.getInt(1) > 0;
				}
				resultSet.close();
				resultSet = null;
				
				// Save the entry, if required.
				if (recordExists) {
					if (overwrite) {
						statementUpdate.setString(0, thisEntry.getOwnerName().toLowerCase());
						statementUpdate.setString(1, thisEntry.getHomeName().toLowerCase());
						statementUpdate.setString(2, thisEntry.getWorld());
						statementUpdate.setDouble(3, thisEntry.getX());
						statementUpdate.setDouble(4, thisEntry.getY());
						statementUpdate.setDouble(5, thisEntry.getZ());
						statementUpdate.setFloat(6, thisEntry.getPitch());
						statementUpdate.setFloat(7, thisEntry.getYaw());
						statementUpdate.setString(8, thisEntry.getOwnerName().toLowerCase());
						statementUpdate.setString(9, thisEntry.getHomeName().toLowerCase());
						statementUpdate.execute();
					}
				} else {
					statementInsert.setString(0, thisEntry.getOwnerName().toLowerCase());
					statementInsert.setString(1, thisEntry.getHomeName().toLowerCase());
					statementInsert.setString(2, thisEntry.getWorld());
					statementInsert.setDouble(3, thisEntry.getX());
					statementInsert.setDouble(4, thisEntry.getY());
					statementInsert.setDouble(5, thisEntry.getZ());
					statementInsert.setFloat(6, thisEntry.getPitch());
					statementInsert.setFloat(7, thisEntry.getYaw());
					statementInsert.execute();
				}
			}

		} catch (SQLException e) {
			Messaging.logSevere("Failed to import home locations!", this.plugin);
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (statementExists != null) {
				try {
					statementExists.close();
				} catch (SQLException ex) {} // Eat errors
			}

			if (statementInsert != null) {
				try {
					statementInsert.close();
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
