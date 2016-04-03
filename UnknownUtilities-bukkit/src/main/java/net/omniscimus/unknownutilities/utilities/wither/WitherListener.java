package net.omniscimus.unknownutilities.utilities.wither;

import java.util.UUID;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listener for events related to the Wither arena.
 */
public class WitherListener implements Listener {

    private final WitherUtility witherUtility;

    private UUID lastWitherSkullPlacer;

    /**
     * Creates the object.
     *
     * @param witherUtility WitherUtility instance
     */
    public WitherListener(WitherUtility witherUtility) {
	this.witherUtility = witherUtility;
    }

    /**
     * Called by Bukkit whenever a creature spawns in the game. Used to prevent
     * wither spawns outside of the arena.
     *
     * @param event the event that occurred
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
	if (event.getSpawnReason() == SpawnReason.BUILD_WITHER) {
	    /* Prevent wither spawning outside of the arena */
	    boolean inWitherArena = witherUtility.getArenaManager()
		    .isInWitherArena(event.getLocation());
	    if (!inWitherArena) {
		event.setCancelled(true);
	    }
	}
    }

    /**
     * Called by Bukkit whenever a block is placed. Used to track the player who
     * spawned a wither (who placed the third skull).
     *
     * @param event the event that occurred
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
	BlockState state = event.getBlock().getState();
	if (state instanceof Skull) {
	    if (((Skull) state).getSkullType() == SkullType.WITHER) {
		lastWitherSkullPlacer = event.getPlayer().getUniqueId();
	    }
	}
    }

    /**
     * Called by Bukkit whenever a player dies. Used to track when a player dies
     * while fighting the wither.
     *
     * @param event the event that occurred
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
	witherUtility.removeFightingPlayer(event.getEntity());
    }

    /**
     * Unregisters this Listener from all events.
     */
    public void disable() {
	CreatureSpawnEvent.getHandlerList().unregister(this);
    }

}
