package net.omniscimus.unknownutilities.utilities.wither;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.HashMap;
import java.util.UUID;
import net.omniscimus.universalvotes.UniversalVotes;
import net.omniscimus.universalvotes.VotesSQL;
import net.omniscimus.unknownutilities.ModuleException;
import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownUtility;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.plugin.Plugin;

/**
 * This module allows players to fight the wither in an arena.
 */
public class WitherUtility extends UnknownUtility {

    private final UnknownUtilities plugin;

    private ArenaManager arenaManager;
    private WitherListener listener;

    /* Wither's UUID, Player's UUID */
    private HashMap<UUID, UUID> withers;

    /**
     * Constructs the object.
     *
     * @param plugin the plugin instance
     */
    public WitherUtility(UnknownUtilities plugin) {
	this.plugin = plugin;
    }

    /**
     * Gets the plugin where this utility originates from.
     *
     * @return UnknownUtilities instance
     */
    public UnknownUtilities getPlugin() {
	return plugin;
    }

    public VotesSQL getVotesDatabase() {
	return ((UniversalVotes) plugin.getServer().getPluginManager().getPlugin("UniversalVotes"))
		.getVotesDatabase();
    }

    /**
     * Adds a new wither fight to the memory.
     *
     * @param wither the new wither to register
     * @param player the player who spawned this wither
     */
    public void registerWitherFight(Wither wither, Player player) {
	withers.put(wither.getUniqueId(), player.getUniqueId());
    }

    /**
     * Gets the player who spawned a wither.
     *
     * @param wither the wither whose spawner should be given
     * @return the spawner, or null if that player couldn't be found or if that
     * player is not online anymore
     */
    public Player getWitherSpawner(Wither wither) {
	UUID playerSpawner = withers.get(wither.getUniqueId());
	if (playerSpawner == null) {
	    return null;
	}
	OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerSpawner);
	if (player == null) {
	    return null;
	}
	return player.getPlayer();
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

	withers = new HashMap<>();

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
