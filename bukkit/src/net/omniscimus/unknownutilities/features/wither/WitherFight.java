package net.omniscimus.unknownutilities.features.wither;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.omniscimus.unknownutilities.UnknownUtilities;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class WitherFight {

	private List<String> participatingPlayers;
	void addPlayer(String playerName) {
		if(!hasPlayer(playerName)) {
			participatingPlayers.add(playerName);
		}
	}
	void removePlayer(String playerName) {
		if(hasPlayer(playerName)) participatingPlayers.remove(playerName);
	}
	boolean hasPlayer(String playerName) {
		if(participatingPlayers.contains(playerName)) {
			return true;
		}
		else return false;
	}

	private String witherSummoner;
	private long startTime;

	WitherFight(String witherSummoner) {
		this.participatingPlayers = new ArrayList<String>();
		this.witherSummoner = witherSummoner;
		startTime = System.currentTimeMillis();
	}

	long getElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}

	void end(String killer, UnknownUtilities plugin, WitherLimiter witherLimiter, ArenaManager arenaManager, WorldEditManager worldEditManager) {

		// Geef ze allemaal een puntje
		if(witherLimiter.getSQL() != null) {
			long finalElapsedTime = getElapsedTime();
			for(String playerName : participatingPlayers) {
				try {
					witherLimiter.getSQL().setWitherKills(playerName, witherLimiter.getSQL().getWitherKills(playerName) + 1);
				} catch (ClassNotFoundException | SQLException e) {
					System.out.println("Couldn't add a wither kill point in the database for player " + playerName + "!!");
					e.printStackTrace();
				}

				try {
					witherLimiter.getSQL().updateShortestWitherKillTime(playerName, finalElapsedTime);
				} catch(ClassNotFoundException | SQLException e) {
					System.out.println("Couldn't change the shortest wither kill time in the database for player " + playerName + "!!");
					e.printStackTrace();
				}

				try {
					witherLimiter.getSQL().updateNumberOfWithersAtOnce(playerName, arenaManager.getWithersAtOnce(playerName));
				} catch (ClassNotFoundException | SQLException e) {
					System.out.println("Couldn't change the most wither kills at once in the database for player " + playerName + "!!");
					e.printStackTrace();
				}
			}

		}
		if(killer != null) {
			Player summoner = plugin.getServer().getPlayer(witherSummoner);
			summoner.giveExp(50);
			Random random = new Random();
			if(random.nextInt(2) == 0) {
				// 50% 0 and 50% 1
				summoner.getInventory().addItem(new ItemStack(Material.NETHER_STAR, 1));
				plugin.getServer().broadcastMessage(ChatColor.RED + witherSummoner + ChatColor.GOLD + " got a Nether Star!!!");
			}
			else {
				summoner.getInventory().addItem(new ItemStack(Material.EMERALD, 5));
				plugin.getServer().broadcastMessage(ChatColor.GOLD + "Bad luck! " + ChatColor.RED + witherSummoner + ChatColor.GOLD + " didn't get a Nether Star.");
			}
			summoner.updateInventory();
		}
		// If no more withers remain, teleport the players onto the podium and shoutout.
		// Teleport the fighters to the podium, the others to the spectator area. (passive players in the arena)
		if(arenaManager.getNumberOfFights() == 1) {
			List<String> initialFights = new ArrayList<String>(participatingPlayers);
			if(killer != null) {
				Location podiumLocation = witherLimiter.getPodiumLocation();
				Location spectatorLocation = witherLimiter.getSpectatorLocation();
				StringBuilder victoryMessageBuilder = new StringBuilder().append(ChatColor.RED);

				// Teleport all players who participated in the fight to the podium.

				for(int i = 0; i < initialFights.size(); i++) {
					String playerName = initialFights.get(i);
					plugin.getServer().getPlayer(playerName).teleport(podiumLocation);
					arenaManager.playerLeftArena(playerName);
					victoryMessageBuilder.append(ChatColor.RED).append(playerName).append(ChatColor.GOLD);
					if(i < initialFights.size() - 2) {
						victoryMessageBuilder.append(", ");
					}
					else if(i == initialFights.size() - 2) victoryMessageBuilder.append(" and ");
				}
				// Teleport all passive players who remain in the arena to the spectator location.
				for(String playerName : arenaManager.getPlayersInArena().keySet()) {
					plugin.getServer().getPlayer(playerName).teleport(spectatorLocation);
				}

				if(initialFights.size() > 1) {
					victoryMessageBuilder.append(" have just defeated a Wither!");
				}
				else {
					victoryMessageBuilder.append(" has just defeated a Wither!");
				}
				plugin.getServer().broadcastMessage(victoryMessageBuilder.toString());
				// Fireworks
				Firework firework = (Firework) podiumLocation.getWorld().spawnEntity(podiumLocation, EntityType.FIREWORK);
				FireworkMeta fireworkMeta = firework.getFireworkMeta();
				fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).withColor(Color.GREEN).withFade(Color.AQUA).with(Type.BALL_LARGE).trail(true).build());
				fireworkMeta.setPower(2);
				firework.setFireworkMeta(fireworkMeta);
				
			}
			
			// Reset arena
			witherLimiter.getWorldEditManager().paste("witherForest", witherLimiter.getPasteLocation());
		}

	}

}
