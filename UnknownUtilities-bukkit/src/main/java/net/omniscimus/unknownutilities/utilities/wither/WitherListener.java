package net.omniscimus.unknownutilities.utilities.wither;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Listener for events related to the Wither arena.
 */
public class WitherListener implements Listener {

    private final WitherUtility witherUtility;

    /**
     * Creates the object.
     *
     * @param witherUtility WitherUtility instance
     */
    public WitherListener(WitherUtility witherUtility) {
	this.witherUtility = witherUtility;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
	Entity entity = event.getEntity();
	Location location = event.getLocation();

	if (entity.getType() == EntityType.WITHER) {
	    /* Prevent wither spawning outside of the arena */
	    boolean inWitherArena = witherUtility.getArenaManager()
		    .isInWitherArena(location);
	    event.setCancelled(!inWitherArena);
	}
    }

    /**
     * Unregisters this Listener from all events.
     */
    public void disable() {
	CreatureSpawnEvent.getHandlerList().unregister(this);
    }

}
