package net.omniscimus.unknownutilities;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Contains configuration information.
 */
public class Settings {

    private static final Logger logger = Logger.getLogger(Settings.class.getName());

    private final transient UnknownUtilities plugin;
    private transient FileConfiguration config;

    /**
     * Constructs the object.
     *
     * @param plugin UnknownUtilities instance
     */
    public Settings(UnknownUtilities plugin) {
	this.plugin = plugin;
	plugin.saveDefaultConfig();
	this.config = plugin.getConfig();
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
	plugin.reloadConfig();
	config = plugin.getConfig();
    }

    /**
     * Gets the modules that should be enabled in the plugin from the
     * configuration.
     *
     * @return a list of UnknownUtility that should be enabled
     */
    public ArrayList<Class<? extends UnknownUtility>> getEnabledModules() {
	ConfigurationSection modules = config.getConfigurationSection("modules");
	ArrayList<Class<? extends UnknownUtility>> enabledModules = new ArrayList<>();
	modules.getKeys(false).stream()
		.filter((module) -> (modules.getBoolean(module + ".enabled", false)))
		.forEach((module) -> {
		    Class<? extends UnknownUtility> clazz = UnknownUtilities.getModuleClass(module);
		    if (clazz != null) {
			enabledModules.add(clazz);
		    } else {
			logger.log(Level.WARNING, "Did not recognize enabled module in config.yml: {0}", module);
		    }
		});
	return enabledModules;
    }

    /**
     * Gets the ConfigurationSection containing the settings for a specific
     * module.
     *
     * @param module the module whose settings should be looked up
     * @return the requested ConfigurationSection, or null if it couldn't be
     * found
     */
    public ConfigurationSection getModuleSettings(UnknownUtility module) {
	String moduleName = UnknownUtilities.getModuleName(module.getClass());
	String path = "modules." + moduleName + ".settings";
	return config.getConfigurationSection(path);
    }

}
