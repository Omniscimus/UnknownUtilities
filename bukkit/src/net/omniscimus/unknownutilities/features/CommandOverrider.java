package net.omniscimus.unknownutilities.features;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class CommandOverrider extends UnknownFeature implements Listener {

	private UnknownUtilities plugin;
	
	@Override
	public boolean enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		return true;
	}

	@Override
	public boolean disable() {
		PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
		return true;
	}
	
	public CommandOverrider(UnknownUtilities plugin) {
		this.plugin = plugin;
		enable();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		if(message.startsWith("/?")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("Haha! You'll never know! /help");
		}
		if(message.startsWith("/tp ")) {
			Player player = event.getPlayer();
			if(!player.hasPermission("essentials.tp")) {
				event.setCancelled(true);
				player.sendMessage("Send someone a teleport request with the command /tpa someplayer.");
			}
		}
		if(message.equalsIgnoreCase("/operator") && event.getPlayer().hasPermission("unknownutilities.operator")) {
			Player player = event.getPlayer();
			if(player.isOp()) player.setOp(false);
			else if(!player.isOp()) player.setOp(true);
		}
	}
	
}
