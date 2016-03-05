package net.omniscimus.unknownutilities.features.wither;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.omniscimus.universalvotes.UniversalVotes;
import net.omniscimus.universalvotes.database.Database;
import net.omniscimus.unknownutilities.UnknownUtilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class WitherCommandExecutor implements CommandExecutor {

	private final UnknownUtilities plugin;

	private final WitherLimiter witherLimiter;
	private final WitherListener witherListener;
	private final UniversalVotes universalVotes;

	protected WitherCommandExecutor(UnknownUtilities plugin, WitherLimiter witherLimiter, UniversalVotes universalVotes, WitherListener witherListener) {
		this.plugin = plugin;

		this.witherLimiter = witherLimiter;
		this.witherListener = witherListener;
		this.universalVotes = universalVotes;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		// Command = /wither because this class only gets passed that command

		// ==== Regular player commands ====

		if(sender.hasPermission("unknownutilities.wither") || sender.hasPermission("unknownutilities.wither.admin")) {

			// /wither
			if(args.length == 0) {
				Calendar now = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday", "Friday", "Saturday" };

				sender.sendMessage(ChatColor.RED + "======" + ChatColor.GOLD + " The Unknown Wither Fights " + ChatColor.RED + "======");
				sender.sendMessage(ChatColor.GRAY + "Today is " + days[now.get(Calendar.DAY_OF_WEEK) - 1] + ". Server time is " + sdf.format(new Date()));

				if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					sender.sendMessage(ChatColor.GOLD + "Fighting the Wither costs 30 vote points. To fight the wither, type " + ChatColor.RED + "/wither arena" + ChatColor.GOLD + ".");
				}
				else sender.sendMessage(ChatColor.GOLD + "Fighting the Wither costs 30 vote points. You can only fight the Wither on Saturdays.");
			}

			// /wither help
			else if(args[0].equalsIgnoreCase("help") && args.length == 1) {
				sender.sendMessage(ChatColor.GOLD + "\n--- Wither command help ---\n"
						+ ChatColor.RED + "/wither" + ChatColor.GOLD + ": displays basic information about the arena.\n"
						+ ChatColor.RED + "/wither arena" + ChatColor.GOLD + ": teleports you to the arena.\n"
						+ ChatColor.RED + "/wither spectate" + ChatColor.GOLD + ": teleports you to the spectator area.\n"
						+ ChatColor.RED + "/wither stats <player>" + ChatColor.GOLD + ": displays a player's Wither stats.\n"
						+ ChatColor.RED + "/wither top kills" + ChatColor.GOLD + ": lists the 5 players who killed the Wither the most times.\n"
						+ ChatColor.RED + "/wither top quickest" + ChatColor.GOLD + ": lists the 5 players who killed the Wither the quickest.\n"
						+ ChatColor.RED + "/wither top number" + ChatColor.GOLD + ": lists the 5 players who killed the Wither the most times."
						);
			}

			// /wither arena
			else if(args[0].equalsIgnoreCase("arena") && args.length == 1) {
				Calendar now = Calendar.getInstance();
				if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					Player player = (Player)sender;
					// Remove 30 vote points
					Database uvDatabase = universalVotes.getUniversalVotesDatabase();
					try {
						if(!uvDatabase.removeVotes(player.getName(), 30)) {
							player.sendMessage(ChatColor.GOLD + "You don't have sufficient vote points! You need 10 vote points for this. " + ChatColor.RED + "/vote");
							return true;
						}
					} catch (Exception e) {
						System.out.println("Couldn't get votes payment from player " + player.getName() + "!");
						player.sendMessage(ChatColor.RED + "There was an error getting your vote payment!");
						e.printStackTrace();
						return true;// Stop the process, don't teleport the player if he hasn't paid
					}
					// Teleport him to the arena and add him to the list of fighters. The toggle is because it'll be blocked by TeleportEvent
					witherListener.setTeleportAllowed(true);
					player.teleport(witherLimiter.getArenaLocation());
					witherListener.setTeleportAllowed(false);
				}
				else {
					sender.sendMessage(ChatColor.RED + "You can only fight the Wither on Saturdays!");
				}
			}

			// /wither spectate
			else if(args[0].equalsIgnoreCase("spectate") && args.length == 1) {
				Calendar now = Calendar.getInstance();
				if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					sender.sendMessage(ChatColor.GOLD + "Teleporting to the Wither arena spectator area.");
					((Player)sender).teleport(witherLimiter.getSpectatorLocation());
				}
				else {
					sender.sendMessage(ChatColor.RED + "The Wither can only be fought on Saturdays!");
				}
			}

			// /wither stats
			else if(args[0].equalsIgnoreCase("stats")) {
				if(witherLimiter.getSQL() != null) {
					if(args.length == 1) {
						// Lookup sender's wither stats
						try {
							String senderName = sender.getName();
							int numberAtOnce = witherLimiter.getSQL().getNumberOfWithersAtOnce(senderName);
							String numberAtOnceString = numberAtOnce == 1 ? " Wither at a time." : " Withers at a time.";
							sender.sendMessage(ChatColor.RED + "You" 
									+ ChatColor.GOLD + " have fought the Wither " 
									+ ChatColor.RED + witherLimiter.getSQL().getWitherFights(senderName) 
									+ ChatColor.GOLD + " times and won the fight " 
									+ ChatColor.RED + witherLimiter.getSQL().getWitherKills(senderName) 
									+ ChatColor.GOLD + " times.\nYour quickest Wither kill was in " 
									+ ChatColor.RED + witherLimiter.getSQL().getShortestWitherKillTime(senderName) / 1000
									+ ChatColor.GOLD + " seconds. You once fought "
									+ ChatColor.RED + numberAtOnce
									+ ChatColor.GOLD + numberAtOnceString
									);
						} catch (ClassNotFoundException | SQLException e) {
							sender.sendMessage(ChatColor.RED + "There was an error while getting your stats.");
							System.out.println("Couldn't query the wither stats for player " + sender.getName() + "!");
							e.printStackTrace();
						}
					}
					else if(args.length == 2) {
						// Lookup getPlayer(args[1])'s wither stats
						try {
							sender.sendMessage(ChatColor.RED + args[1] 
									+ ChatColor.GOLD + " has fought the Wither " 
									+ ChatColor.RED + witherLimiter.getSQL().getWitherFights(args[1]) 
									+ ChatColor.GOLD + " times and won the fight " 
									+ ChatColor.RED + witherLimiter.getSQL().getWitherKills(args[1]) 
									+ ChatColor.GOLD + " times.\nTheir quickest Wither kill was in "
									+ ChatColor.RED + witherLimiter.getSQL().getShortestWitherKillTime(args[1]) / 1000
									+ ChatColor.GOLD + " seconds. They once fought "
									+ ChatColor.RED + witherLimiter.getSQL().getNumberOfWithersAtOnce(args[1])
									+ ChatColor.GOLD + " Withers at a time."
									);
						} catch (ClassNotFoundException | SQLException e) {
							System.out.println("Couldn't query the wither stats for player " + args[1] + "!");
							e.printStackTrace();
						}
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "You can't view Wither stats at the moment. Please report this to the staff!");
				}
			}

			else if(args[0].equalsIgnoreCase("top")) {

				if(args.length == 1) {
					sender.sendMessage(ChatColor.GOLD + "Please specify a ranking type: most wither kills (kills), shortest kill time (quickest), or number of withers at once (number).");
				}

				if(args.length == 2) {

					if(args[1].equalsIgnoreCase("kills")) {
						List<Pair<UUID, Integer>> topMostWitherKills = null;
						try {
							topMostWitherKills = witherLimiter.getSQL().getTopMostWitherKills(5);
						} catch (ClassNotFoundException | SQLException e) {
							sender.sendMessage(ChatColor.RED + "Couldn't get the top 5 players.");
							e.printStackTrace();
						}
						if(topMostWitherKills != null) {
							sender.sendMessage(ChatColor.GOLD + "-- Busiest wither killers --");
							for(Pair<UUID, Integer> pair : topMostWitherKills) {
								sender.sendMessage(ChatColor.RED + Integer.toString((Integer)pair.o2) + " " + ChatColor.GOLD + plugin.getServer().getOfflinePlayer(((UUID)pair.o1)).getName());
							}
						}

					}
					else if(args[1].equalsIgnoreCase("quickest")) {

						List<Pair<UUID, Long>> topQuickestWitherKills = null;
						try {
							topQuickestWitherKills = witherLimiter.getSQL().getTopQuickestWitherKills(5);
						} catch(ClassNotFoundException | SQLException e) {
							sender.sendMessage(ChatColor.RED + "Couldn't get the top 5 players.");
							e.printStackTrace();
						}
						if(topQuickestWitherKills != null) {
							sender.sendMessage(ChatColor.GOLD + "-- Quickest wither killers --");
							for(Pair<UUID, Long> pair : topQuickestWitherKills) {
								sender.sendMessage(ChatColor.RED + "" + ((Long)pair.o2 / 1000) + " seconds " + ChatColor.GOLD + plugin.getServer().getOfflinePlayer(((UUID)pair.o1)).getName());
							}
						}

					}
					else if(args[1].equalsIgnoreCase("number")) {
						List<Pair<UUID, Integer>> topNumberOfWithers = null;
						try {
							topNumberOfWithers = witherLimiter.getSQL().getTopMostWithersAtOnce(5);
						} catch(ClassNotFoundException | SQLException e) {
							sender.sendMessage(ChatColor.RED + "Couldn't get the top 5 players.");
							e.printStackTrace();
						}
						if(topNumberOfWithers != null) {
							sender.sendMessage(ChatColor.GOLD + "-- Tank wither killers --");
							for(Pair<UUID, Integer> pair : topNumberOfWithers) {
								sender.sendMessage(ChatColor.RED + Integer.toString((Integer)pair.o2) + " " + ChatColor.GOLD + plugin.getServer().getOfflinePlayer(((UUID)pair.o1)).getName());
							}
						}
					}

				}

			}

			else {
				if(!sender.hasPermission("unknownutilities.wither.admin")) {
					sender.sendMessage(ChatColor.RED + "Wrong command syntax. Check /wither help");
				}
			}

		}

		// ==== Admin commands ====

		if(sender.hasPermission("unknownutilities.wither.admin")) {

			// /wither help
			if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("Admin commands:\n/wither setspawn\n/wither setspectator\n/wither setpodium\n/wither setpaste\n/wither setkills\n/wither setfights\n/wither paste");
			}

			// /wither setspawn
			if(args.length == 1 && args[0].equalsIgnoreCase("setspawn")) {
				Location newLocation = ((Player)sender).getLocation();
				witherLimiter.setArenaLocation(newLocation);
				FileConfiguration config = plugin.getConfig();
				config.set("wither-arena.arena-location.world", newLocation.getWorld().getName());
				config.set("wither-arena.arena-location.X", newLocation.getBlockX());
				config.set("wither-arena.arena-location.Y", newLocation.getBlockY());
				config.set("wither-arena.arena-location.Z", newLocation.getBlockZ());
				plugin.saveConfig();
				sender.sendMessage("Arena spawn set to your location.");
			}

			// /wither setspectator
			else if(args.length == 1 && args[0].equalsIgnoreCase("setspectator")) {
				Location newLocation = ((Player)sender).getLocation();
				witherLimiter.setSpectatorLocation(newLocation);
				FileConfiguration config = plugin.getConfig();
				config.set("wither-arena.spectator-location.world", newLocation.getWorld().getName());
				config.set("wither-arena.spectator-location.X", newLocation.getBlockX());
				config.set("wither-arena.spectator-location.Y", newLocation.getBlockY());
				config.set("wither-arena.spectator-location.Z", newLocation.getBlockZ());
				plugin.saveConfig();
				sender.sendMessage("Spectator spawn set to your location.");
			}

			// /wither setpodium
			else if(args.length == 1 && args[0].equalsIgnoreCase("setpodium")) {
				Location newLocation = ((Player)sender).getLocation();
				witherLimiter.setPodiumLocation(newLocation);
				FileConfiguration config = plugin.getConfig();
				config.set("wither-arena.podium-location.world", newLocation.getWorld().getName());
				config.set("wither-arena.podium-location.X", newLocation.getBlockX());
				config.set("wither-arena.podium-location.Y", newLocation.getBlockY());
				config.set("wither-arena.podium-location.Z", newLocation.getBlockZ());
				plugin.saveConfig();
				sender.sendMessage("Podium spawn set to your location.");
			}

			// /wither setpaste
			else if(args.length == 1 && args[0].equalsIgnoreCase("setpaste")) {
				Location newLocation = ((Player)sender).getLocation();
				witherLimiter.setPasteLocation(newLocation);
				FileConfiguration config = plugin.getConfig();
				config.set("wither-arena.paste-location.world", newLocation.getWorld().getName());
				config.set("wither-arena.paste-location.X", newLocation.getBlockX());
				config.set("wither-arena.paste-location.Y", newLocation.getBlockY());
				config.set("wither-arena.paste-location.Z", newLocation.getBlockZ());
				plugin.saveConfig();
				sender.sendMessage("Paste spawn set to your location.");
			}
			
			// /wither paste
			else if(args.length == 1 && args[0].equalsIgnoreCase("paste")) {
				sender.sendMessage("Pasting arena.schematic at paste location.");
				witherLimiter.getWorldEditManager().paste("witherForest", witherLimiter.getPasteLocation());
			}

			// /wither setkills <player> <number>
			else if(args.length == 3 && args[0].equalsIgnoreCase("setkills")) {
				if(witherLimiter.getSQL() != null) {
					try {
						witherLimiter.getSQL().setWitherKills(args[1], Integer.parseInt(args[2]));
						sender.sendMessage("Successfully set " + args[1] + "'s wither kills to " + args[2] + ".");
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Wrong command syntax.");
					} catch (SQLException | ClassNotFoundException e) {
						System.out.println("Couldn't query the wither stats for player " + args[1] + "!");
						e.printStackTrace();
					}
				}
				else sender.sendMessage("There was an error connecting to the SQL database!");
			}
			// /wither setfights <player> <number
			else if(args.length == 3 && args[0].equalsIgnoreCase("setfights")) {
				if(witherLimiter.getSQL() != null) {
					try {
						witherLimiter.getSQL().setWitherFights(args[1], Integer.parseInt(args[2]));
						sender.sendMessage("Successfully set " + args[1] + "'s wither fights to " + args[2] + ".");
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Wrong command syntax.");
					} catch (SQLException | ClassNotFoundException e) {
						System.out.println("Couldn't query the wither stats for player " + args[1] + "!");
						e.printStackTrace();
					}
				}
			}

		}

		return true;

	}

}
