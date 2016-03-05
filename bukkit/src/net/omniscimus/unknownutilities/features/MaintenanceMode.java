package net.omniscimus.unknownutilities.features;

import java.util.Collection;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class MaintenanceMode extends UnknownFeature implements Listener,CommandExecutor {

	@Override
	public boolean enable() {
		setMaintenanceState(plugin.getConfig().getBoolean("maintenancemode.running"));
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getCommand("maintenance").setExecutor(this);
		
		Logger logger = plugin.getLogger();
		if(getMaintenanceState()) {
			logger.info("Maintenance Mode is Running!! You can stop it using /maintenance off");
		}
		else logger.info("/maintenance features enabled. You can start maintenance mode with the command /maintenance on");
		
		return true;
	}
	@Override
	public boolean disable() {
		ServerListPingEvent.getHandlerList().unregister(this);
		
		plugin.getCommand("maintenance").setExecutor(plugin);
		
		FileConfiguration config = plugin.getConfig();
		if(getMaintenanceState()) config.set("maintenancemode.running", true);
		else config.set("maintenancemode.running", false);
		return true;
	}
	
	private UnknownUtilities plugin;

	private boolean maintenanceState;// true = aan; false = uit

	public MaintenanceMode(UnknownUtilities plugin) {
		this.plugin = plugin;
		
		enable();
	}

	public void setMaintenanceState(boolean state) {
		this.maintenanceState = state;
		plugin.getServer().setWhitelist(state);
		if(maintenanceState) {
			Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
			for(Player player : players) {
				if(!player.isWhitelisted()) player.kickPlayer("The server has gone in Maintenance Mode to install some updates. We'll be back soon!");
			}
			plugin.getServer().broadcastMessage("Motd has changed, whitelist is enabled. Happy updating.");
		}
		else {
			plugin.getServer().broadcastMessage("Motd has changed back, whitelist is disabled. Hope all went well!");
		}
	}
	public boolean getMaintenanceState() {
		return maintenanceState;
	}

	@EventHandler
	public void onServerListPingEvent(ServerListPingEvent event) {
		if(maintenanceState) {
			event.setMotd(event.getMotd() + "\nThe Unknown is currently in maintenance mode.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(!(sender instanceof Player) || sender.hasPermission("unknownutilities.maintenance")) {
				if(args.length > 1) {
					sender.sendMessage("Wrong syntax. Use either /maintenance on, or /maintenance off");
				}
				else if(args.length == 0) {
					if(getMaintenanceState()) sender.sendMessage("Maintenance Mode is currently running.");
					else sender.sendMessage("Maintenance Mode is currently not running.");
					sender.sendMessage("Use /maintenance on, or /maintenance off");
				}
				else if(args[0].equalsIgnoreCase("on")) {
					if(getMaintenanceState()) sender.sendMessage("Maintenance Mode is already enabled!");
					else setMaintenanceState(true);// change motd and enable whitelist
				}
				else if(args[0].equalsIgnoreCase("off")) {
					if(!getMaintenanceState()) sender.sendMessage("Maintenance Mode is already disabled!");
					else setMaintenanceState(false);// change motd back and disable whitelist
				}
				else {
					sender.sendMessage("Wrong syntax. Use either /maintenance on, or /maintenance off");
				}
		}
		else sender.sendMessage("You don't have permission.");
		return true;

	}
	
}