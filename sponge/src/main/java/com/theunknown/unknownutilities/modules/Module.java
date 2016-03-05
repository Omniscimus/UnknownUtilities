package com.theunknown.unknownutilities.modules;

/**
 * Represents a branch of functionality of this plugin.
 */
public abstract class Module {

    /**
     * Indicates whether this module implements any listeners from the Sponge
     * API that should be registered.
     *
     * @see org.spongepowered.api.event.EventManager
     */
    public boolean hasListeners = false;

    /**
     * Creates a new Module.
     *
     * @param hasListeners whether this module implements any listeners from the
     * Sponge API that should be registered.
     */
    public Module(boolean hasListeners) {
	this.hasListeners = hasListeners;
    }

    /**
     * Enables this module. Should be called in the modules registrar:
     * {@link com.theunknown.unknownutilities.UnknownUtilities}
     */
    public abstract void enable();

    /**
     * Disables this module. Should be called in the modules registrar:
     * {@link com.theunknown.unknownutilities.UnknownUtilities}
     */
    public abstract void disable();

}
