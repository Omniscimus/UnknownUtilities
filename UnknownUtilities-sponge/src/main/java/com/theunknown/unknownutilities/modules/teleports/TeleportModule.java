package com.theunknown.unknownutilities.modules.teleports;

import com.theunknown.unknownutilities.modules.Commandable;
import com.theunknown.unknownutilities.Command;
import com.theunknown.unknownutilities.UnknownUtilitiesPlugin;
import com.theunknown.unknownutilities.modules.Module;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Provides teleporting functionality.
 */
public class TeleportModule extends Module implements Commandable {

    private TeleportRequestManager requestManager;

    /**
     * List of commands handled by TeleportModule
     */
    private Command[] commands;

    public TeleportModule() {
	super(true);
    }

    @Override
    public void enable() {
	this.requestManager = new TeleportRequestManager();
	this.commands = new Command[]{
	    new TeleportAskCommand(requestManager),
	    new TeleportAskHereCommand(requestManager),
	    new TeleportYesCommand(requestManager)
	};
    }

    @Override
    public void disable() {
    }

    @Override
    public Command[] getCommands() {
	return commands;
    }

    /**
     * Gets if the player can be teleported safely around the specified
     * location.
     *
     * @param location the location to be checked
     * @return true if the location is safe
     */
    public boolean locationIsSafe(Location<World> location) {
	return UnknownUtilitiesPlugin.plugin.getGame().getTeleportHelper()
		.getSafeLocation(location).isPresent();
    }

}
