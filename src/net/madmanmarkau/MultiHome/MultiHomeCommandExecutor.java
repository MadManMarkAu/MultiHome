package net.madmanmarkau.MultiHome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiHomeCommandExecutor implements CommandExecutor {
	MultiHome plugin;

	public MultiHomeCommandExecutor(MultiHome plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player)) {
			// Command sent by console/plugin
			onCommandFromConsole(sender, cmd, commandLabel, args);
		} else {
			// Command sent by player
			onCommandFromPlayer((Player) sender, cmd, commandLabel, args);
		}
		return true;
	}

	private void onCommandFromPlayer(Player player, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().compareToIgnoreCase("home") == 0 || cmd.getName().compareToIgnoreCase("mhome") == 0) {

			if (args.length == 0) {
				MultiHomeCommands.goDefaultHome(this.plugin, player);
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);

				if (homeArgs.length > 1) {
					MultiHomeCommands.goPlayerNamedHome(this.plugin, player, homeArgs[0], homeArgs[1]);
				} else {
					MultiHomeCommands.goNamedHome(this.plugin, player, homeArgs[0]);
				}
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("sethome") == 0 || cmd.getName().compareToIgnoreCase("msethome") == 0) {

			if (args.length == 0) {
				MultiHomeCommands.setDefaultHome(this.plugin, player);
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);
				
				if (homeArgs.length > 1) {
					MultiHomeCommands.setPlayerNamedHome(this.plugin, player, homeArgs[0], homeArgs[1]);
				} else {
					MultiHomeCommands.setNamedHome(this.plugin, player, homeArgs[0]);
				}
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("deletehome") == 0 || cmd.getName().compareToIgnoreCase("mdeletehome") == 0) {

			if (args.length == 0) {
				MultiHomeCommands.deleteDefaultHome(this.plugin, player);
			} else if (args.length == 1) {
				String homeArgs[] = Util.splitHome(args[0]);

				if (homeArgs.length > 1) {
					MultiHomeCommands.deletePlayerNamedHome(this.plugin, player, homeArgs[0], homeArgs[1]);
				} else {
					MultiHomeCommands.deleteNamedHome(this.plugin, player, homeArgs[0]);
				}
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("listhomes") == 0 || cmd.getName().compareToIgnoreCase("mlisthomes") == 0) {
			
			if (args.length == 0) {
				MultiHomeCommands.listHomes(this.plugin, player);
			} else if (args.length == 1) {
				MultiHomeCommands.listPlayerHomes(this.plugin, player, args[0]);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("invitehome") == 0 || cmd.getName().compareToIgnoreCase("minvitehome") == 0) {

			if (args.length == 1) {
				MultiHomeCommands.inviteDefaultHome(this.plugin, player, args[0]);
			} else if (args.length == 2) {
				MultiHomeCommands.inviteNamedHome(this.plugin, player, args[0], args[1]);
			}

		} else if (cmd.getName().compareToIgnoreCase("invitehometimed") == 0 || cmd.getName().compareToIgnoreCase("minvitehometimed") == 0) {

			if (args.length == 2) {
				MultiHomeCommands.inviteDefaultTimedHome(this.plugin, player, args[0], Util.decodeTime(args[1]));
			} else if (args.length == 3) {
				MultiHomeCommands.inviteNamedTimedHome(this.plugin, player, args[0], Util.decodeTime(args[1]), args[2]);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("uninvitehome") == 0 || cmd.getName().compareToIgnoreCase("muninvitehome") == 0) {

			if (args.length == 1) {
				MultiHomeCommands.uninviteDefaultHome(this.plugin, player, args[0]);
			} else if (args.length == 2) {
				MultiHomeCommands.uninviteNamedHome(this.plugin, player, args[0], args[1]);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("listinvites") == 0 || cmd.getName().compareToIgnoreCase("mlistinvites") == 0) {

			if (args.length == 0) {
				MultiHomeCommands.listInvitesToMe(this.plugin, player);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		} else if (cmd.getName().compareToIgnoreCase("listmyinvites") == 0 || cmd.getName().compareToIgnoreCase("mlistmyinvites") == 0) {

			if (args.length == 0) {
				MultiHomeCommands.listInvitesToOthers(this.plugin, player);
			} else {
				Settings.sendMessageTooManyParameters(player);
			}

		}
	}

    private void onCommandFromConsole(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	if (cmd.getName().compareToIgnoreCase("listhomes") == 0 || cmd.getName().compareToIgnoreCase("mlisthomes") == 0) {

			if (args.length == 0) {
				Messaging.sendError(sender, "Missing <player> argument.");
			} else if (args.length == 1) {
				MultiHomeCommands.listPlayerHomesConsole(this.plugin, sender, args[0]);
			} else {
				Settings.sendMessageTooManyParameters(sender);
			}

		} else {
			Messaging.sendError(sender, "This command is not available from console.");
		}
    }
}
