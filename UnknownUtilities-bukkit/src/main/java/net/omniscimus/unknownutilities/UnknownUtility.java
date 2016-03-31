package net.omniscimus.unknownutilities;

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

}
