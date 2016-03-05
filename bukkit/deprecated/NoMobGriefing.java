package net.omniscimus.unknownutilities.features;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import net.omniscimus.unknownutilities.Main;
import net.omniscimus.unknownutilities.UnknownFeature;

public class NoMobGriefing extends UnknownFeature implements Listener {
	
	private Main plugin;
	
	NoMobGriefing(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		EntityType type = event.getEntityType();
		// Fireball is Ghast
		if(type == EntityType.PRIMED_TNT || type == EntityType.MINECART_TNT || type == EntityType.CREEPER || type == EntityType.FIREBALL) {
			if(!plugin.getWitherLimiter().getArenaManager().isInWitherRegion(event.getEntity().getLocation())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onZombieBreakIn(EntityBreakDoorEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEndermanBlockChange(EntityChangeBlockEvent event) {
		if(event.getEntityType() == EntityType.ENDERMAN) event.setCancelled(true);
	}
	
}
