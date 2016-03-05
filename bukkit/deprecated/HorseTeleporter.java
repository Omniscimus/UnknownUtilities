package net.omniscimus.unknownutilities.features;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
import org.bukkit.event.player.PlayerTeleportEvent;

/*
 * Dit werkt niet vanwege een bug in Bukkit
 * vehicle.eject()					werkt niet
 * vehicle.teleport(event.getTo)	teleport de player niet correct op het paard, je zit er niet op maar bestuurt hem wel, maar je kan niet lopen
 * EntityUnleashEvent				doesn't get called
 */
//TODO @EventHandler
public class HorseTeleporter implements Listener {
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		Player player = event.getPlayer();
		
		Entity vehicle = player.getVehicle();
		if(vehicle == null) return;
		else {
			EntityType type = vehicle.getType();
			if(type == EntityType.HORSE || type == EntityType.PIG) {
				vehicle.eject();// Unmount the player, otherwise 1337 bugs
				vehicle.teleport(event.getTo());
			}
		}
		
	}
	
	public void onUnleash(EntityUnleashEvent event) {
		System.out.println("unleash");
		if(event.getReason() == UnleashReason.DISTANCE) System.out.println("distance");
		if(event.getReason() == UnleashReason.HOLDER_GONE) System.out.println("holder gone");
		if(event.getReason() == UnleashReason.PLAYER_UNLEASH) System.out.println("player unleash");
		if(event.getReason() == UnleashReason.UNKNOWN) System.out.println("unknown");
			Entity unleashedEntity = event.getEntity();
			if(unleashedEntity instanceof LivingEntity) {
				LivingEntity livingUnleashedEntity = (LivingEntity) unleashedEntity;
				Entity leashHolder = livingUnleashedEntity.getLeashHolder();
				// Teleport the entity to the player and leash it again
				unleashedEntity.teleport(leashHolder);
				livingUnleashedEntity.setLeashHolder(leashHolder);
			}
		
		
	}
	
}
