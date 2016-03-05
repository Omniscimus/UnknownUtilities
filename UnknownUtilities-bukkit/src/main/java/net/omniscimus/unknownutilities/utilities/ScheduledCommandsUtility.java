package net.omniscimus.unknownutilities.utilities;

import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownUtility;
import org.bukkit.command.CommandSender;

/**
 * This module provides functionality for executing commands in the Minecraft
 * server at specified times.
 */
public class ScheduledCommandsUtility extends UnknownUtility {

    private final transient UnknownUtilities plugin;

    /**
     * Constructs the object.
     *
     * @param plugin the plugin instance
     */
    public ScheduledCommandsUtility(UnknownUtilities plugin) {
	this.plugin = plugin;
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    /**
     * Executes the specified server command.
     *
     * @param command the command to execute
     * @param sender the CommandSender who should execute the command; leave
     * null to use the server console
     * @param delay the amount of ticks to wait before executing the command
     * @param interval the interval in ticks at which the command should be
     * executed repeatedly; 0 if it should not be repeated
     */
    public void executeCommand(final String command, CommandSender sender, long delay, long interval) {
	final CommandSender commandSender
		= (sender == null) ? plugin.getServer().getConsoleSender() : sender;

	plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
	    plugin.getServer().dispatchCommand(commandSender, command);
	}, delay);
    }

}
