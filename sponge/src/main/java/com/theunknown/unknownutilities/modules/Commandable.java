package com.theunknown.unknownutilities.modules;

import com.theunknown.unknownutilities.Command;

/**
 * Implemented by modules that can execute commands.
 */
public interface Commandable {
    
    public Command[] getCommands();
    
}
