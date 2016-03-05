package net.omniscimus.unknownutilities.features;

import java.lang.reflect.InvocationTargetException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownFeature;

public class TabCompleteHider extends UnknownFeature {

	private final UnknownUtilities plugin;
	
	@Override
	public boolean enable() {
		protocolManager.addPacketListener(new PacketAdapter(plugin, new PacketType[] { PacketType.Play.Client.TAB_COMPLETE }) {
			public void onPacketReceiving(PacketEvent event) {
				if ((event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) && 
						(((String)event.getPacket().getStrings().read(0)).startsWith("/")) && 
						(((String)event.getPacket().getStrings().read(0)).split(" ").length == 1)) {

					event.setCancelled(true);

					PacketContainer tabComplete = protocolManager.createPacket(PacketType.Play.Server.TAB_COMPLETE);
					tabComplete.getStringArrays().write(0, new String[0]);
					try {
						protocolManager.sendServerPacket(event.getPlayer(), tabComplete);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		});
		plugin.getLogger().info("TabCompleteHider is enabled.");
		return true;
	}
	@Override
	public boolean disable() {
		protocolManager.removePacketListeners(plugin);
		return true;
	}

	final ProtocolManager protocolManager;

	public TabCompleteHider(UnknownUtilities plugin) {
		this.plugin = plugin;
		protocolManager = ProtocolLibrary.getProtocolManager();
		enable();
	}

}
