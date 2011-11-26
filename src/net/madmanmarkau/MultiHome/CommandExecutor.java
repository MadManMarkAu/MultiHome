package net.madmanmarkau.MultiHome;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor {
	MultiHome plugin;

	public CommandExecutor(MultiHome plugin) {
		this.plugin = plugin;
	}

	public void goDefaultHome(Player player) {
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
			Date cooldown = plugin.cooldowns.getCooldown(player.getName());

			if (cooldown != null && !HomePermissions.has(player, "multihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getTime() - new Date().getTime()), 1000) / 1000);
				return;
			}

			int warmupTime = Settings.getSettingWarmup(player);
			Location teleport = plugin.homes.getHome(player, "");

			if (teleport != null) {
				if (warmupTime > 0 && !HomePermissions.has(player, "multihome.ignore.warmup")) {
					// Warpup required.
					HomeWarmUp warmup = new HomeWarmUp(this.plugin, player, Util.dateInFuture(warmupTime), teleport, amount);
					plugin.warmups.addWarmup(player.getName(), warmup);
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

					Util.teleportPlayer(player, teleport, plugin);

					int cooldownTime = Settings.getSettingCooldown(player);
					if (cooldownTime > 0) plugin.cooldowns.addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
				}
			} else {
				Settings.sendMessageNoDefaultHome(player);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to warp to default home location. Permission not granted.", plugin);
		}
	}

	public void goNamedHome(Player player, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.go")) {
			double amount = 0;
			
			// Get user cooldown timer.
			Date cooldown = plugin.cooldowns.getCooldown(player.getName());

			if (cooldown != null && !HomePermissions.has(player, "multihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getTime() - new Date().getTime()), 1000) / 1000);
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
			Location teleport = plugin.homes.getHome(player, home);

			if (teleport != null) {
				if (warmupTime > 0 && !HomePermissions.has(player, "multihome.ignore.warmup")) {
					// Warpup required.
					HomeWarmUp warmup = new HomeWarmUp(this.plugin, player, Util.dateInFuture(warmupTime), teleport, amount);
					plugin.warmups.addWarmup(player.getName(), warmup);
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
					
					Util.teleportPlayer(player, teleport, plugin);

					int cooldownTime = Settings.getSettingCooldown(player);
					if (cooldownTime > 0) plugin.cooldowns.addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
				}
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to warp to home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public void goPlayerNamedHome(Player player, String owner, String home) {
		if (HomePermissions.has(player, "multihome.othershome.go") || plugin.invites.getInvite(owner, home, player.getName()) != null) {
			double amount = 0;
			
			// Get user cooldown timer.
			Date cooldown = plugin.cooldowns.getCooldown(player.getName());

			if (cooldown != null && !HomePermissions.has(player, "multihome.ignore.cooldown")) {
				Settings.sendMessageCooldown(player, Math.max((int) (cooldown.getTime() - new Date().getTime()), 1000) / 1000);
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

			if (plugin.homes.getUserExists(owner)) {
				Location teleport = plugin.homes.getHome(owner, home);

				if (teleport != null) {
					if (warmupTime > 0 && !HomePermissions.has(player, "multihome.ignore.warmup")) {
						// Warpup required.
						HomeWarmUp warmup = new HomeWarmUp(this.plugin, player, Util.dateInFuture(warmupTime), teleport, amount);
						plugin.warmups.addWarmup(player.getName(), warmup);
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

						Util.teleportPlayer(player, teleport, plugin);

						int cooldownTime = Settings.getSettingCooldown(player);
						if (cooldownTime > 0) plugin.cooldowns.addCooldown(player.getName(), Util.dateInFuture(cooldownTime));
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

	public void setDefaultHome(Player player) {
		if (HomePermissions.has(player, "multihome.defaulthome.set")) {
			int numHomes = plugin.homes.getUserHomeCount(player);
			int maxHomes = Settings.getSettingMaxHomes(player);
			double amount = 0;

			if (numHomes < maxHomes || maxHomes == -1 || plugin.homes.getHome(player, "") != null) {
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

				plugin.homes.addHome(player, "", player.getLocation());
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

	public void setNamedHome(Player player, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.set")) {
			int numHomes = plugin.homes.getUserHomeCount(player);
			int maxHomes = Settings.getSettingMaxHomes(player);
			double amount = 0;

			if (numHomes < maxHomes || maxHomes == -1 || plugin.homes.getHome(player, home) != null) {
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

				plugin.homes.addHome(player, home, player.getLocation());
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

	public void setPlayerNamedHome(Player player, String owner, String home) {
		if (HomePermissions.has(player, "multihome.othershome.set")) {
			plugin.homes.addHome(owner, home, player.getLocation());
			Settings.sendMessageHomeSet(player, owner + ":" + home);
			Messaging.logInfo("Player " + player.getName() + " set player " + owner + "'s home location [" + home + "]", plugin);
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to set player " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public void deleteDefaultHome(Player player) {
		if (plugin.homes.getHome(player, "") != null) {
			Settings.sendMessageCannotDeleteDefaultHome(player);
		} else {
			Settings.sendMessageNoDefaultHome(player);
		}
		Messaging.logInfo("Player " + player.getName() + " tried to delete deafult home location. Cannot do.", plugin);
	}

	public void deleteNamedHome(Player player, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.delete")) {
			if (plugin.homes.getHome(player, home) != null) {
				plugin.homes.removeHome(player, home);
				Settings.sendMessageHomeDeleted(player, home);
				Messaging.logInfo("Player " + player.getName() + " deleted home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to delete home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public void deletePlayerNamedHome(Player player, String owner, String home) {
		if (HomePermissions.has(player, "multihome.othershome.delete")) {
			if (plugin.homes.getHome(owner, home) != null) {
				plugin.homes.removeHome(owner, home);
				Settings.sendMessageHomeDeleted(player, owner + ":" + home);
				Messaging.logInfo("Player " + player.getName() + " deleted " + owner + "'s home location [" + home + "].", plugin);
			} else {
				Settings.sendMessageNoHome(player, home);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to delete " + owner + "'s home location [" + home + "]. Permission not granted.", plugin);
		}
	}

	public void listHomes(Player player) {
		if (HomePermissions.has(player, "multihome.namedhome.list")) {
			ArrayList<HomeLocation> homes = plugin.homes.listUserHomes(player);

			Settings.sendMessageHomeList(player, Util.compileHomeList(homes));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list home locations. Permission not granted.", plugin);
		}
	}

	public void listPlayerHomes(Player player, String owner) {
		if (HomePermissions.has(player, "multihome.othershome.list")) {
			ArrayList<HomeLocation> homes = plugin.homes.listUserHomes(owner);

			Settings.sendMessageOthersHomeList(player, owner, Util.compileHomeList(homes));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list " + owner + "'s home locations. Permission not granted.", plugin);
		}
	}

	public void listPlayerHomesConsole(CommandSender sender, String owner) {
		ArrayList<HomeLocation> homes = plugin.homes.listUserHomes(owner);

		Settings.sendMessageOthersHomeList(sender, owner, Util.compileHomeList(homes));
	}

	public void inviteDefaultHome(Player player, String target) {
		if (HomePermissions.has(player, "multihome.defaulthome.invite")) {
			if (plugin.homes.getHome(player, "") != null) {
				plugin.invites.addInvite(player.getName(), "", target);
				Settings.sendMessageInviteOwnerHome(player, target, "");

				Player targetPlayer = Util.getExactPlayer(target, this.plugin);
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

	public void inviteNamedHome(Player player, String target, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.invite")) {
			if (plugin.homes.getHome(player, home) != null) {
				plugin.invites.addInvite(player.getName(), home, target);
				Settings.sendMessageInviteOwnerHome(player, target, home);

				Player targetPlayer = Util.getExactPlayer(target, this.plugin);
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


	public void inviteDefaultTimedHome(Player player, String target, int time) {
		if (HomePermissions.has(player, "multihome.defaulthome.invitetimed")) {
			if (plugin.homes.getHome(player, "") != null) {
				plugin.invites.addInvite(player.getName(), "", target, Util.dateInFuture(time));
				Settings.sendMessageInviteTimedOwnerHome(player, target, "", time);

				Player targetPlayer = Util.getExactPlayer(target, this.plugin);
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

	public void inviteNamedTimedHome(Player player, String target, int time, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.invitetimed")) {
			if (plugin.homes.getHome(player, home) != null) {
				plugin.invites.addInvite(player.getName(), home, target, Util.dateInFuture(time));
				Settings.sendMessageInviteOwnerHome(player, target, home);

				Player targetPlayer = Util.getExactPlayer(target, this.plugin);
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

	public void uninviteDefaultHome(Player player, String target) {
		if (HomePermissions.has(player, "multihome.defaulthome.uninvite")) {
			if (plugin.invites.getInvite(player.getName(), "", target) != null) {
				plugin.invites.removeInvite(player.getName(), "", target);

				Settings.sendMessageUninviteOwnerHome(player, target, "");

				Player targetPlayer = Util.getExactPlayer(target, this.plugin);
				if (targetPlayer != null) {
					Settings.sendMessageUninviteTargetHome(targetPlayer, player.getName(), "");
				}

				Messaging.logInfo("Player " + player.getName() + " removed invite for " + target + " to their default home.", plugin);
			} else {
				Settings.sendMessageNoDefaultHome(player);
			}
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to remove invite for " + target + " to their default home. Permission not granted.", plugin);
		}
	}

	public void uninviteNamedHome(Player player, String target, String home) {
		if (HomePermissions.has(player, "multihome.namedhome.uninvite")) {
			if (plugin.invites.getInvite(player.getName(), home, target) != null) {
				plugin.invites.removeInvite(player.getName(), home, target);
				Settings.sendMessageUninviteOwnerHome(player, target, home);

				Player targetPlayer = Util.getExactPlayer(target, this.plugin);
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

	public void listInvitesToMe(Player player) {
		if (HomePermissions.has(player, "multihome.listinvites.tome")) {
			ArrayList<HomeInvite> invites = plugin.invites.getListPlayerInvitesToMe(player.getName());

			Settings.sendMessageInviteListToMe(player, player.getName(), Util.compileInviteListForMe(player.getName(), invites));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list invitations open to them. Permission not granted.", plugin);
		}
	}

	public void listInvitesToOthers(Player player) {
		if (HomePermissions.has(player, "multihome.listinvites.toothers")) {
			ArrayList<HomeInvite> invites = plugin.invites.getListPlayerInvitesToOthers(player.getName());

			Settings.sendMessageInviteListToOthers(player, player.getName(), Util.compileInviteListForOthers(invites));
		} else {
			Messaging.logInfo("Player " + player.getName() + " tried to list invitations they've given. Permission not granted.", plugin);
		}
	}
}
