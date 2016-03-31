package net.omniscimus.unknownutilities.utilities.wither;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;

/**
 * Manager for the location of the wither arena.
 */
public class ArenaManager {

    private final WorldGuardPlugin worldGuard;

    /**
     * Constructs the object.
     * 
     * @param worldGuard WorldGuardPlugin instance
     */
    public ArenaManager(WorldGuardPlugin worldGuard) {
	this.worldGuard = worldGuard;
    }

    /**
     * Gets whether the specified location is within the wither arena.
     *
     * @param loc the location to check for a wither arena region
     * @return if loc is in the wither arena. Returns false if the region
     * information couldn't be found
     */
    public boolean isInWitherArena(Location loc) {
	RegionManager rm = worldGuard.getRegionContainer().get(loc.getWorld());
	if (rm == null) {
	    return false;
	}
	return rm.getApplicableRegions(loc).getRegions().stream().anyMatch(
		(region) -> (region.getId().equalsIgnoreCase("wither")));
    }

}
