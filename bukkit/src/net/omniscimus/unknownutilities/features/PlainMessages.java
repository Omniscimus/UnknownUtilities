package net.omniscimus.unknownutilities.features;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class PlainMessages extends UnknownFeature implements CommandExecutor {

	private UnknownUtilities plugin;

	@Override
	public boolean enable() {
		plugin.getCommand("ubr").setExecutor(this);
		plugin.getCommand("umsg").setExecutor(this);
		return true;
	}
	@Override
	public boolean disable() {
		plugin.getCommand("ubr").setExecutor(plugin);
		plugin.getCommand("umsg").setExecutor(plugin);
		return true;
	}

	public PlainMessages(UnknownUtilities plugin) {
		this.plugin = plugin;
		enable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(sender.hasPermission("unknownutilities.plainmessage")) {
			if(args.length > 0) {
				if(commandLabel.equalsIgnoreCase("ubr")) {
					plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', concatArgs(args)));
					return true;
				}
				else if(commandLabel.equalsIgnoreCase("umsg")) {
					// Make a new array without args[0]
					String[] wordsArray = new String[args.length - 1];
					for(int i = 1; i < args.length; i++) wordsArray[i] = args[i];
					// Send the message
					plugin.getServer().getPlayer(args[0]).sendMessage(ChatColor.translateAlternateColorCodes('&', concatArgs(wordsArray)));
					return true;
				}
			}
			else sender.sendMessage(ChatColor.RED + "Wrong command syntax.");
		}
		return true;

	}

	private String concatArgs(String[] args) {
		StringBuilder messageBuilder = new StringBuilder();
		for(String arg : args) {
			messageBuilder.append(arg).append(" ");
		}
		return messageBuilder.toString();
	}

}
