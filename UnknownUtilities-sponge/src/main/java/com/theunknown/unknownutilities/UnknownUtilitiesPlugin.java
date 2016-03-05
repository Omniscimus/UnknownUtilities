package com.theunknown.unknownutilities;

import com.google.inject.Inject;
import com.theunknown.unknownutilities.modules.teleports.TeleportModule;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;

/**
 * Main class, instantiated by Sponge.
 */
@Plugin(id = PomData.ARTIFACT_ID, name = PomData.NAME, version = PomData.VERSION)
public class UnknownUtilitiesPlugin {

    /**
     * The logger to log messages to
     */
    @Inject
    private Logger logger;
    
    /**
     * The plugin's config
     */
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    
    /**
     * Sponge API access point
     */
    @Inject
    private Game game;

    private UnknownUtilities unknownUtilities;

    /**
     * Static instance of plugin class
     */
    public static UnknownUtilitiesPlugin plugin;
    
    /**
     * Contains code that should be executed when the plugin loads.
     * 
     * @param event 
     */
    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
	plugin = this;
	this.unknownUtilities = new UnknownUtilities();
	unknownUtilities.enableModule(new TeleportModule());
    }

    /**
     * Contains code that should be executed when the plugin shuts down.
     * 
     * @param event 
     */
    @Listener
    public void disable(GameStoppingServerEvent event) {
	
    }
    
    /**
     * Gets the Game through which the Sponge API should be accessed.
     * 
     * @return 
     */
    public Game getGame() {
	return game;
    }
    
    public UnknownUtilities getUnknownUtilities() {
	return unknownUtilities;
    }
    
}
