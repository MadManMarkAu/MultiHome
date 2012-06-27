package net.madmanmarkau.MultiHome;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.madmanmarkau.MultiHome.Data.HomeEntry;
import net.madmanmarkau.MultiHome.Data.InviteEntry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class ImportData {

	public static ArrayList<HomeEntry> importHomesFromMultiHomeFile(MultiHome plugin) {
		File homesFile = new File(plugin.getDataFolder(), "homes.txt");
		ArrayList<HomeEntry> homes = new ArrayList<HomeEntry>();
		
		if (homesFile.exists()) {
			try {
				FileReader fstream = new FileReader(homesFile);
				BufferedReader reader = new BufferedReader(fstream);
	
				String line = reader.readLine().trim();
	
				while (line != null) {
					if (!line.startsWith("#") && line.length() > 0) {
						HomeEntry thisHome;
						
						String[] values = line.split(";");
						double X = 0, Y = 0, Z = 0;
						float pitch = 0, yaw = 0;
						String world = "";
						String name = "";
						String player = "";

						try {
							if (values.length == 7) {
								player = values[0];
								X = Double.parseDouble(values[1]);
								Y = Double.parseDouble(values[2]);
								Z = Double.parseDouble(values[3]);
								pitch = Float.parseFloat(values[4]);
								yaw = Float.parseFloat(values[5]);

								world = values[6];
								name = "";
							} else if (values.length == 8) {
								player = values[0];
								X = Double.parseDouble(values[1]);
								Y = Double.parseDouble(values[2]);
								Z = Double.parseDouble(values[3]);
								pitch = Float.parseFloat(values[4]);
								yaw = Float.parseFloat(values[5]);

								world = values[6];
								name = values[7];
							}
						} catch (Exception e) {
							// This entry failed. Ignore and continue.
						}

						if (values.length == 7 || values.length == 8) {
							boolean save = true;

							thisHome = new HomeEntry(player.toLowerCase(), name.toLowerCase(), world, X, Y, Z, pitch, yaw);

							for (HomeEntry home : homes) {
								if (home.getHomeName().compareToIgnoreCase(thisHome.getHomeName()) == 0 && home.getOwnerName().compareToIgnoreCase(thisHome.getOwnerName()) == 0) {
									save = false;
								}
							}
							
							if (save) {
								homes.add(thisHome);
							}
						}
					}
	
					line = reader.readLine();
				}
	
				reader.close();
			} catch (Exception e) {
				Messaging.logSevere("Could not read the homes file.", plugin);
				e.printStackTrace();
				return new ArrayList<HomeEntry>();
			}
		}
		
		return homes;
	}

	public static ArrayList<HomeEntry> importHomesFromMultiHomeMySQL(MultiHome plugin) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<HomeEntry> homes = new ArrayList<HomeEntry> ();

		try {
			connection = DriverManager.getConnection(Settings.getDataStoreSettingString("sql", "url"), 
					Settings.getDataStoreSettingString("sql", "user"),
					Settings.getDataStoreSettingString("sql", "pass"));
			if (!connection.isValid(100)) {
				throw new SQLException();
			}

			statement = connection.prepareStatement("SELECT * FROM `homes`;");
			resultSet = statement.executeQuery();
			if (resultSet.first()) {
				do {
					homes.add(new HomeEntry(resultSet.getString("owner").toLowerCase(), 
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
			// Ignore errors
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

		return homes;
	}

	public static ArrayList<HomeEntry> importHomesFromEssentials(MultiHome plugin) {
		File essentialsDir = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata");
		ArrayList<HomeEntry> homes = new ArrayList<HomeEntry> ();

		if (essentialsDir.exists()) {
			File[] userFiles = essentialsDir.listFiles();
			List<?> homeLocation;
			
			for (File userFile : userFiles) {
				try {
					String user = userFile.getName().replaceAll("\\.yml", "");
					YamlConfiguration userConfig = new YamlConfiguration();
					userConfig.load(userFile);
	
					// Load old Essentials home format.
					homeLocation = userConfig.getList("home");
					if (homeLocation != null && !homeLocation.isEmpty()) {
						try {
							homes.add(new HomeEntry(user, 
									"", 
									((String) homeLocation.get(5)).toLowerCase(), 
									(Double) homeLocation.get(0), 
									(Double) homeLocation.get(1), 
									(Double) homeLocation.get(2), 
									(Float) homeLocation.get(4), 
									(Float) homeLocation.get(3)));
						} catch (Exception e) {
							// This entry failed. Ignore and continue.
						}
					}
	
					// Load new Essentials home format.
					ConfigurationSection homeWorlds = userConfig.getConfigurationSection("home.worlds");
					if (homeWorlds != null) {
						for (String homeWorld : homeWorlds.getKeys(false)) {
							ConfigurationSection homeData = userConfig.getConfigurationSection("home.worlds." + homeWorld);
							
							if (homeData != null) {
								try {
									homes.add(new HomeEntry(user, 
											"", 
											(homeData.getString("world")).toLowerCase(), 
											homeData.getDouble("x", 0), homeData.getDouble("y", 0), homeData.getDouble("z", 0),
											(float) homeData.getDouble("pitch", 0), (float) homeData.getDouble("yaw", 0)));
								} catch (Exception e) {
									// This entry failed. Ignore and continue.
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		}
		
		return homes;
	}
	
	public static ArrayList<HomeEntry> importHomesFromMultipleHomes(MultiHome plugin) {
		File multipleHomesDir = new File("plugins" + File.separator + "MultipleHomes" + File.separator + "Homes");
		ArrayList<HomeEntry> homes = new ArrayList<HomeEntry> ();

		if (multipleHomesDir.exists()) {
			File[] userFiles = multipleHomesDir.listFiles();
			
			for (File userFile : userFiles) {
				String name = userFile.getName().replaceAll("home\\_", "").replaceAll("\\.txt", "");
				
				try {
					FileReader fstream = new FileReader(userFile);
					BufferedReader reader = new BufferedReader(fstream);

					String line = reader.readLine().trim();

					while (line != null) {
						if (line.startsWith("~")) {
							String[] split = line.split(":");
							
							if (split.length == 2) {
								String user = split[0].substring(1);
								String[] values = split[1].split("_");
								double X = 0, Y = 0, Z = 0;
								float pitch = 0, yaw = 0;
	
								try {
									X = Double.parseDouble(values[0]);
									Y = Double.parseDouble(values[1]);
									Z = Double.parseDouble(values[2]);
									pitch = Float.parseFloat(values[4]);
									yaw = Float.parseFloat(values[3]);

									if (name == null || name.length() == 0 || name.compareTo("0") == 0) {
										homes.add(new HomeEntry(user, "", values[5].toLowerCase(), X, Y, Z, pitch, yaw));
									} else {
										homes.add(new HomeEntry(user, name, values[5].toLowerCase(), X, Y, Z, pitch, yaw));
									}
								} catch (Exception e) {
									// This entry failed. Ignore and continue.
								}
							}
						}

						line = reader.readLine();
					}

					reader.close();
				} catch (Exception e) {
					// Eat errors
				}
			}
		}
		
		return homes;
	}
	
	public static ArrayList<HomeEntry> importHomesFromMyHome(MultiHome plugin) {
		File myHomeFile = new File("plugins" + File.separator + "MyHome" + File.separator + "homes.db");
		ArrayList<HomeEntry> homes = new ArrayList<HomeEntry> ();

		if (myHomeFile.exists()) {
			// connect to the MyHomes database.
			Connection conn;

	        try {
	            Class.forName("org.sqlite.JDBC");
	            conn = DriverManager.getConnection("jdbc:sqlite:" + myHomeFile.getAbsolutePath());
	            conn.setAutoCommit(false);
	        } catch (SQLException ex) {
				return homes;
	        } catch (ClassNotFoundException ex) {
				return homes;
	        }

	        try {
	            Statement statement = null;
	            ResultSet set = null;

	            statement = conn.createStatement();
	            set = statement.executeQuery("SELECT * FROM homeTable");
	            while (set.next()) {
	                String name = set.getString("name");
	                String world = set.getString("world");
	                double X = set.getDouble("x");
	                int Y = set.getInt("y");
	                double Z = set.getDouble("z");
	                int yaw = set.getInt("yaw");
	                int pitch = set.getInt("pitch");

					homes.add(new HomeEntry(name, "", world.toLowerCase(), X, Y, Z, pitch, yaw));
	            }
	            
	            set.close();
	            statement.close();
	        } catch (SQLException e) {
	        }
	        
            try {
                conn.close();
            } catch (SQLException ex) {
            }
		}
		
		return homes;
	}
	
	public static ArrayList<InviteEntry> importInvitesFromMyHome(MultiHome plugin) {
		File myHomeFile = new File("plugins" + File.separator + "MyHome" + File.separator + "homes.db");
		ArrayList<InviteEntry> invites = new ArrayList<InviteEntry>();
		
		if (myHomeFile.exists()) {
			// connect to the MyHomes database.
			Connection conn;

	        try {
	            Class.forName("org.sqlite.JDBC");
	            conn = DriverManager.getConnection("jdbc:sqlite:" + myHomeFile.getAbsolutePath());
	            conn.setAutoCommit(false);
	        } catch (SQLException ex) {
				return invites;
	        } catch (ClassNotFoundException ex) {
				return invites;
	        }

	        try {
	            Statement statement = null;
	            ResultSet set = null;

	            statement = conn.createStatement();
	            set = statement.executeQuery("SELECT * FROM homeTable");
	            while (set.next()) {
	                String owner = set.getString("name").toLowerCase();
	                boolean publicAll = set.getBoolean("publicAll");
	                String permissions = set.getString("permissions");
	                String reason = set.getString("welcomeMessage");

	                if (publicAll) {
	                	invites.add(new InviteEntry(owner, "", "*", reason));
	                }
	                
	                String users[] = permissions.split(",");
	                for (String thisUser : users) {
	                    if (thisUser.length() == 0) {
	                        continue;
	                    }
	                	invites.add(new InviteEntry(owner, "", thisUser.trim().toLowerCase(), reason));
	                }
	            }
	            
	            set.close();
	            statement.close();
	            
	        } catch (SQLException e) {
	        }
	        
            try {
                conn.close();
            } catch (SQLException ex) {
            }
		}
		
		return invites;
	}
}
