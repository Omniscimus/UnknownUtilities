package net.omniscimus.unknownutilities.features;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class BedExplosionPreventer extends UnknownFeature implements Listener {

	private final UnknownUtilities plugin;
	
	@Override
	public boolean enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		return true;
	}
	@Override
	public boolean disable() {
		BlockPlaceEvent.getHandlerList().unregister(this);
		return true;
	}
	
	public BedExplosionPreventer(UnknownUtilities plugin) {
		this.plugin = plugin;
		enable();
	}
	
	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location location = block.getLocation();
		if(player.getWorld().getBiome(location.getBlockX(), location.getBlockZ())  == Biome.HELL) {
			if(block.getType() == Material.BED_BLOCK) event.setCancelled(true);
		}
		
	}
	
}
