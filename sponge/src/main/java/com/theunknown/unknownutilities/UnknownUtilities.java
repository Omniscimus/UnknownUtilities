package com.theunknown.unknownutilities;

import com.theunknown.unknownutilities.modules.Module;
import com.theunknown.unknownutilities.modules.Commandable;
import java.util.ArrayList;

/**
 * Main class for this plugin. Controls enabled modules.
 */
public class UnknownUtilities {

    /**
     * A list of all currently enabled modules.
     */
    private final ArrayList<Module> enabledModules;

    public UnknownUtilities() {
	this.enabledModules = new ArrayList<>();
    }

    /**
     * Registers a new module. If the module has listeners or commands, they
     * will be registered to the Sponge API here.
     *
     * @param module the module to enable
     * @return false if an instance of the specified module is already enabled;
     * true if the module was registered successfully
     */
    public boolean enableModule(Module module) {

	if (!enabledModules.stream().noneMatch(
		(enabledModule) -> (module.getClass().equals(enabledModule.getClass())))) {
	    // Already registered
	    return false;
	}

	module.enable();
	if (module.hasListeners) {
	    UnknownUtilitiesPlugin.plugin.getGame().getEventManager()
		    .registerListeners(UnknownUtilitiesPlugin.plugin, module);
	}
	if (module instanceof Commandable) {
	    UnknownUtilitiesPlugin plugin = UnknownUtilitiesPlugin.plugin;
	    for (Command command : ((Commandable) module).getCommands()) {
		plugin.getGame().getCommandManager()
			.register(plugin, command.getCallable(), command.getAliases());
	    }
	}

	enabledModules.add(module);

	return true;
    }

    /**
     * Disables all previously registered instances of a module.
     *
     * @param moduleClass the class of the module(s) to unregister
     * @return the number of disabled instances of the module
     */
    public long disableModule(Class<? extends Module> moduleClass) {

	return enabledModules.stream()
		.filter((module) -> (module.getClass().equals(moduleClass)))
		.map((module) -> {
		    module.disable();
		    enabledModules.remove(module);
		    return module;
		}).count();
    }

}
