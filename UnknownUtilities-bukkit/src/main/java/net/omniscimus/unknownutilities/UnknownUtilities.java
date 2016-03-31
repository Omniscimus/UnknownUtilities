package net.omniscimus.unknownutilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.unknownutilities.utilities.ScheduledCommandsUtility;
import net.omniscimus.unknownutilities.utilities.wither.WitherUtility;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for this plugin's communication with Bukkit. This class is
 * instantiated by Bukkit.
 */
public class UnknownUtilities extends JavaPlugin {

    private static final Logger logger = Logger.getLogger(UnknownUtilities.class.getName());

    private static UnknownUtilities inst;
    private transient Settings settings;
    private final ArrayList<UnknownUtility> enabledModules = new ArrayList<>();
    private static final Map<String, Class<? extends UnknownUtility>> MODULES;

    static {
	HashMap<String, Class<? extends UnknownUtility>> map = new HashMap<>();
	// Add all possible modules here
	map.put("scheduledcommands", ScheduledCommandsUtility.class);
	map.put("wither", WitherUtility.class);

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

	settings = new Settings(this);
	settings.getEnabledModules().stream().forEach((module) -> {
	    try {
		enableModule(module);
		logger.log(Level.INFO, "Enabled module: {0}", module.getSimpleName());
	    } catch (ModuleException ex) {
		logger.log(Level.WARNING, "Failed to enable module: " + module.getSimpleName(), ex);
	    }
	});
    }

    /**
     * Gets a module's class by its name.
     *
     * @param name the name of the module
     * @return the class of the module, or null if there's no module with that
     * name
     */
    public static Class<? extends UnknownUtility> getModuleClass(String name) {
	return MODULES.get(name);
    }

    /**
     * Gets a module's name by its class.
     *
     * @param clazz the class of the module whose name should be given
     * @return the name of the module, or null if the given module isn't
     * registered
     */
    public static String getModuleName(Class<? extends UnknownUtility> clazz) {
	for (Entry<String, Class<? extends UnknownUtility>> entry : MODULES.entrySet()) {
	    if (clazz.equals(entry.getValue())) {
		return entry.getKey();
	    }
	}
	return null;
    }

    /**
     * Gets an enabled module by its class.
     *
     * @param clazz the module's class
     * @return the module, if it is enabled; otherwise null
     */
    public UnknownUtility getEnabledModule(Class<? extends UnknownUtility> clazz) {
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
     * @throws ModuleException if the module couldn't be enabled
     */
    public void enableModule(Class<? extends UnknownUtility> module)
	    throws ModuleException {
	try {
	    UnknownUtility instance = module.getDeclaredConstructor(UnknownUtilities.class)
		    .newInstance(this);
	    instance.enable();
	    enabledModules.add(instance);
	} catch (NoSuchMethodException | SecurityException |
		InstantiationException | IllegalAccessException |
		IllegalArgumentException | InvocationTargetException ex) {
	    throw new ModuleException(ex);
	}
    }

    /**
     * Disables a module, if it was enabled.
     *
     * @param module the class of the module to disable
     */
    public void disableModule(Class<? extends UnknownUtility> module) {
	UnknownUtility toDisable = getEnabledModule(module);
	toDisable.disable();
	enabledModules.remove(toDisable);
    }

    /**
     * Reloads the specified module.
     *
     * @param module the module to reload
     * @throws ModuleException if the module couldn't be re-enabled
     */
    public void reloadModule(Class<? extends UnknownUtility> module)
	    throws ModuleException {
	disableModule(module);
	enableModule(module);
    }

}
