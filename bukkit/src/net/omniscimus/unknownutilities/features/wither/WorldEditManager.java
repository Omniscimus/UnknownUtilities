package net.omniscimus.unknownutilities.features.wither;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

@SuppressWarnings("deprecation")
public class WorldEditManager {

	private WorldEditPlugin worldEditPlugin;

	private WorldEdit worldEditAP;

	WorldEditManager(WorldEditPlugin worldEditPlugin) {
		this.worldEditPlugin = worldEditPlugin;

		worldEditAP = worldEditPlugin.getWorldEdit();
	}

	public void paste(String schematicName, Location pasteLoc) {
		
		try {
			File file = new File(worldEditPlugin.getDataFolder() + File.separator + "schematics" + File.separator + schematicName + ".schematic");
			System.out.println("Loading schematic " + file.getAbsolutePath());
			EditSession editSession = worldEditAP.getEditSessionFactory().getEditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
			editSession.enableQueue();

			SchematicFormat schematic = SchematicFormat.getFormat(file);
			CuboidClipboard clipboard = schematic.load(file);

			clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), false);
			editSession.flushQueue();
		} catch (DataException | IOException ex) {
			ex.printStackTrace();
		} catch (MaxChangedBlocksException ex) {
			ex.printStackTrace();
		}
	}

}
