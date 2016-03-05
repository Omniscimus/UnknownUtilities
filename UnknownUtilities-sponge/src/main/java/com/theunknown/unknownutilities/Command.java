package com.theunknown.unknownutilities;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

/**
 * Represents a command that a player or the console can execute.
 *
 * @author omniscimus
 */
public abstract class Command implements CommandExecutor {

    public static final String PERMISSION_BASE = "unknownutilities.";

    /**
     * The command itself
     */
    private CommandCallable callable;

    /**
     * All possible aliases of this command
     */
    private final String[] aliases;

    /**
     * Creates a new Command.
     *
     * @param aliases all possible aliases of this command
     */
    public Command(String[] aliases) {
	this.aliases = aliases;
	setCallable();
    }
    
    /**
     * Gets a new CommandException with a message indicating wrong syntax.
     * 
     * @return 
     */
    public CommandException getSyntaxException() {
	return new CommandException(Text.of("Wrong command syntax."));
    }

    /**
     * Constructs the CommandCallable containing the information about the
     * command.
     *
     * @return a CommandCallable created for this specific instance of Command
     */
    protected abstract CommandCallable constructCallable();

    /**
     * Sets the callable to a new callable, constructed by this specific
     * instance of Command.
     */
    private void setCallable() {
	this.callable = constructCallable();
    }

    public CommandCallable getCallable() {
	return callable;
    }

    public String[] getAliases() {
	return aliases;
    }

}
