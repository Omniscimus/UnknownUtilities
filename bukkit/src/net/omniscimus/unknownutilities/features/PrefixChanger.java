package net.omniscimus.unknownutilities.features;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class PrefixChanger extends UnknownFeature implements CommandExecutor {

	private final UnknownUtilities plugin;
	
	@Override
	public boolean enable() {
		plugin.getCommand("prefix").setExecutor(this);
		return true;
	}
	@Override
	public boolean disable() {
		plugin.getCommand("prefix").setExecutor(plugin);
		return true;
	}
	
	public PrefixChanger(UnknownUtilities plugin) {
		this.plugin = plugin;
		enable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

			if((sender instanceof Player)) {
				if(sender.hasPermission("unknownutilities.prefix")) {
					if(args.length == 1) {
						if(args[0].length() <= 2) {
							
							Server server = plugin.getServer();
							
							if(sender.hasPermission("unknownutilities.group.administrator")) {
								server.dispatchCommand(server.getConsoleSender(), "pex user " + sender.getName() + " prefix \"&7[LVL " + args[0] + "&7] &9[A] \"");
								sender.sendMessage(ChatColor.GOLD + "Prefix changed!");
							}
							else if(sender.hasPermission("unknownutilities.group.moderator")) {
								server.dispatchCommand(server.getConsoleSender(), "pex user " + sender.getName() + " prefix \"&7[LVL " + args[0] + "&7]&r &6[M] \"");
								sender.sendMessage(ChatColor.GOLD + "Prefix changed!");
							}
							else if(sender.hasPermission("unknownutilities.group.helper")) {
								server.dispatchCommand(server.getConsoleSender(), "pex user " + sender.getName() + " prefix \"&7[LVL " + args[0] + "&7]&r &6[H] \"");
								sender.sendMessage(ChatColor.GOLD + "Prefix changed!");
							}
							else if(sender.hasPermission("unknownutilities.group.builder")) {
								server.dispatchCommand(server.getConsoleSender(), "pex user " + sender.getName() + " prefix \"&7[LVL " + args[0] + "&7] &3[B] \"");
								sender.sendMessage(ChatColor.GOLD + "Prefix changed!");
							}
							else {
								// only donator rank
								server.dispatchCommand(server.getConsoleSender(), "pex user " + sender.getName() + " prefix \"&7[LVL " + args[0] + "&7] &b[D] \"");
								sender.sendMessage(ChatColor.GOLD + "Prefix changed!");
							}
						}
						else sender.sendMessage(ChatColor.RED + "Please choose a Level prefix of 1 or 2 characters.");
					}
					else sender.sendMessage(ChatColor.RED + "Wrong command syntax.");
				}
				else sender.sendMessage(ChatColor.RED + "This is a Donators-only feature ツ");
			}
			else sender.sendMessage("Lol wut?");

		return true;

	}
	
}
