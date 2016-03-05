package net.omniscimus.unknownutilities.features;

/**
 * @author Omniscimus
 * @version 1.1.0
 */

public final class DonatorRankManager {
	/*
	private final Main plugin;
	
	public DonatorRankManager(Main plugin) {
		this.plugin = plugin;
	}
	
	protected boolean setupPermissions() {
		if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}
		plugin.perms = rsp.getProvider();
		return plugin.perms != null;
	}
	
	protected void maakDonator(CommandSender sender, String[] args) {

		if(args.length == 0) {
			sender.sendMessage("Player name wasn't specified, /makedonator playerName");
		}
		else if(args.length > 1) {
			sender.sendMessage("/makedonator playerName");
		}
		else {
			Server server = Bukkit.getServer();
			Player targetPlayer = server.getPlayer(args[0]);
			String message = String.format("Made player %s a donator.", args[0]);
			
			if(targetPlayer == null) sender.sendMessage("That player doesn't exist or isn't online.");
			else {
				
				if(plugin.perms.playerInGroup(targetPlayer, "user_01")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_01");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_01");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_02")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_02");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_02");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_03")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_03");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_03");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_04")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_04");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_04");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_05")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_05");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_05");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_06")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_06");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_06");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_07")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_07");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_07");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_08")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_08");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_08");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_09")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_09");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_09");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_10")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_10");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_10");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_11")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_11");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_11");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_12")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_12");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_12");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_13")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_13");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_13");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_14")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_14");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_14");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_15")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_15");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_15");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_16")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_16");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_16");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_17")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_17");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_17");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_18")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_18");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_18");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_19")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_19");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_19");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_20")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_20");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_20");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_21")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_21");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_21");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_22")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_22");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_22");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_23")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_23");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_23");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_24")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_24");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_24");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_25")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_25");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_25");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_26")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_26");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_26");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_27")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_27");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_27");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_28")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_28");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_28");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_29")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_29");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_29");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_30")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_30");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_30");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_31")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_31");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_31");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_32")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_32");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_32");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_33")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_33");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_33");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_34")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_34");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_34");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_35")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_35");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_35");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_36")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_36");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_36");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_37")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_37");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_37");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_38")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_38");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_38");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_39")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_39");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_39");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_40")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_40");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_40");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_41")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_41");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_41");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_42")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_42");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_42");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_43")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_43");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_43");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_44")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_44");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_44");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_45")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_45");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_45");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_46")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_46");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_46");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_47")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_47");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_47");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_48")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_48");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_48");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_49")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_49");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_49");
					sender.sendMessage(message);
				}
				else if(plugin.perms.playerInGroup(targetPlayer, "user_50")) {
					plugin.perms.playerAddGroup(targetPlayer, "donator_50");
					plugin.perms.playerRemoveGroup(targetPlayer, "user_50");
					sender.sendMessage(message);
				}

				else {
					sender.sendMessage("Error. Wrong command syntax? /makedonator playerName\nIs that player in a User-group anyway?");
				}

			}
		}

	}
	*/
}