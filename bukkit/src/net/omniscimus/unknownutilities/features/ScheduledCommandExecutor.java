package net.omniscimus.unknownutilities.features;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.scheduler.BukkitScheduler;

import net.omniscimus.unknownutilities.UnknownFeature;
import net.omniscimus.unknownutilities.UnknownUtilities;

public class ScheduledCommandExecutor extends UnknownFeature {

	private final UnknownUtilities plugin;

	private int taskId;
	private ScheduledFuture<?> midnightSchedule;
	
	@Override
	public boolean enable() {
		
		delayCommandsList = plugin.getConfig().getStringList("scheduledcommandexecutor.commands");
		interval = plugin.getConfig().getLong("scheduledcommandexecutor.interval");
		
		scheduler = plugin.getServer().getScheduler();
		taskId = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {

				executeCommand(delayCommandsList);

			}
		}, 0L, interval);
		
		plugin.getLogger().info("Delay commands enabled.");
		
		if(plugin.getConfig().getBoolean("scheduledcommandexecutor.midnight-commands-enabled") == true) {
			startExecutionAt(11, 55, 0, plugin.getConfig().getStringList("scheduledcommandexecutor.5minbeforemidnight-commands"));
			startExecutionAt(11, 59, 0, plugin.getConfig().getStringList("scheduledcommandexecutor.1minbeforemidnight-commands"));
			startExecutionAt(12, 00, 0, plugin.getConfig().getStringList("scheduledcommandexecutor.midnight-commands"));
			// If reloading the plugin, don't just call this method again! (it's recursive)
			plugin.getLogger().info("Midnight commands enabled.");
		}
		
		return true;
	}
	@Override
	public boolean disable() {
		scheduler.cancelTask(taskId);// Cancel repeating (broadcast) commands
		midnightSchedule.cancel(false);// Cancel midnight commands (recursive thing)
		return true;
	}
	
	private BukkitScheduler scheduler;
	private long interval;
	private List<String> delayCommandsList;
	
	public ScheduledCommandExecutor(UnknownUtilities plugin) {
		this.plugin = plugin;
		
		enable();
	}

	protected void executeCommand(List<String> list) {
		for(String str : list) {
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), str);
		}
	}

	
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	
	public void startExecutionAt(int targetHour, int targetMin, int targetSec, List<String> commandsList) {
		Runnable taskWrapper = new Runnable() {
			@Override
			public void run() {
				executeCommand(commandsList);
				startExecutionAt(targetHour, targetMin, targetSec - 1, commandsList);
			}
		};
		long delay = computNextDelay(targetHour, targetMin, targetSec);
		midnightSchedule = executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
	}

	private long computNextDelay(int targetHour, int targetMin, int targetSec) {
		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
		ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
		if(zonedNow.compareTo(zonedNextTarget) > 0)
			zonedNextTarget = zonedNextTarget.plusDays(1);

		Duration duration = Duration.between(zonedNow, zonedNextTarget);
		return duration.getSeconds();
	}

}
