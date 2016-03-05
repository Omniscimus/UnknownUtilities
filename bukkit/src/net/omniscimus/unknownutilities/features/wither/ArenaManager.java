package net.omniscimus.unknownutilities.features.wither;

import java.util.HashMap;
import java.util.Map;

import net.omniscimus.unknownutilities.UnknownUtilities;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ArenaManager {

	void disable() {
		this.endAllFights();
	}
	
	private UnknownUtilities plugin;
	private WitherLimiter witherLimiter;
	private WorldEditManager worldEditManager;
	private WorldGuardPlugin worldGuardPlugin;
	
	// Don't use .getUniqueId() because it uses more memory and is only necessary if used across server restarts.
	private Map<String, Integer> playersInArena;// playerName, number of withers at the same time
	void playerEnteredArena(String playerName) {
		playersInArena.put(playerName, 0);
		witherFights.forEach((k, v) -> v.addPlayer(playerName));
	}
	void playerLeftArena(String playerName) {
		playersInArena.remove(playerName);
		witherFights.forEach((k, v) -> v.removePlayer(playerName));
	}
	Map<String, Integer> getPlayersInArena() {
		return playersInArena;
	}
	int getWithersAtOnce(String playerName) {
		if(playersInArena.get(playerName) == null) {
			return 0;
		}
		return playersInArena.get(playerName);
	}
	int getNumberOfPlayersInArena() {
		return playersInArena.size();
	}

	private Map<Integer, WitherFight> witherFights;
	void registerNewWither(int witherID, String summonerName) {
		witherFights.put(witherID, new WitherFight(summonerName));
	}
	void deRegisterWither(int witherID) {
		witherFights.forEach((k, v) -> {
			if(k == witherID) witherFights.remove(k);
		});
		
	}
	void endFight(String killer, int witherID) {
		witherFights.get(witherID).end(killer, plugin, witherLimiter, this, worldEditManager);
		// Kill the wither
		for(Entity e : witherLimiter.getArenaLocation().getWorld().getEntities()) {
			if(e.getEntityId() == witherID) {
				e.remove();
				break;
			}
		}
		witherFights.remove(witherID);
	}
	void playerHitWither(int witherID, String playerName) {
		WitherFight wf = witherFights.get(witherID);
		if(!wf.hasPlayer(playerName)) {
			wf.addPlayer(playerName);
			playersInArena.put(playerName, playersInArena.get(playerName) == null ? 0 : playersInArena.get(playerName) + 1);
		}
		return;
		
	}
	int getNumberOfFights() {
		return witherFights.size();
	}
	
	ArenaManager(UnknownUtilities plugin, WitherLimiter witherLimiter, WorldEditManager worldEditManager) {
		this.plugin = plugin;
		this.witherLimiter = witherLimiter;
		this.worldEditManager = worldEditManager;
		this.worldGuardPlugin = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		
		playersInArena = new HashMap<String, Integer>();
		witherFights = new HashMap<Integer, WitherFight>();
	}
	
	/**
	 * Checks if the given location is within a wither-arena flagged region
	 * @param loc the location to check
	 * @return true if the location is in a wither-arena regions
	 */
	public boolean isInWitherRegion(Location loc) {
		for(ProtectedRegion r : worldGuardPlugin.getRegionManager(loc.getWorld()).getApplicableRegions(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).getRegions()) {
			if(r.getId().equalsIgnoreCase("wither")) return true;
		}
		return false;
	}

	/**
	 * Checks if there are players in the wither arena at the specified location
	 * @param locationToCheck the location inside the wither arena which we should check for players
	 * @return true if there are still players in the arena
	 */
	boolean playersInArena() {
		if(playersInArena.size() == 0) return false;
		else return true;
		/*
		// Loop through the online players until we find someone in the wither region 
		for(Player pl : plugin.getServer().getOnlinePlayers()) {
			if(!pl.getUniqueId().equals(except.getUniqueId())) {
				Location location = pl.getLocation();
				if(getArenaRegion(locationToCheck).contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
					// Found a player in the region; so, there was still a player. The player wasn't the last survivor.
					return true;
				}
			}
		}
		return false;
		*/
	}
	
	/**
	 * Kills all Withers in the specified world.
	 * @param world in which withers should be killed.
	 */
	/*void killWithers(World world) {
		for(Entity entity : world.getEntities()) {
			if(entity.getType() == EntityType.WITHER) {
				entity.remove();
			}
		}
	}*/
	void endAllFights() {
		for(int i : witherFights.keySet()) {
			endFight(null, i);
		}
		for(String str : playersInArena.keySet()) {
			playerLeftArena(str);
		}
	}

	/**
	 * Gets the Wither arena at the specified location
	 * @param the location which should be checked for wither arena's
	 * @return null if there's no arena; otherwise the first wither-arena flagged region match
	 */
	/*
	private ProtectedRegion getArenaRegion(Location loc) {
		ApplicableRegionSet set = worldGuardPlugin.getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		// Filter out the first match in the regions which has the "wither-arena" flag 
		for(ProtectedRegion pr : set) {
			if(pr.getId().equalsIgnoreCase("wither")) {
				return pr;
			}
		}
		return null;
	}*/
	
}
