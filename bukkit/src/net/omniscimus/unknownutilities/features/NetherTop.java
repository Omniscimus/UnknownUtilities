package net.omniscimus.unknownutilities.features;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

/**
 * Prevents people from glitching on top of the Nether
 */
public class NetherTop extends UnknownFeature implements Listener {

	private final UnknownUtilities plugin;
	
	@Override
	public boolean enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		return true;
	}
	@Override
	public boolean disable() {
		PlayerMoveEvent.getHandlerList().unregister(this);
		PlayerTeleportEvent.getHandlerList().unregister(this);
		return true;
	}
	
	public NetherTop(UnknownUtilities plugin) {
		this.plugin = plugin;
		enable();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		
		if(isForbidden(event.getTo())) event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		if(isForbidden(event.getTo())) event.setCancelled(true);
		
	}
	
	private boolean isForbidden(Location loc) {
		if(loc.getWorld().getEnvironment() == Environment.NETHER && loc.getBlockY() >= 128) {
			return true;
		}
		else return false;
	}
	
}
