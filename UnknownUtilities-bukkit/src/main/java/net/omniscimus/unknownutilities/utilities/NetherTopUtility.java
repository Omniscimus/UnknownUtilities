package net.omniscimus.unknownutilities.utilities;

import net.omniscimus.unknownutilities.ModuleException;
import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownUtility;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Prevents people from getting on the bedrock layer on top of the Nether.
 */
public class NetherTopUtility extends UnknownUtility implements Listener {

    private final UnknownUtilities plugin;

    /**
     * Constructs the object.
     *
     * @param plugin the plugin instance
     */
    public NetherTopUtility(UnknownUtilities plugin) {
	this.plugin = plugin;
    }

    @Override
    protected void enable() throws ModuleException {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected void disable() {
	PlayerMoveEvent.getHandlerList().unregister(this);
	PlayerTeleportEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
	if (isForbidden(event.getTo())) {
	    event.setCancelled(true);
	}
    }

    /**
     * Gets whether loc represents a location on top of the Nether.
     *
     * @param loc the location to check
     * @return true if the location is on top of the nether
     */
    private boolean isForbidden(Location loc) {
	return loc.getWorld().getEnvironment() == Environment.NETHER && loc.getBlockY() >= 128;
    }

}
