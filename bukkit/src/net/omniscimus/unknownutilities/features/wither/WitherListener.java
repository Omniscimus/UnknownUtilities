package net.omniscimus.unknownutilities.features.wither;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.omniscimus.unknownutilities.UnknownUtilities;

public class WitherListener implements Listener {

	private UnknownUtilities plugin;
	
	private WitherLimiter witherLimiter;
	private ArenaManager arenaManager;

	protected WitherListener(UnknownUtilities plugin, WitherLimiter witherLimiter, ArenaManager arenaManager, WorldEditManager worldEditManager) {
		this.plugin = plugin;
		
		this.witherLimiter = witherLimiter;
		this.arenaManager = arenaManager;
		
		wreckedPlayers = new ArrayList<Integer>();
	}
	
	void disable() {
		PlayerEggThrowEvent.getHandlerList().unregister(this);
		BlockPlaceEvent.getHandlerList().unregister(this);
		CreatureSpawnEvent.getHandlerList().unregister(this);
		EntityDeathEvent.getHandlerList().unregister(this);
		PlayerDeathEvent.getHandlerList().unregister(this);
		PlayerRespawnEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
		PlayerTeleportEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
	}
	
	// Egg throw
	@EventHandler
	public void onEggThrow(PlayerEggThrowEvent event) {
		if(arenaManager.isInWitherRegion(event.getPlayer().getLocation())) {
			event.setHatching(false);
			event.getPlayer().sendMessage(ChatColor.RED + "Your chickens won't help you here!");
		}
	}

	// Necessary for knowing who spawned the Wither
	String lastPlayerWhoPlacedWitherSkull;
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().getType() == Material.SKULL_ITEM || event.getBlock().getType() == Material.SKULL) {
			lastPlayerWhoPlacedWitherSkull = event.getPlayer().getName();
		}
	}
	
	// Wither spawn
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {

		Entity entity = event.getEntity();
		if(entity.getType() == EntityType.WITHER) {
			Location loc = event.getLocation();
			
			// If it's not allowed to spawn the wither at this location {
			if(!arenaManager.isInWitherRegion(loc)) {
				event.setCancelled(true);

				// Send a message to nearby (<=5 blocks away) players, directing them to the correct location
				for(Player p : plugin.getServer().getOnlinePlayers()) {
					if(p.getLocation().distance(loc) <= 5) {
						p.sendMessage(ChatColor.RED + "You can only summon the wither in the wither arena!! Do /wither");
					}
				}
				return;

			}
			// Hasn't been returned yet

			arenaManager.registerNewWither(entity.getEntityId(), lastPlayerWhoPlacedWitherSkull);
			
			plugin.getServer().broadcastMessage(ChatColor.RED + plugin.getServer().getPlayer(lastPlayerWhoPlacedWitherSkull).getName() + ChatColor.GOLD + " spawned a Wither! Help by typing /wither arena (costs 30 votes), or watch by typing /wither spectate !");
			
			if(witherLimiter.getSQL() != null) {
				for(Player pl : plugin.getServer().getOnlinePlayers()) {
					if(arenaManager.isInWitherRegion(pl.getLocation())) {
						try {
							witherLimiter.getSQL().setWitherFights(pl.getName(), witherLimiter.getSQL().getWitherFights(pl.getName()) + 1);
						} catch (ClassNotFoundException | SQLException e) {
							System.out.println("Couldn't add a wither fight point in the database for player " + pl.getName() + "!!");
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	// Wither death
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		
		Entity entity = event.getEntity();
		if(entity.getType() == EntityType.WITHER) {
			event.setDroppedExp(0);
			event.getDrops().clear();
			
			EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
			if(lastDamageCause instanceof EntityDamageByEntityEvent) {
				
				if( ( (EntityDamageByEntityEvent)lastDamageCause ).getDamager() instanceof Player) {
					arenaManager.endFight(((Player)((EntityDamageByEntityEvent)lastDamageCause).getDamager()).getName(), entity.getEntityId());
				}
			}
			
		}

	}

	// Player death
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		// If he was inside the wither arena region, reset the arena and kill the wither, delete drops
		// Don't forget to check if there are still players, if they were with more than one, keep the wither
		// What to do with his stuff? Armor/potions/etc? Clear it

		Player player = event.getEntity();
		if(arenaManager.isInWitherRegion(player.getLocation())) {
			event.setKeepLevel(false);
			event.setDroppedExp(0);
			event.setKeepInventory(true);// maar clear hem vervolgens
			player.getInventory().clear();
			
			// He died, so he'll get teleported to the spawn automatically
			arenaManager.playerLeftArena(player.getName());

			// If he was the last survivor:
			if(!arenaManager.playersInArena()) {
				// Kill the wither
				arenaManager.endAllFights();

			} // Else: just go on with the fight. The killed player is gone and he lost his items.
			plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " was defeated by the Wither!");
			
			wreckedPlayers.add(player.getEntityId());
		}

	}
	List<Integer> wreckedPlayers;
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Integer playerID = event.getPlayer().getEntityId();
		if(wreckedPlayers.contains(playerID)) {
			event.setRespawnLocation(witherLimiter.getSpectatorLocation());
			wreckedPlayers.remove(playerID);
		}
	}

	// Player logout (combatlog / ragequit / ...)
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		// Don't delete all his items, just tele him to spawn.
		Player player = event.getPlayer();

		Location loc = player.getLocation();
		if(arenaManager.isInWitherRegion(loc)) {
			
			arenaManager.playerLeftArena(player.getName());
			player.teleport(witherLimiter.getSpectatorLocation());

			if(!arenaManager.playersInArena()) {
				// Kill the wither, because no players remain
				arenaManager.endAllFights();
			}
		}

	}

	// Player teleport (flee)
	protected void setTeleportAllowed(boolean value) {
		teleportAllowed = value;
	}
	private boolean teleportAllowed;
	@EventHandler
	public void onPlayer(PlayerTeleportEvent event) {
		// Fleeing from combat:
		// Don't delete all his items, but reset the arena if he was the last fighter.
		Player player = event.getPlayer();

		Location loc = player.getLocation();
		if(arenaManager.isInWitherRegion(loc)) {

			if(!arenaManager.playersInArena()) {
				arenaManager.endAllFights();
			}
		}

		// Teleporting into the arena:
		if(arenaManager.isInWitherRegion(event.getTo())) {
			if(!teleportAllowed) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "Please use /wither arena to get into the wither arena.");
			}
			else arenaManager.playerEnteredArena(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerHitWither(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if(entity.getType() == EntityType.WITHER && event.getDamager() instanceof Player) {
			arenaManager.playerHitWither(entity.getEntityId(), ((Player)event.getDamager()).getName());
		}
	}
	
}
