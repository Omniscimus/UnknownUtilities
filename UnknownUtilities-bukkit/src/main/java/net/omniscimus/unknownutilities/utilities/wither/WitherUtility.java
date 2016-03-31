package net.omniscimus.unknownutilities.utilities.wither;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.omniscimus.unknownutilities.ModuleException;
import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownUtility;
import org.bukkit.plugin.Plugin;

/**
 * This module allows players to fight the wither in an arena.
 */
public class WitherUtility extends UnknownUtility {
    
    private final UnknownUtilities plugin;
    
    private ArenaManager arenaManager;
    private WitherListener listener;
    
    /**
     * Constructs the object.
     *
     * @param plugin the plugin instance
     */
    public WitherUtility(UnknownUtilities plugin) {
	this.plugin = plugin;
    }
    
    /**
     * Gets the object that handles the wither arena's location.
     * 
     * @return ArenaManager instance
     */
    public ArenaManager getArenaManager() {
	return arenaManager;
    }

    @Override
    protected void enable() throws ModuleException {
	
	/* Find WorldGuard for determining the arena region */
	Plugin worldGuard = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	if (!(worldGuard instanceof WorldGuardPlugin)) {
	    throw new ModuleException("Couldn't find a compatible version of WorldGuard. WitherUtility could not be enabled.");
	}
	arenaManager = new ArenaManager((WorldGuardPlugin) worldGuard);
	
	listener = new WitherListener(this);
	plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    protected void disable() {
	listener.disable();
    }

}
