package net.omniscimus.unknownutilities.features.wither;

import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.omniscimus.universalvotes.UniversalVotes;
import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class WitherLimiter extends UnknownFeature {

	public boolean enable() {

		if(plugin.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
			
			// Instantiate external classes
			universalVotes = (UniversalVotes) plugin.getServer().getPluginManager().getPlugin("UniversalVotes");
			worldEditPlugin = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
			
			// Instantiate own classes
			arenaManager = new ArenaManager(plugin, this, worldEditManager);
			worldEditManager = new WorldEditManager(worldEditPlugin);
			witherListener = new WitherListener(plugin, this, arenaManager, worldEditManager);
			witherCommandExecutor = new WitherCommandExecutor(plugin, this, universalVotes, witherListener);
			
			Server server = plugin.getServer();
			FileConfiguration config = plugin.getConfig();
			this.arenaLocation = new Location(server.getWorld(config.getString("wither-arena.arena-location.world")), config.getInt("wither-arena.arena-location.X"), config.getInt("wither-arena.arena-location.Y"), config.getInt("wither-arena.arena-location.Z"));
			this.spectatorLocation = new Location(server.getWorld(config.getString("wither-arena.spectator-location.world")), config.getInt("wither-arena.spectator-location.X"), config.getInt("wither-arena.spectator-location.Y"), config.getInt("wither-arena.spectator-location.Z"));
			this.podiumLocation = new Location(server.getWorld(config.getString("wither-arena.podium-location.world")), config.getInt("wither-arena.podium-location.X"), config.getInt("wither-arena.podium-location.Y"), config.getInt("wither-arena.podium-location.Z"));
			this.pasteLocation = new Location(server.getWorld(config.getString("wither-arena.paste-location.world")), config.getInt("wither-arena.paste-location.X"), config.getInt("wither-arena.paste-location.Y"), config.getInt("wither-arena.paste-location.Z"));

			server.getPluginManager().registerEvents(witherListener, plugin);
			plugin.getCommand("wither").setExecutor(witherCommandExecutor);

			if(config.getBoolean("wither-arena.sql-database.enabled")) {
				try {
					sql = new SQL(plugin, config.getString("wither-arena.sql-database.hostname"), config.getString("wither-arena.sql-database.port"), config.getString("wither-arena.sql-database.database"), config.getString("wither-arena.sql-database.username"), config.getString("wither-arena.sql-database.password"));
				} catch (ClassNotFoundException | SQLException e) {
					plugin.getLogger().warning("Couldn't connect to the SQL server!!! Continuing without database...");
					e.printStackTrace();
					sql = null;
				}
			}
			return true;
		}
		else {
			plugin.getServer().getLogger().warning("WorldEdit not found! Disabling the Wither feature.");
			disable();
			return false;
		}

	}
	/**
	 * Important method for closing the SQL connection
	 * @return true if closing the connection succeeded.
	 */
	public boolean disable() {

		if(witherListener != null) witherListener.disable();
		if(arenaManager != null) arenaManager.disable();
		plugin.getCommand("wither").setExecutor(plugin);

		if(sql == null) return false;
		else if(sql.closeSQLConnection()) return true;
		else return false;
	}

	// External classes
	private UniversalVotes universalVotes;
	private WorldEditPlugin worldEditPlugin;

	private UnknownUtilities plugin;

	protected Location getArenaLocation() {return arenaLocation;}
	protected void setArenaLocation(Location loc) {arenaLocation = loc;}
	private Location arenaLocation;
	protected Location getSpectatorLocation() {return spectatorLocation;}
	protected void setSpectatorLocation(Location loc) {spectatorLocation = loc;}
	private Location spectatorLocation;
	protected Location getPodiumLocation() {return podiumLocation;}
	protected void setPodiumLocation(Location loc) {podiumLocation = loc;}
	private Location podiumLocation;
	protected Location getPasteLocation() {return pasteLocation;}
	protected void setPasteLocation(Location loc) {pasteLocation = loc;}
	private Location pasteLocation;

	// Own classes
	private ArenaManager arenaManager;
	public ArenaManager getArenaManager() { return arenaManager; }
	private WorldEditManager worldEditManager;
	public WorldEditManager getWorldEditManager() { return worldEditManager; }
	private WitherCommandExecutor witherCommandExecutor;
	private WitherListener witherListener;
	private SQL sql;
	SQL getSQL() {return sql;}

	public WitherLimiter(UnknownUtilities plugin) {

		this.plugin = plugin;

		enable();

	}

}
