package net.omniscimus.unknownutilities.utilities.wither;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
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

	witherUtility.registerWitherFight((Wither) event.getEntity(), lastWitherSkullPlacer);
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
     * Called by Bukkit whenever an entity dies. Handles rewards when a Wither
     * dies, and resets the arena afterwards.
     *
     * @param event the event that occurred
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
	LivingEntity entity = event.getEntity();
	if (entity.getType() != EntityType.WITHER) {
	    return;
	}

	event.setDroppedExp(0);
	event.getDrops().clear();

	Player playerSpawner = witherUtility.getWitherSpawner((Wither) entity);
	if (playerSpawner == null) {
	    return;
	}
	playerSpawner.giveExp(50);
	giveNetherStar(playerSpawner);

	witherUtility.getArenaManager().resetArena();

	// TODO command for setting the arena's schematic paste location
	// TODO teleport away the player to avoid suffocation when resetting the arena
	// TODO do only permit one wither fight at a time
	// TODO unregister the wither fight
	// TODO if someone spawns a wither and then just walks away (or dies),
	//      despawn the wither after some time (and unregister the fight)
	// TODO do not allow spamming egg throws to distract the Wither
    }

    /**
     * Gives the specified player a Nether Star. If there's not enough space in
     * the player's inventory or ender chest, this method will recurse with a
     * delay of one minute, until the player quits the server.
     *
     * @param toPlayer the player to reward with a Nether Star
     */
    private void giveNetherStar(Player toPlayer) {
	if (toPlayer == null || !toPlayer.isOnline()) {
	    return;
	}

	Map<Integer, ItemStack> remaining = toPlayer.getInventory()
		.addItem(new ItemStack(Material.NETHER_STAR, 1));
	if (!remaining.isEmpty()) {
	    remaining = toPlayer.getEnderChest().addItem(remaining.values().toArray(new ItemStack[1]));
	    if (!remaining.isEmpty()) {
		toPlayer.sendMessage(ChatColor.RED + "You don't have enough space in your inventory or ender chest for a Nether Star! Retrying in a minute...");
		JavaPlugin plugin = witherUtility.getPlugin();
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
		    giveNetherStar(toPlayer);
		}, 20 * 60);
	    } else {
		toPlayer.sendMessage(ChatColor.GOLD + "Congratulations! A Nether Star has been added to your ender chest.");
	    }
	} else {
	    toPlayer.sendMessage(ChatColor.GOLD + "Congratulations! A Nether Star has been added to your inventory.");
	}
    }

    /**
     * Unregisters this Listener from all events.
     */
    public void disable() {
	CreatureSpawnEvent.getHandlerList().unregister(this);
	BlockPlaceEvent.getHandlerList().unregister(this);
	EntityDeathEvent.getHandlerList().unregister(this);
    }

}
