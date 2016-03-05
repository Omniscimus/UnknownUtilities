package net.omniscimus.unknownutilities.features;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownFeature;
import org.bukkit.ChatColor;

public class SafeLogin extends UnknownFeature implements Listener {

	private UnknownUtilities plugin;

	@Override
	public boolean enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		return true;
	}
	@Override
	public boolean disable() {
		PlayerJoinEvent.getHandlerList().unregister(this);
		return true;
	}
	
	public SafeLogin(UnknownUtilities plugin) {
		this.plugin = plugin;
		
		enable();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		if(player.getGameMode() != GameMode.CREATIVE && !locationIsSafe(player.getEyeLocation())) {
			plugin.getServer().dispatchCommand(player, "spawn");
			player.sendMessage(ChatColor.RED + "You were teleported to the spawn because you logged in at an unsafe location.");
		}

	}

	private boolean locationIsSafe(Location locationAtEyeHeight) {

		Material eyeHeightBlockType = locationAtEyeHeight.getBlock().getType();

		if(eyeHeightBlockType == Material.AIR || eyeHeightBlockType == Material.ACACIA_DOOR || eyeHeightBlockType == Material.BIRCH_DOOR
				|| eyeHeightBlockType == Material.DARK_OAK_DOOR || eyeHeightBlockType == Material.IRON_DOOR_BLOCK
				|| eyeHeightBlockType == Material.JUNGLE_DOOR || eyeHeightBlockType == Material.SPRUCE_DOOR || eyeHeightBlockType == Material.LADDER
				|| eyeHeightBlockType == Material.SIGN || eyeHeightBlockType == Material.SIGN_POST || eyeHeightBlockType == Material.WOODEN_DOOR) {
			
			// Won't be suffocated, checking for lava under the player next (if it's above, he has time to run away)
			World world = locationAtEyeHeight.getWorld();
			int blockX = locationAtEyeHeight.getBlockX();
			int blockZ = locationAtEyeHeight.getBlockZ();
			
			for(int i = locationAtEyeHeight.getBlockY(); i >= 0; i--) {
				Material blockType = world.getBlockAt(blockX, i, blockZ).getType();
				if(blockType == Material.LAVA || blockType == Material.STATIONARY_LAVA) {
					return false;
				}
				if(blockType != Material.AIR) break;// Found a solid block beneath the player, doesn't matter if lava is beneath that. Is safe.
				if(locationAtEyeHeight.getBlockY() - i > 10) return false;// High fall
			}
			
			// No dangerous lava found, checking for the void next
			if(locationAtEyeHeight.getBlockY() < 0) return false;
			
			
			return true;// All checks returned safe
		}
		else return false;
	}
}
