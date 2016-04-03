package net.omniscimus.unknownutilities.utilities.wither;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.ArrayList;
import java.util.UUID;
import net.omniscimus.unknownutilities.ModuleException;
import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownUtility;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * This module allows players to fight the wither in an arena.
 */
public class WitherUtility extends UnknownUtility {

    private final UnknownUtilities plugin;

    private ArenaManager arenaManager;
    private WitherListener listener;

    private ArrayList<UUID> fightingPlayers;

    /**
     * Constructs the object.
     *
     * @param plugin the plugin instance
     */
    public WitherUtility(UnknownUtilities plugin) {
	this.plugin = plugin;
    }

    /**
     * Adds a player to the list of players who are currently fighting the
     * Wither in the arena.
     *
     * @param player the player who started fighting the wither
     */
    public void addFightingPlayer(Player player) {
	fightingPlayers.add(player.getUniqueId());
    }
    
    /**
     * Removes a player from the list of players who are currently fighting the
     * Wither in the arena.
     *
     * @param player the player who stopped fighting the wither
     */
    public void removeFightingPlayer(Player player) {
	fightingPlayers.remove(player.getUniqueId());
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

	fightingPlayers = new ArrayList<>();

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
