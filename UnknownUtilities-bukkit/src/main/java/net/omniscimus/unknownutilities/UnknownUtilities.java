package net.omniscimus.unknownutilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for this plugin's communication with Bukkit. This class is
 * instantiated by Bukkit.
 */
public class UnknownUtilities extends JavaPlugin {

    private static final Logger logger = Logger.getLogger(UnknownUtilities.class.getName());

    private static UnknownUtilities inst;
    private transient Settings settings;
    private static final ArrayList<UnknownUtility> enabledModules = new ArrayList<>();
    public static final Map<String, Class<? extends UnknownUtility>> MODULES;

    static {
	HashMap<String, Class<? extends UnknownUtility>> map = new HashMap<>();
	// Add all possible modules here
	MODULES = Collections.unmodifiableMap(map);
    }

    /**
     * Gets the current instance of UnknownUtilities
     *
     * @return UnknownUtilities instance
     */
    public static UnknownUtilities inst() {
	return inst;
    }

    /**
     * Gets the current instance of Settings
     *
     * @return Settings instance
     */
    public Settings getSettings() {
	return settings;
    }

    /**
     * Called when the plugin enables.
     */
    @Override
    public void onEnable() {
	inst = this;

	settings.getEnabledModules().stream().forEach((module) -> {
	    try {
		module.newInstance().enable();
		logger.log(Level.INFO, "Enabled module: {0}", module.getSimpleName());
	    } catch (InstantiationException | IllegalAccessException ex) {
		logger.log(Level.WARNING, "Failed to enable module: " + module.getSimpleName(), ex);
	    }
	});
    }

    /**
     * Gets a module by its class.
     *
     * @param clazz the module's class
     * @return the module, if it is enabled; otherwise null
     */
    public UnknownUtility getModule(Class<? extends UnknownUtility> clazz) {
	for (UnknownUtility module : enabledModules) {
	    if (module.getClass().equals(clazz)) {
		return module;
	    }
	}
	return null;
    }

    /**
     * Enables the specified module.
     *
     * @param module the module to enable
     * @throws InstantiationException @see Class#newInstance()
     * @throws IllegalAccessException @see Class#newInstance()
     */
    public void enableModule(Class<? extends UnknownUtility> module)
	    throws InstantiationException, IllegalAccessException {

	UnknownUtility instance = module.newInstance();
	instance.enable();
	enabledModules.add(instance);
    }

    /**
     * Disables a module, if it was enabled.
     *
     * @param module the class of the module to disable
     */
    public void disableModule(Class<? extends UnknownUtility> module) {
	UnknownUtility toDisable = getModule(module);
	toDisable.disable();
	enabledModules.remove(toDisable);
    }

    /**
     * Reloads the specified module.
     *
     * @param module the module to reload
     * @throws InstantiationException @see Class#newInstance()
     * @throws IllegalAccessException @see Class#newInstance()
     */
    public void reloadModule(Class<? extends UnknownUtility> module)
	    throws InstantiationException, IllegalAccessException {
	disableModule(module);
	enableModule(module);
    }

}
