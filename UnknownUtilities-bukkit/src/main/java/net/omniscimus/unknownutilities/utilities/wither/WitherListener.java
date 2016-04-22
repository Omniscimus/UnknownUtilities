package net.omniscimus.unknownutilities.utilities.wither;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Listener for events related to the Wither arena.
 */
public class WitherListener implements Listener {
    
    private static final Logger logger = Logger.getLogger(WitherListener.class.getName());

    private final WitherUtility witherUtility;

    private Player lastWitherSkullPlacer;

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
	if (event.getSpawnReason() != SpawnReason.BUILD_WITHER) {
	    return;
	}

	/* Prevent wither spawning outside of the arena */
	boolean inWitherArena = witherUtility.getArenaManager()
		.isInWitherArena(event.getLocation());
	if (!inWitherArena) {
	    event.setCancelled(true);
	    return;
	}
	
	if (lastWitherSkullPlacer == null || !lastWitherSkullPlacer.isOnline()) {
	    event.setCancelled(true);
	    logger.log(Level.WARNING, "Someone tried to spawn the Wither, but the plugin can't find that player. Cancelling.");
	    return;
	}

	/* Try to remove vote points from the player who spawned it */
	try {
	    if (!witherUtility.getVotesDatabase().removeVotes(lastWitherSkullPlacer.getUniqueId(), 20)) {
		event.setCancelled(true);
		lastWitherSkullPlacer.sendMessage(ChatColor.RED + "You need 20 vote points to spawn the wither.");
		return;
	    }
	} catch (SQLException | ClassNotFoundException ex) {
	    event.setCancelled(true);
	    lastWitherSkullPlacer.sendMessage(ChatColor.RED + "Could not spawn the wither due to a problem with your vote points.");
	    logger.log(Level.WARNING, "Could not remove votes from player: " + lastWitherSkullPlacer.getName() + " due to an SQL error.", ex);
	    return;
	}

	witherUtility.addWither((Wither) event.getEntity(), lastWitherSkullPlacer);
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
		lastWitherSkullPlacer = event.getPlayer();
		/* If the placed skull doesn't result in a wither spawn in the
		 next tick, reset the lastWitherSkullPlacer so the garbage
		 collector can clean up unused Player objects. */
		JavaPlugin plugin = witherUtility.getPlugin();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
		    lastWitherSkullPlacer = null;
		});
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
