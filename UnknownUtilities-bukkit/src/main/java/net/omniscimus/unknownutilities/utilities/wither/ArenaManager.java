package net.omniscimus.unknownutilities.utilities.wither;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;

/**
 * Manager for the location of the wither arena.
 */
public class ArenaManager {
    
    private static final Logger logger = Logger.getLogger(ArenaManager.class.getName());

    private final WitherUtility witherUtility;
    private final WorldEditPlugin worldEdit;
    private final WorldGuardPlugin worldGuard;

    /**
     * Constructs the object.
     *
     * @param witherUtility WitherUtility instance
     * @param worldEdit WorldEditPlugin instance
     * @param worldGuard WorldGuardPlugin instance
     */
    public ArenaManager(WitherUtility witherUtility, WorldEditPlugin worldEdit, WorldGuardPlugin worldGuard) {
	this.witherUtility = witherUtility;
	this.worldEdit = worldEdit;
	this.worldGuard = worldGuard;
    }

    /**
     * Gets whether the specified location is within the wither arena.
     *
     * @param loc the location to check for a wither arena region
     * @return if loc is in the wither arena. Returns false if the region
     * information couldn't be found
     */
    public boolean isInWitherArena(Location loc) {
	RegionManager rm = worldGuard.getRegionContainer().get(loc.getWorld());
	if (rm == null) {
	    return false;
	}
	return rm.getApplicableRegions(loc).getRegions().stream().anyMatch(
		(region) -> (region.getId().equalsIgnoreCase("wither")));
    }

    /**
     * Gets the location where the arena schematic should be pasted upon an
     * arena reset.
     *
     * @return the arena schematic paste location
     */
    private Location getPasteLocation() {
	Object setting = witherUtility.getSettings().get("paste-location");
	if (!(setting instanceof Location)) {
	    return null;
	}
	return (Location) setting;
    }

    /**
     * Resets the arena by pasting the WorldEdit schematic at the paste
     * location.
     */
    public void resetArena() {
	String schematicName = witherUtility.getSettings().getString("schematic");
	Location pasteLocation = getPasteLocation();
	if (schematicName == null || pasteLocation == null) {
	    logger.log(Level.WARNING, "Could not reset the arena because the plugin is configured improperly.");
	    return;
	}

	BukkitWorld world = new BukkitWorld(pasteLocation.getWorld());
	EditSession editSession = worldEdit.getWorldEdit().getEditSessionFactory()
		.getEditSession(world, Integer.MAX_VALUE);
	editSession.enableQueue();

	File schematicFile = new File(
		worldEdit.getDataFolder() + File.separator
		+ "schematics" + File.separator
		+ schematicName + ".schematic");
	SchematicFormat schematic = SchematicFormat.getFormat(schematicFile);
	CuboidClipboard clipboard;
	try {
	    clipboard = schematic.load(schematicFile);
	} catch (IOException ex) {
	    logger.log(Level.WARNING, "Could not reset the arena because WorldEdit couldn't read the data from schematic: " + schematicFile.getAbsolutePath(), ex);
	    return;
	} catch (DataException ex) {
	    logger.log(Level.WARNING, "Could not reset the arena because the schematic data isn't in the correct format: " + schematicFile.getAbsolutePath(), ex);
	    return;
	} finally {
	    editSession.flushQueue();
	}

	try {
	    clipboard.paste(editSession, BukkitUtil.toVector(pasteLocation), false);
	} catch (MaxChangedBlocksException ex) {
	    logger.log(Level.WARNING, "Could not reset the arena because the WorldEdit paste schematic is too large!", ex);
	} finally {
	    editSession.flushQueue();
	}
    }

}
