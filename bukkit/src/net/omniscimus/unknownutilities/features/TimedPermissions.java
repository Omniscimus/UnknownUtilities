package net.omniscimus.unknownutilities.features;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

/**
 * When you add a PEX timed permission, it's revoked when the server is
 * restarted. So, save the permissions to a file with the starting point and the
 * time, and re-add the permission if it hasn't expired yet.
 */
public class TimedPermissions extends UnknownFeature implements CommandExecutor {

    private final UnknownUtilities plugin;
    private File timedPermissionsFile;
    private FileConfiguration timedPermissionsConfig;

    @Override
    public boolean enable() {

	if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
	    plugin.getCommand("timedpermission").setExecutor(this);

	    try {
		if (getPermissionsConfig().getConfigurationSection("timedpermissions") != null) {
		    for (String playerKey : getPermissionsConfig().getConfigurationSection("timedpermissions").getKeys(false)) {
			if (getPermissionsConfig().getConfigurationSection("timedpermissions." + playerKey) != null) {
			    for (String permissionKey : getPermissionsConfig().getConfigurationSection("timedpermissions." + playerKey).getKeys(false)) {
				final long until = getPermissionsConfig().getLong("timedpermissions." + playerKey + "." + permissionKey);
				// if until is less than the time it's now
				if (until < System.currentTimeMillis()) {
				    // Purge: if the time has expired, remove from config
				    getPermissionsConfig().set("timedpermissions." + playerKey + "." + permissionKey, null);
				} else {
				    dispatchPermissionCommand(playerKey, permissionKey.replace('?', '.'), (int) ((until - System.currentTimeMillis()) / 1000));
				}
			    }
			}
		    }
		}
		savePermissionsConfig();
	    } catch (UnsupportedEncodingException e) {
		plugin.getLogger().warning("Couldn't get values from permissions config! Disabling the Timed Permissions!");
		disable();
		return false;
	    }
	    return true;
	} else {
	    plugin.getLogger().warning("PermissionsEx not found! Disabling TimedPermissions....");
	    disable();
	    return false;
	}
    }

    @Override
    public boolean disable() {
	plugin.getCommand("timedpermission").setExecutor(plugin);
	savePermissionsConfig();
	return true;
    }

    public TimedPermissions(UnknownUtilities plugin) {
	this.plugin = plugin;
	enable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		// /timedpermission Omniscimus essentials.back 86400
	if (sender.hasPermission("unknownutilities.plainmessage")) {
	    if (args.length == 3) {

		try {
		    int seconds = Integer.parseInt(args[2]);
		    addPermission(args[0], args[1], seconds);
		    sender.sendMessage("Player " + args[0] + " has been given the permission " + args[1] + " for " + args[2] + " seconds.");
		} catch (NumberFormatException e) {
		    sender.sendMessage("Wrong command syntax.");
		} catch (UnsupportedEncodingException e) {
		    sender.sendMessage("Couldn't add that permission!");
		    e.printStackTrace();
		}

	    } else {
		sender.sendMessage("Wrong command syntax. /timedpermission <player> <permission> <seconds>");
	    }
	    return true;
	} else {
	    sender.sendMessage(ChatColor.RED + "No permission.");
	}
	return true;

    }

    private void addPermission(String playerName, String permission, int seconds) throws UnsupportedEncodingException {

	final String untilPath = "timedpermissions." + playerName + "." + permission.replace('.', '?');
	final long newUntil = System.currentTimeMillis() + seconds * 1000L;

	if (newUntil > getPermissionsConfig().getLong(untilPath)) {
	    dispatchPermissionCommand(playerName, permission, seconds);
	    getPermissionsConfig().set(untilPath, newUntil);
	    savePermissionsConfig();
	}
	// Else there's no point in adding the permission; the player already had that permission for a longer amount of time.

    }

    private void dispatchPermissionCommand(String playerName, String permission, int seconds) {
	Server server = plugin.getServer();
	server.dispatchCommand(server.getConsoleSender(), "pex user " + playerName + " timed add " + permission + " " + seconds);
    }

    // -------- timedpermissions.yml config ----------
    private static final String PERMISSIONSFILESTRING = "timedpermissions.yml";
    private static final String UTF = "UTF8";
    private static final String COULDNOTSAVE = "Could not save config to ";

    private void reloadPermissionsConfig() throws UnsupportedEncodingException {
	if (timedPermissionsFile == null) {
	    timedPermissionsFile = new File(plugin.getDataFolder(), PERMISSIONSFILESTRING);
	}
	timedPermissionsConfig = YamlConfiguration.loadConfiguration(timedPermissionsFile);

	// Look for defaults in the jar
	Reader defConfigStream = new InputStreamReader(plugin.getResource(PERMISSIONSFILESTRING), UTF);
	YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	timedPermissionsConfig.setDefaults(defConfig);
    }

    private FileConfiguration getPermissionsConfig() throws UnsupportedEncodingException {
	if (timedPermissionsConfig == null) {
	    reloadPermissionsConfig();
	}
	return timedPermissionsConfig;
    }

    private void savePermissionsConfig() {
	if (timedPermissionsConfig == null || timedPermissionsFile == null) {
	    return;
	}
	try {
	    getPermissionsConfig().save(timedPermissionsFile);
	} catch (IOException e) {
	    plugin.getLogger().severe(COULDNOTSAVE + timedPermissionsFile);
	    e.printStackTrace();
	}
    }

}
