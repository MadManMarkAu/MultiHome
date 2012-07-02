package net.madmanmarkau.MultiHome;

import java.util.ArrayList;
import java.util.Date;

import net.madmanmarkau.MultiHome.Data.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiHomeCommands {
	public static void goDefaultHome(MultiHome plugin, Player player) {
		if (HomePermissions.has(player, "multihome.defaulthome.go")) {
			double amount = 0;
			
			//Check for economy first - and make sure the player either has permission for free homes or has enough money
			if (Settings.isEconomyEnabled() && !HomePermissions.has(player, "multihome.free.defaulthome.go")) {
				if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getHomeCost(player))) {
					Settings.sendMessageNotEnoughMoney(player, Settings.getHomeCost(player));
					return;
				} else {
					amount = Settings.getHomeCost(player);
				}
			}
			
			// Get user cooldown timer.
			CoolDownEntry cooldown = plugin.getCoolDownManager().getCooldown(player.getName());

			if (cooldown != null && !HomePermissions.has(player, "multihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getExpiry().getTime() - new Date().getTime()), 1000) / 1000);
				return;
			}

			int warmupTime = Settings.getSettingWarmup(player);
			HomeEntry homeEntry = plugin.getHomeManager().getHome(player, "");

			if (homeEntry != null) {
				if (warmupTime > 0 && !HomePermissions.has(player, "multihome.ignore.warmup")) {
					// Warpup required.
					WarmUpEntry warmup = new WarmUpEntry(player.getName(), Util.dateInFuture(warmupTime), homeEntry.getHomeLocation(plugin.getServer()), amount);
					plugin.getWarmUpManager().addWarmup(warmup);
					Settings.sendMessageWarmup(player, warmupTime);
				} else {
					// Can transfer instantly

					//Double Check the charge before teleporting the player
					if (!HomePermissions.has(player, "multihome.free.defaulthome.go") && amount != 0) {
						if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
							return;
						} else {
							Settings.sendMessageDeductForHome(player, amount);
						}
					}

					Util.teleportPlayer(player, homeEntry.getHomeLocation(plugin.getServer()), plugin);

					int cooldownTime = Settings.getSettingCooldown(player);
					if (cooldownTime > 0) plugin.getCoolDownManager().addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
				}
			} else {
				Settings.sendMessageNoDefaultHome(player);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to warp to default home location. Permission not granted.", plugin);
		}
	}

	public static void goNamedHome(MultiHome plugin, Player player, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.go")) {
			double amount = 0;
			
			// Get user cooldown timer.
			CoolDownEntry cooldown = plugin.getCoolDownManager().getCooldown(player.getName());

			if (cooldown != null && !HomePermissions.has(player, "multihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getExpiry().getTime() - new Date().getTime()), 1000) / 1000);
				return;
			}

			//Check for economy first - and make sure the player either has permission for free homes or has enough money
			if (Settings.isEconomyEnabled() && !HomePermissions.has(player, "multihome.free.namedhome.go")) {
				if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getNamedHomeCost(player))) {
					Settings.sendMessageNotEnoughMoney(player, Settings.getNamedHomeCost(player));
					return;
				} else {
					amount = Settings.getNamedHomeCost(player);
				}
			}
		
			int warmupTime = Settings.getSettingWarmup(player);
			HomeEntry homeEntry = plugin.getHomeManager().getHome(player, home);

			if (homeEntry != null) {
				if (warmupTime > 0 && !HomePermissions.has(player, "multihome.ignore.warmup")) {
					// Warpup required.
					WarmUpEntry warmup = new WarmUpEntry(player.getName(), Util.dateInFuture(warmupTime), homeEntry.getHomeLocation(plugin.getServer()), amount);
					plugin.getWarmUpManager().addWarmup(warmup);
					Settings.sendMessageWarmup(player, warmupTime);
				} else {
					// Can transfer instantly

					//Double Check the charge before teleporting the player
					if (!HomePermissions.has(player, "multihome.free.namedhome.go") && amount != 0) {
						if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
							return;
						} else {
							Settings.sendMessageDeductForHome(player, amount);
						}
					}
					
					Util.teleportPlayer(player, homeEntry.getHomeLocation(plugin.getServer()), plugin);

					int cooldownTime = Settings.getSettingCooldown(player);
					if (cooldownTime > 0) plugin.getCoolDownManager().addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
				}
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to warp to home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void goPlayerNamedHome(MultiHome plugin, Player player, String owner, String home) {
		if (HomePermissions.has(player, "multihome.othershome.go") || plugin.getInviteManager().getInvite(owner, home, player.getName()) != null) {
			double amount = 0;
			
			// Get user cooldown timer.
			CoolDownEntry cooldown = plugin.getCoolDownManager().getCooldown(player.getName());

			if (cooldown != null && !HomePermissions.has(player, "multihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getExpiry().getTime() - new Date().getTime()), 1000) / 1000);
				return;
			}

			//Check for economy first - and make sure the player either has permission for free homes or has enough money
			if (Settings.isEconomyEnabled() && !HomePermissions.has(player, "multihome.free.othershome.go")) {
				if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getOthersHomeCost(player))) {
					Settings.sendMessageNotEnoughMoney(player, Settings.getOthersHomeCost(player));
					return;
				} else {
					amount = Settings.getOthersHomeCost(player);
				}
			}

			int warmupTime = Settings.getSettingWarmup(player);

			if (plugin.getHomeManager().getUserExists(owner)) {
				HomeEntry homeEntry = plugin.getHomeManager().getHome(owner, home);

				if (homeEntry != null) {
					if (warmupTime > 0 && !HomePermissions.has(player, "multihome.ignore.warmup")) {
						// Warpup required.
						WarmUpEntry warmup = new WarmUpEntry(player.getName(), Util.dateInFuture(warmupTime), homeEntry.getHomeLocation(plugin.getServer()), amount);
						plugin.getWarmUpManager().addWarmup(warmup);
						Settings.sendMessageWarmup(player, warmupTime);
						Messaging.logInfo("Player " + player.getName() + " warped to player " + owner + "'s home location: " + home, plugin);
					} else {
						// Can transfer instantly
						
						//Double Check the charge before teleporting the player
						if (!HomePermissions.has(player, "multihome.free.othershome.go") && amount != 0) {
							if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
								return;
							} else {
								Settings.sendMessageDeductForHome(player, amount);
							}
						}

						Util.teleportPlayer(player, homeEntry.getHomeLocation(plugin.getServer()), plugin);

						int cooldownTime = Settings.getSettingCooldown(player);
						if (cooldownTime > 0) plugin.getCoolDownManager().addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
					}
				} else {
					Settings.sendMessageNoHome(player, owner + ":" + home);
				}
			} else {
				Settings.sendMessageNoPlayer(player, owner);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to warp to " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void setDefaultHome(MultiHome plugin, Player player) {
		if (HomePermissions.has(player, "multihome.defaulthome.set")) {
			int numHomes = plugin.getHomeManager().getUserHomeCount(player);
			int maxHomes = Settings.getSettingMaxHomes(player);
			double amount = 0;

			if (numHomes < maxHomes || maxHomes == -1 || plugin.getHomeManager().getHome(player, "") != null) {
				//Check for economy first - and make sure the player either has permission for free homes or has enough money
				if (Settings.isEconomyEnabled() && !HomePermissions.has(player, "multihome.free.defaulthome.set")) {
					if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getSetHomeCost(player))) {
						Settings.sendMessageNotEnoughMoney(player, Settings.getSetHomeCost(player));
						return;
					} else {
						amount = Settings.getSetHomeCost(player);
					}
				}

				//Double Check the charge before settings home
				if (!HomePermissions.has(player, "multihome.free.defaulthome.set") && amount != 0) {
					if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
						return;
					} else {
						Settings.sendMessageDeductForHome(player, amount);
					}
				}

				plugin.getHomeManager().addHome(player, "", player.getLocation());
				Settings.sendMessageDefaultHomeSet(player);
				Messaging.logInfo("Player " + player.getName() + " set defult home location", plugin);
			} else {
				Settings.sendMessageMaxHomes(player, numHomes, maxHomes);
				Messaging.logInfo("Player " + player.getName() + " tried to set default home location. Too many set already.", plugin);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to set default home location. Permission not granted.", plugin);
		}
	}

	public static void setNamedHome(MultiHome plugin, Player player, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.set")) {
			int numHomes = plugin.getHomeManager().getUserHomeCount(player);
			int maxHomes = Settings.getSettingMaxHomes(player);
			double amount = 0;

			if (numHomes < maxHomes || maxHomes == -1 || plugin.getHomeManager().getHome(player, home) != null) {
				//Check for economy first - and make sure the player either has permission for free homes or has enough money
				if (Settings.isEconomyEnabled() && !HomePermissions.has(player, "multihome.free.namedhome.set")) {
					if (!MultiHomeEconManager.hasEnough(player.getName(), Settings.getSetNamedHomeCost(player))) {
						Settings.sendMessageNotEnoughMoney(player, Settings.getSetNamedHomeCost(player));
						return;
					} else {
						amount = Settings.getSetNamedHomeCost(player);
					}
				}

				//Double Check the charge before settings home
				if (!HomePermissions.has(player, "multihome.free.namedhome.set") && amount != 0) {
					if (!MultiHomeEconManager.chargePlayer(player.getName(), amount)) {
						return;
					} else {
						Settings.sendMessageDeductForHome(player, amount);
					}
				}

				plugin.getHomeManager().addHome(player, home, player.getLocation());
				Settings.sendMessageHomeSet(player, home);
				Messaging.logInfo("Player " + player.getName() + " set home location [" + home + "]", plugin);
			} else {
				Settings.sendMessageMaxHomes(player, numHomes, maxHomes);
				Messaging.logInfo("Player " + player.getName() + " tried to set home location [" + home + "]. Too many set already.", plugin);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to set home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void setPlayerNamedHome(MultiHome plugin, Player player, String owner, String home) {
		if (HomePermissions.has(player, "multihome.othershome.set")) {
			plugin.getHomeManager().addHome(owner, home, player.getLocation());
			Settings.sendMessageHomeSet(player, owner + ":" + home);
			Messaging.logInfo("Player " + player.getName() + " set player " + owner + "'s home location [" + home + "]", plugin);
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to set player " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void deleteDefaultHome(MultiHome plugin, Player player) {
		if (plugin.getHomeManager().getHome(player, "") != null) {
			Settings.sendMessageCannotDeleteDefaultHome(player);
		} else {
			Settings.sendMessageNoDefaultHome(player);
		}
		Messaging.logInfo("Player " + player.getName() + " tried to delete deafult home location. Cannot do.", plugin);
	}

	public static void deleteNamedHome(MultiHome plugin, Player player, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.delete")) {
			if (plugin.getHomeManager().getHome(player, home) != null) {
				plugin.getHomeManager().removeHome(player, home);
				Settings.sendMessageHomeDeleted(player, home);
				Messaging.logInfo("Player " + player.getName() + " deleted home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to delete home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void deletePlayerNamedHome(MultiHome plugin, Player player, String owner, String home) {
		if (HomePermissions.has(player, "multihome.othershome.delete")) {
			if (plugin.getHomeManager().getHome(owner, home) != null) {
				plugin.getHomeManager().removeHome(owner, home);
				Settings.sendMessageHomeDeleted(player, owner + ":" + home);
				Messaging.logInfo("Player " + player.getName() + " deleted " + owner + "'s home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to delete " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void listHomes(MultiHome plugin, Player player) {
		if (HomePermissions.has(player, "multihome.namedhome.list")) {
			ArrayList<HomeEntry> homes = plugin.getHomeManager().listUserHomes(player);

			Settings.sendMessageHomeList(player, Util.compileHomeList(homes));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list home locations. Permission not granted.", plugin);
		}
	}

	public static void listPlayerHomes(MultiHome plugin, Player player, String owner) {
		if (HomePermissions.has(player, "multihome.othershome.list")) {
			ArrayList<HomeEntry> homes = plugin.getHomeManager().listUserHomes(owner);

			Settings.sendMessageOthersHomeList(player, owner, Util.compileHomeList(homes));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list " + owner + "'s home locations. Permission not granted.", plugin);
		}
	}

	public static void listPlayerHomesConsole(MultiHome plugin, CommandSender sender, String owner) {
		ArrayList<HomeEntry> homes = plugin.getHomeManager().listUserHomes(owner);

		Settings.sendMessageOthersHomeList(sender, owner, Util.compileHomeList(homes));
	}

	public static void inviteDefaultHome(MultiHome plugin, Player player, String target) {
		if (HomePermissions.has(player, "multihome.defaulthome.invite")) {
			if (plugin.getHomeManager().getHome(player, "") != null) {
				plugin.getInviteManager().addInvite(player.getName(), "", target);
				Settings.sendMessageInviteOwnerHome(player, target, "");

				Player targetPlayer = Util.getExactPlayer(target, plugin);
				if (targetPlayer != null) {
					Settings.sendMessageInviteTargetHome(targetPlayer, player.getName(), "");
				}

				Messaging.logInfo("Player " + player.getName() + " invited " + target + " to their default home.", plugin);
			} else {
				Settings.sendMessageNoDefaultHome(player);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to invite " + target + " to their default home. Permission not granted.", plugin);
		}
	}

	public static void inviteNamedHome(MultiHome plugin, Player player, String target, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.invite")) {
			if (plugin.getHomeManager().getHome(player, home) != null) {
				plugin.getInviteManager().addInvite(player.getName(), home, target);
				Settings.sendMessageInviteOwnerHome(player, target, home);

				Player targetPlayer = Util.getExactPlayer(target, plugin);
				if (targetPlayer != null) {
					Settings.sendMessageInviteTargetHome(targetPlayer, player.getName(), home);
				}

				Messaging.logInfo("Player " + player.getName() + " invited " + target + " to their home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to invite " + target + " to their home location [" + home + "]. Permission not granted.", plugin);
		}
	}


	public static void inviteDefaultTimedHome(MultiHome plugin, Player player, String target, int time) {
		if (HomePermissions.has(player, "multihome.defaulthome.invitetimed")) {
			if (plugin.getHomeManager().getHome(player, "") != null) {
				plugin.getInviteManager().addInvite(player.getName(), "", target, Util.dateInFuture(time));
				Settings.sendMessageInviteTimedOwnerHome(player, target, "", time);

				Player targetPlayer = Util.getExactPlayer(target, plugin);
				if (targetPlayer != null) {
					Settings.sendMessageInviteTimedTargetHome(targetPlayer, player.getName(), "", time);
				}

				Messaging.logInfo("Player " + player.getName() + " invited " + target + " to their default home for " + time + " seconds.", plugin);
			} else {
				Settings.sendMessageNoDefaultHome(player);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to invite " + target + " to their default home for " + time + " seconds. Permission not granted.", plugin);
		}
	}

	public static void inviteNamedTimedHome(MultiHome plugin, Player player, String target, int time, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.invitetimed")) {
			if (plugin.getHomeManager().getHome(player, home) != null) {
				plugin.getInviteManager().addInvite(player.getName(), home, target, Util.dateInFuture(time));
				Settings.sendMessageInviteOwnerHome(player, target, home);

				Player targetPlayer = Util.getExactPlayer(target, plugin);
				if (targetPlayer != null) {
					Settings.sendMessageInviteTimedTargetHome(targetPlayer, player.getName(), home, time);
				}

				Messaging.logInfo("Player " + player.getName() + " invited " + target + " to their home location [" + home + "] for " + time + " seconds.", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to invite " + target + " to their home location [" + home + "] for " + time + " seconds. Permission not granted.", plugin);
		}
	}

	public static void uninviteDefaultHome(MultiHome plugin, Player player, String target) {
		if (HomePermissions.has(player, "multihome.defaulthome.uninvite")) {
			if (plugin.getInviteManager().getInvite(player.getName(), "", target) != null) {
				plugin.getInviteManager().removeInvite(player.getName(), "", target);

				Settings.sendMessageUninviteOwnerHome(player, target, "");

				Player targetPlayer = Util.getExactPlayer(target, plugin);
				if (targetPlayer != null) {
					Settings.sendMessageUninviteTargetHome(targetPlayer, player.getName(), "[Default]");
				}

				Messaging.logInfo("Player " + player.getName() + " removed invite for " + target + " to their default home.", plugin);
			} else {
				Settings.sendMessageNoDefaultHome(player);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to remove invite for " + target + " to their default home. Permission not granted.", plugin);
		}
	}

	public static void uninviteNamedHome(MultiHome plugin, Player player, String target, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.uninvite")) {
			if (plugin.getInviteManager().getInvite(player.getName(), home, target) != null) {
				plugin.getInviteManager().removeInvite(player.getName(), home, target);
				Settings.sendMessageUninviteOwnerHome(player, target, home);

				Player targetPlayer = Util.getExactPlayer(target, plugin);
				if (targetPlayer != null) {
					Settings.sendMessageUninviteTargetHome(targetPlayer, player.getName(), home);
				}

				Messaging.logInfo("Player " + player.getName() + " removed invite for " + target + " to their home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to remove invite for " + target + " to their home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public static void listInvitesToMe(MultiHome plugin, Player player) {
		if (HomePermissions.has(player, "multihome.listinvites.tome")) {
			ArrayList<InviteEntry> invites = plugin.getInviteManager().listPlayerInvitesToMe(player.getName());

			Settings.sendMessageInviteListToMe(player, player.getName(), Util.compileInviteListForMe(player.getName(), invites));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list invitations open to them. Permission not granted.", plugin);
		}
	}

	public static void listInvitesToOthers(MultiHome plugin, Player player) {
		if (HomePermissions.has(player, "multihome.listinvites.toothers")) {
			ArrayList<InviteEntry> invites = plugin.getInviteManager().listPlayerInvitesToOthers(player.getName());

			Settings.sendMessageInviteListToOthers(player, player.getName(), Util.compileInviteListForOthers(invites));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list invitations they've given. Permission not granted.", plugin);
		}
	}
}
