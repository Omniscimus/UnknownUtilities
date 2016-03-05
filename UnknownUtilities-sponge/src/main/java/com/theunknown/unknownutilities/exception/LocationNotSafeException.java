package com.theunknown.unknownutilities.exception;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

/**
 * Thrown if a certain location is not safe to teleport to.
 */
public class LocationNotSafeException extends CommandException {
    private static final long serialVersionUID = 1L;

    public LocationNotSafeException() {
	super(Text.of("I don't think that place is safe."));
    }
    
    public LocationNotSafeException(Text message) {
	super(message);
    }
    
    public LocationNotSafeException(Text message, Throwable cause) {
	super(message, cause);
    }
    
}
