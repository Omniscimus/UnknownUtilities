package com.theunknown.unknownutilities.exception;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

/**
 * Thrown if a command sender is not a Player while it should be one.
 */
public class SenderNotPlayerException extends CommandException {
    private static final long serialVersionUID = 1L;

    public SenderNotPlayerException() {
	super(Text.of("This command can only be executed by players."));
    }
    
    public SenderNotPlayerException(Text message) {
	super(message);
    }
    
    public SenderNotPlayerException(Text message, Throwable cause) {
	super(message, cause);
    }
    
}
