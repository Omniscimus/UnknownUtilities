package net.omniscimus.unknownutilities;

import java.util.ArrayList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Contains configuration information.
 */
public class Settings {

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
		    enabledModules.add(UnknownUtilities.MODULES.get(module));
		});
	return enabledModules;
    }

}
