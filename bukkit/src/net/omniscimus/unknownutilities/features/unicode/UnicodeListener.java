package net.omniscimus.unknownutilities.features.unicode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class UnicodeListener implements Listener {

	void disable() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
		SignChangeEvent.getHandlerList().unregister(this);
	}
	
	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if(hasUnicodePermission(player)) event.setMessage(addUnicode(event.getMessage()));
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(hasUnicodePermission(player)) event.setMessage(addUnicode(event.getMessage()));
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if(hasUnicodePermission(player)) {
			String[] lines = event.getLines();
			for(int i = 0; i < lines.length; i++) {
				event.setLine(i, addUnicode(lines[i]));
			}
		}
	}

	/**
	 * Converts all keycodes in a String to the appropriate Unicode characters  
	 * @param message the String to convert
	 * @return the modified String
	 */
	private String addUnicode(String message) {
		// if someone says ;;<unicodeHex>, replace if not disallowed
		if(message.contains(";;")) {
			for(String str : message.split(" ")) {
				if(str.length() > 2 && str.contains(";;")) {
					// Get the substring, convert it into a hexidecimal int, then convert that int into a char
					try {
						char targetCharacter = (char)(Integer.parseInt(str.substring(2), 16));
						if(!UnicodeHandler.onDenyList(targetCharacter)) {
							message = message.replace(str, String.valueOf(targetCharacter));
						}
					} catch(NumberFormatException e) {
						
					}
				}
			}
		}
		// if someone says e.g. :smile:, replace
		for(String str : UnicodeHandler.CHARACTERS.keySet()) {
			if(message.contains(str)) message = message.replace(str, String.valueOf(UnicodeHandler.CHARACTERS.get(str)));
		}
		return message;
	}

	private boolean hasUnicodePermission(Player player) {
		if(player.hasPermission("unknownutilities.unicode")) return true;
		else return false;
	}

}
