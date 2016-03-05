package net.omniscimus.unknownutilities.features.unicode;

import com.google.common.collect.ImmutableMap;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class UnicodeHandler extends UnknownFeature {

	private UnknownUtilities plugin;
	private UnicodeListener unicodeListener;

	public UnicodeHandler(UnknownUtilities plugin) {
		this.plugin = plugin;
		unicodeListener = new UnicodeListener();
		enable();
		plugin.getLogger().info("UnicodeHandler is enabled.");
	}
	
	@Override
	public boolean enable() {
		plugin.getServer().getPluginManager().registerEvents(unicodeListener, plugin);
		return true;
	}
	@Override
	public boolean disable() {
		unicodeListener.disable();
		return true;
	}
	
	/**
	 * Adds custom unicode characters to the Minecraft jar.
	 */
	/*
	private static void modifyAllowedCharacters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = SharedConstants.class.getDeclaredField("allowedCharacters");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		char[] oldallowedchars = (char[]) field.get(null);
		
		List<Character> allowedCharsList = new ArrayList<Character>();
		for(char c : oldallowedchars) {
			allowedCharsList.add(c);
		}
		for(char c : CHARACTERS.values()) {
			allowedCharsList.add(c);
		}

		char[] newallowedchars = new char[allowedCharsList.size()];
		for(int i = 0; i < allowedCharsList.size(); i++) {
			newallowedchars[i] = allowedCharsList.get(i);
		}
		
		field.set(null, newallowedchars);
	}
	*/
	
	static final ImmutableMap<String, Character> CHARACTERS = ImmutableMap.<String, Character>builder()
			.put(":heart:", '❤')
			.put(":spade:", '♠')
			.put(":club:", '♣')
			.put(":diamond:", '♦')
			.put(":man:", '웃')
			.put(":peace:", '☮')
			.put(":nuclear:", '☢')
			.put(":skull:", '☠')
			.put(":check:", '✔')
			.put(":nope:", '✘')
			.put(":triangle:", '▲')
			.put(":note:", '♪')
			.put(":music:", '♫')
			.put(":plane:", '✈')
			.put(":poison:", '☣')
			.put(":smile:", 'ツ')
			.put(":snowman:", '☃')
			.put(":euro:", '€')
			.put(":pound:", '£')
			.put(":dollar:", '$')
			.put(":inf:", '∞')
			.put(":half:", '½')
			.put(":quarter:", '¼')
			.put(":sun:", '☼')
			.put(":cloud:", '☁')
			.put(":lightning:", 'ϟ')
			.put(":snow:", '❄')
			.put(":star:", '✯')
			.put(":cr:", '©')
			.put(":reg:", '®')
			.put(":tm:", '™')
			.put(":commu:", '☭')
			.put(":celcius:", '℃')
			.put(":fahren:", '℉')
			.put(":scissors:", '✂')
			.put(":up:", '↑')
			.put(":down:", '↓')
			.put(":right:", '→')
			.put(":left:", '←')
			.put(":flower:", '✿')
			.build();
	
	private static final char[] DENIEDCHARACTERS = {
			'♋',
			'卍',
			'卐',
			'⚣',
			'⚤',
			'⚧',
			'⚥',
			'⚦',
			'⚨',
			'⚩',
			'⚢'
	};
	static boolean onDenyList(char queriedChar) {
		for(char c : DENIEDCHARACTERS) {
			if(c == queriedChar) return true;
		}
		return false;
	}
	
}
