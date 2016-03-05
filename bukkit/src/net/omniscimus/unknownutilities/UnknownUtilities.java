package net.omniscimus.unknownutilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.omniscimus.unknownutilities.features.BedExplosionPreventer;
import net.omniscimus.unknownutilities.features.CommandOverrider;
import net.omniscimus.unknownutilities.features.MaintenanceMode;
import net.omniscimus.unknownutilities.features.NetherTop;
import net.omniscimus.unknownutilities.features.PlainMessages;
import net.omniscimus.unknownutilities.features.PrefixChanger;
import net.omniscimus.unknownutilities.features.SafeLogin;
import net.omniscimus.unknownutilities.features.ScheduledCommandExecutor;
import net.omniscimus.unknownutilities.features.TabCompleteHider;
import net.omniscimus.unknownutilities.features.TimedPermissions;
import net.omniscimus.unknownutilities.features.unicode.UnicodeHandler;
import net.omniscimus.unknownutilities.features.wither.WitherLimiter;

public final class UnknownUtilities extends JavaPlugin {

	static Logger log;
	private FileConfiguration config;
	
	Map<String, UnknownFeature> enabledFeatures;
	
	private boolean unicodehandlerIsEnabled;
	private boolean witherlimiterIsEnabled;
	private boolean bedexplosionpreventerIsEnabled;
	private boolean commandoverriderIsEnabled;
	private boolean maintenancemodeIsEnabled;
	private boolean nethertopIsEnabled;
	private boolean plainmessagesIsEnabled;
	private boolean prefixchangerIsEnabled;
	private boolean safeloginIsEnabled;
	private boolean scheduledcommandexecutorIsEnabled;
	private boolean tabcompletehiderIsEnabled;
	private boolean timedpermissionsIsEnabled;
	
	private UnicodeHandler unicodeHandler;
	private WitherLimiter witherLimiter;
	private BedExplosionPreventer bedExplosionPreventer;
	private CommandOverrider commandOverrider;
	private MaintenanceMode maintenanceMode;
	private NetherTop netherTop;
	private PlainMessages plainMessages;
	private PrefixChanger prefixChanger;
	private SafeLogin safeLogin;
	private ScheduledCommandExecutor scheduledCommandExecutor;
	private TabCompleteHider tabCompleteHider;
	private TimedPermissions timedPermissions;
	
	@Override
	public void onEnable() {
		
		// Get the logger with which we can send info to the console
		log = getLogger();
		config = getConfig();
		// Save the config from the jar into /plugins is it isn't there yet
		saveDefaultConfig();
		// Load the variables from config.yml

		unicodehandlerIsEnabled = config.getBoolean("unicodehandler.enabled");
		witherlimiterIsEnabled = config.getBoolean("wither-arena.enabled");
		bedexplosionpreventerIsEnabled = config.getBoolean("bedexplosionpreventer.enabled");
		commandoverriderIsEnabled = config.getBoolean("commandoverrider.enabled");
		maintenancemodeIsEnabled = config.getBoolean("maintenancemode.enabled");
		nethertopIsEnabled = config.getBoolean("nethertop.enabled");
		plainmessagesIsEnabled = config.getBoolean("plainmessages.enabled");
		prefixchangerIsEnabled = config.getBoolean("prefixchanger.enabled");
		safeloginIsEnabled = config.getBoolean("safelogin.enabled");
		scheduledcommandexecutorIsEnabled = config.getBoolean("scheduledcommandexecutor.enabled");
		tabcompletehiderIsEnabled = config.getBoolean("tabcompletehider.enabled");
		timedpermissionsIsEnabled = config.getBoolean("timedpermissions.enabled");
		
		enabledFeatures = new HashMap<String, UnknownFeature>();
		
		if(unicodehandlerIsEnabled) {
			unicodeHandler = new UnicodeHandler(this);
			enabledFeatures.put("unicodehandler", unicodeHandler);
		}
		
		if(witherlimiterIsEnabled) {
			witherLimiter = new WitherLimiter(this);
			enabledFeatures.put("witherlimiter", witherLimiter);
		}
		
		if(bedexplosionpreventerIsEnabled) {
			bedExplosionPreventer = new BedExplosionPreventer(this);
			enabledFeatures.put("bedexplosionpreventer", bedExplosionPreventer);
		}
		
		if(commandoverriderIsEnabled) {
			commandOverrider = new CommandOverrider(this);
			enabledFeatures.put("commandoverrider", commandOverrider);
		}
		
		if(maintenancemodeIsEnabled) {
			maintenanceMode = new MaintenanceMode(this);
			enabledFeatures.put("maintenancemode", maintenanceMode);
		}
		
		if(nethertopIsEnabled) {
			netherTop = new NetherTop(this);
			enabledFeatures.put("nethertop", netherTop);
		}
		
		if(plainmessagesIsEnabled) {
			plainMessages = new PlainMessages(this);
			enabledFeatures.put("plainmessages", plainMessages);
		}
		
		if(prefixchangerIsEnabled) {
			prefixChanger = new PrefixChanger(this);
			enabledFeatures.put("prefixchanger", prefixChanger);
		}
		
		if(safeloginIsEnabled) {
			safeLogin = new SafeLogin(this);
			enabledFeatures.put("safelogin", safeLogin);
		}
		
		if(scheduledcommandexecutorIsEnabled) {
			scheduledCommandExecutor = new ScheduledCommandExecutor(this);
			enabledFeatures.put("scheduledcommandexecutor", scheduledCommandExecutor);
		}

		if(tabcompletehiderIsEnabled) {
			if(getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
				tabCompleteHider = new TabCompleteHider(this);
				enabledFeatures.put("tabcompletehider", tabCompleteHider);
			}
			else log.warning("Failed to find ProtocolLib; command tab completes will work.");
		}
		
		if(timedpermissionsIsEnabled) {
			timedPermissions = new TimedPermissions(this);
			enabledFeatures.put("timedpermissions", timedPermissions);
		}
		
	}

	@Override
	public void onDisable() {
		
		if(enabledFeatures.get("maintenancemode") != null) {
			maintenanceMode.disable();
		}

		if(enabledFeatures.get("witherlimiter") != null) {
			witherLimiter.disable();
		}
		
		if(enabledFeatures.get("timedpermissions") != null) {
			timedPermissions.disable();
		}
		
	}
	
	
	// Reload commands, etc.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("unknownutilities") || commandLabel.equalsIgnoreCase("uu")) {
			if(sender.hasPermission("unknownutilities.admin")) {
				if(args.length == 0 || (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))) ) {
					sender.sendMessage("-- UnknownUtilities help --\n/uu features\n/uu reload <feature>\n/uu enable <feature>\n/uu disable <feature>\n^ Enable and disable don't get stored in the config, so they'll happily start again on a restart.");
					return true;
				}
				else if(args.length == 1 && args[0].equalsIgnoreCase("features")) {
					sender.sendMessage("Enabled features:");
					for(String name : enabledFeatures.keySet()) {
						sender.sendMessage(name);
					}
					return true;
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("reload")) {
					for(Entry<String, UnknownFeature> entry : enabledFeatures.entrySet()) {
						if(entry.getKey().equals(args[1])) {
							entry.getValue().reload();
							return true;
						}
					}
					sender.sendMessage("Couldn't find that feature.");
					return true;
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("enable")) {
					for(Entry<String, UnknownFeature> entry : enabledFeatures.entrySet()) {
						if(entry.getKey().equals(args[1])) {
							entry.getValue().enable();
							return true;
						}
					}
					sender.sendMessage("Couldn't find that feature.");
					return true;
				}
				else if(args.length == 2 && args[0].equalsIgnoreCase("disable")) {
					for(Entry<String, UnknownFeature> entry : enabledFeatures.entrySet()) {
						if(entry.getKey().equals(args[1])) {
							entry.getValue().disable();
							return true;
						}
					}
					sender.sendMessage("Couldn't find that feature.");
					return true;
				}
				else {
					sender.sendMessage("Wrong syntax. /uu help");
					return true;
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "No permission.");
				return true;
			}
			
		}
		else {
			sender.sendMessage(ChatColor.RED + "This command has been disabled.");
			return true;
		}
		
	}
	
}
