package net.omniscimus.unknownutilities;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a module of this plugin, containing some functionality.
 */
public abstract class UnknownUtility {

    /**
     * Enables this module.
     *
     * @throws ModuleException if the module could not be enabled
     */
    protected abstract void enable() throws ModuleException;

    /**
     * Disables this module.
     */
    protected abstract void disable();

    /**
     * Gets the settings for this module.
     *
     * @return a ConfigurationSection of config.yml containing specific settings
     * for this module
     */
    public ConfigurationSection getSettings() {
	return UnknownUtilities.inst().getSettings().getModuleSettings(this);
    }

}
