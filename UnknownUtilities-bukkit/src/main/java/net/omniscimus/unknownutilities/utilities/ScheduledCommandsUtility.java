package net.omniscimus.unknownutilities.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.CalendarIntervalScheduleBuilder;
import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.Server;

import net.omniscimus.unknownutilities.UnknownUtilities;
import net.omniscimus.unknownutilities.UnknownUtility;

/**
 * This module provides functionality for executing commands in the Minecraft
 * server at specified times.
 */
public class ScheduledCommandsUtility extends UnknownUtility {

    private static final Logger logger = Logger.getLogger(ScheduledCommandsUtility.class.getName());

    private final transient UnknownUtilities plugin;
    private transient Scheduler scheduler;

    /**
     * Constructs the object.
     *
     * @param plugin the plugin instance
     */
    public ScheduledCommandsUtility(UnknownUtilities plugin) {
	this.plugin = plugin;
    }

    @Override
    public void enable() {
	try {
	    scheduler = new StdSchedulerFactory().getScheduler();
	    scheduler.start();
	} catch (SchedulerException ex) {
	    logger.log(Level.WARNING, "Could not start the command scheduler", ex);
	    plugin.disableModule(ScheduledCommandsUtility.class);
	}

	ConfigurationSection commandSettings = plugin.getSettings().getModuleSettings(this);
	List<String> weekCommands = commandSettings.getStringList("week-based");
	weekCommands.stream().forEach((commandConfig) -> {
	    try {
		ScheduledCommandFactory.weekCommandFromConfig(commandConfig).schedule(scheduler);
	    } catch (InvalidConfigurationException ex) {
		logger.log(Level.WARNING, "Found invalid week configuration: " + commandConfig, ex);
	    } catch (SchedulerException ex) {
		logger.log(Level.WARNING, "Error scheduling week command: " + commandConfig, ex);
	    }
	});

	//List<String> monthCommands = commandSettings.getStringList("month-based"); // TODO
    }

    @Override
    public void disable() {
	try {
	    scheduler.clear();
	} catch (SchedulerException ex) {
	    logger.log(Level.WARNING, "Error clearing scheduler", ex);
	}
    }

    /**
     * Executes the given server command. Uses the ConsoleCommandSender as the
     * CommandSender.
     *
     * @param command the command to execute
     */
    static synchronized void executeCommand(String command) {
	logger.log(Level.INFO, "Executing command: {0}", command);
	Server server = UnknownUtilities.inst().getServer();
	server.dispatchCommand(server.getConsoleSender(), command);
    }

}

/**
 * Factory class for ScheduledCommand instances.
 */
class ScheduledCommandFactory {

    private static final List<Character> WHITESPACES;

    static {
	Character[] whiteSpaces = {
	    '\u0009', '\n', '\u000B', '\u000C', '\r', '\u0020', '\u0085',
	    '\u00A0', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003',
	    '\u2005', '\u2006', '\u2007', '\u2008', '\u2009', '\u200A',
	    '\u2028', '\u2029', '\u202F', '\u205F', '\u3000'
	};
	WHITESPACES = Arrays.asList(whiteSpaces);
    }

    public static ScheduledWeekCommand weekCommandFromConfig(String configString) throws InvalidConfigurationException {
	ArrayList<String> configElements = getWords(configString, 6);
	if (configElements.size() != 7) {
	    throw new InvalidConfigurationException("Couldn't parse scheduled command:\n" + configString + "\nNot enough arguments.");
	}

	int second, minute, hour, day, week, year;
	String command;

	try {
	    second = parseTimeElement(configElements.get(0));
	    minute = parseTimeElement(configElements.get(1));
	    hour = parseTimeElement(configElements.get(2));
	    day = parseTimeElement(configElements.get(3));
	    week = parseTimeElement(configElements.get(4));
	    year = parseTimeElement(configElements.get(5));
	    command = configElements.get(6);
	} catch (NumberFormatException ex) {
	    throw new InvalidConfigurationException("Couldn't parse scheduled command:\n" + configString + "\nIncorrect arguments.");
	}

	return new ScheduledWeekCommand(command, second, minute, hour, day, week, year);
    }

    /**
     * Gets the time element from a string. Will return -1 if the element is
     * "*".
     *
     * @param element the time element to parse
     * @return the parsed time element
     */
    private static int parseTimeElement(String element) {
	if (element.equals("*")) {
	    return -1;
	} else {
	    return Integer.parseUnsignedInt(element);
	}
    }

    /**
     * Gets the first word from a string. A word is a group of non-space
     * characters surrounded by space characters.
     *
     * @param chars the string to get words out of
     * @param max the max amount of words that can be in the string; the
     * remainder will be left as a string as the last entry of the returned list
     * @return a list of words found in the string
     */
    private static ArrayList<String> getWords(String str, int max) {
	if (max < 1) {
	    throw new IllegalArgumentException();
	}
	ArrayList<String> words = new ArrayList<>();
	char[] chars = str.toCharArray();
	int nextNonSpace, nextSpace = 0;
	do {
	    nextNonSpace = getFirstNonSpace(chars, nextSpace);
	    nextSpace = getFirstSpace(chars, nextNonSpace);
	    char[] wordChars;
	    if (nextSpace != -1) {
		wordChars = Arrays.copyOfRange(chars, nextNonSpace, nextSpace);
	    } else {
		wordChars = Arrays.copyOfRange(chars, nextNonSpace, chars.length);
	    }
	    String word = String.valueOf(wordChars);
	    words.add(word);
	} while (nextNonSpace != -1 && words.size() < max);
	words.add(String.valueOf(Arrays.copyOfRange(chars, nextSpace, chars.length)));
	return words;
    }

    /**
     * Gets the first non-space character in a string.
     *
     * @param str the string to search, in char[] format
     * @param offset the offset from where the char[] should be searched
     * @return the index of the first non-space character, or -1 if none was
     * found
     */
    private static int getFirstNonSpace(char[] str, int offset) {
	for (int i = offset; i < str.length; i++) {
	    if (!WHITESPACES.contains(str[i])) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Gets the first space character in a string.
     *
     * @param str the string to search, in char[] format
     * @param offset the offset from where the char[] should be searched
     * @return the index of the first space character, or -1 if none was found
     */
    private static int getFirstSpace(char[] str, int offset) {
	for (int i = offset; i < str.length; i++) {
	    if (WHITESPACES.contains(str[i])) {
		return i;
	    }
	}
	return -1;
    }
}

/**
 * Represents a console command that can be scheduled in the future.
 */
abstract class ScheduledCommand {

    /**
     * The command string that should be executed.
     */
    protected final String command;

    /**
     * Creates the object.
     *
     * @param command the command that should be executed
     */
    public ScheduledCommand(String command) {
	this.command = command;
    }

    /**
     * Schedules this command for execution.
     *
     * @param Scheduler the scheduler to schedule this command on
     */
    public abstract void schedule(Scheduler scheduler) throws SchedulerException;

    @Override
    public String toString() {
	return "The command '" + command + "', "
		+ "to be executed for the first time at " + getTime();
    }

    /**
     * Gets a human-friendly representation of the time at which this command
     * will execute for the first time.
     *
     * @return the time of the first execution, in readable format
     */
    protected abstract String getTime();

    /**
     * Calculates the date of first execution and the time between executions,
     * then creates a Trigger with those values.
     *
     * @param dateFormat a string for SimpleDateFormat containing the formatting
     * of the date
     * @param dateString a string containing the date, following the specified
     * format
     * @param interval the number of seconds that should be in between each
     * execution
     * @return the created trigger
     * @throws ParseException if the date could not be parsed
     */
    Trigger createTrigger(String dateFormat, String dateString, int interval) throws ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	Date date = sdf.parse(dateString);

	CalendarIntervalScheduleBuilder cisb = calendarIntervalSchedule()
		.withIntervalInSeconds(interval)
		.withMisfireHandlingInstructionDoNothing();
	if (interval != 0) {
	    cisb.withIntervalInSeconds(interval);
	}
	return newTrigger()
		.withSchedule(cisb)
		.startAt(date)
		.build();
    }

    /**
     * Returns value, if i is not 0; returns i otherwise
     *
     * @param i the int to return if it is not 0
     * @param value the int to return if i is 0
     */
    int setIntOnce(int i, int value) {
	if (i == 0) {
	    return value;
	} else {
	    return i;
	}
    }

}

/**
 * Represents a command scheduled to execute on times formatted with weeks in
 * mind.
 */
class ScheduledWeekCommand extends ScheduledCommand {

    private static final Logger logger = Logger.getLogger(ScheduledWeekCommand.class.getName());

    private final int[] time;

    /**
     * Creates the object.
     *
     * @param command the command that should be executed
     * @param second the second of the minute on which the command should be
     * executed
     * @param minute the minute of the hour on which the command should be
     * executed
     * @param hour the hour of the day on which the command should be executed,
     * ranging from 0 to 23
     * @param day the day of the week, starting at 1 for Monday, on which the
     * command should be executed
     * @param week The week of the year in which this command should be
     * executed, ranging from 1 to 53
     * @param year the year in which this command should execute. Note that this
     * will break in the year 2147483648 due to the limitations to an Integer
     */
    protected ScheduledWeekCommand(String command, int second, int minute,
	    int hour, int day, int week, int year) {

	super(command);
	time = new int[]{second, minute, hour, day, week, year};
    }

    @Override
    public String getTime() {
	return time[2] + ":" + time[1] + ":" + time[0] + " on day "
		+ time[3] + " in week " + time[4] + " of "
		+ time[5];
    }

    @Override
    public void schedule(Scheduler scheduler) throws SchedulerException {
	HashMap<String, String> dataMap = new HashMap<>();
	dataMap.put("command", this.command);

	JobDetail job = newJob(CommandJob.class)
		.setJobData(new JobDataMap(dataMap))
		.withDescription(this.toString())
		.build();

	StringBuilder dateStringBuilder = new StringBuilder();
	int repeatInSeconds = 0;

	for (int i = 0; i < time.length; i++) {
	    if (time[i] == -1) {
		repeatInSeconds = fillDateString(dateStringBuilder, i);
		break;
	    } else {
		dateStringBuilder.append(time[i]);
		if (i != time.length - 1) {
		    dateStringBuilder.append(":");
		}
	    }
	}
	String dateString = dateStringBuilder.toString();
	try {
	    Trigger trigger = createTrigger("ss:mm:HH:u:ww:yyyy", dateString, repeatInSeconds);
	    scheduler.scheduleJob(job, trigger);
	} catch (ParseException ex) {
	    logger.log(Level.WARNING, "Could not parse generated date", ex);
	}
    }

    @SuppressWarnings("fallthrough")
    private int fillDateString(StringBuilder dateBuilder, int progress) {
	int repeatInSeconds = 0;
	LocalDateTime now = LocalDateTime.now();
	switch (progress) {
	    case 0:
		repeatInSeconds = setIntOnce(repeatInSeconds, 1);
		dateBuilder
			.append(now.get(ChronoField.SECOND_OF_MINUTE))
			.append(":");
	    case 1:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60);
		dateBuilder
			.append(now.get(ChronoField.MINUTE_OF_HOUR))
			.append(":");
	    case 2:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60 * 60);
		dateBuilder
			.append(now.get(ChronoField.HOUR_OF_DAY))
			.append(":");
	    case 3:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60 * 60 * 24);
		dateBuilder
			.append(now.get(ChronoField.DAY_OF_WEEK))
			.append(":");
	    case 4:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60 * 60 * 24);
		dateBuilder
			/* Week of year */
			.append(new SimpleDateFormat("w").format(new Date()))
			.append(":");
	    case 5:
		dateBuilder
			.append(now.get(ChronoField.YEAR));
	}
	return repeatInSeconds;
    }

}

/**
 * Represents a command scheduled to execute on times formatted with weeks in
 * mind.
 */
class ScheduledMonthCommand extends ScheduledCommand {

    private static final Logger logger = Logger.getLogger(ScheduledWeekCommand.class.getName());

    private final int[] time;

    /**
     * Creates the object.
     *
     * @param command the command that should be executed
     * @param second the second of the minute on which the command should be
     * executed
     * @param minute the minute of the hour on which the command should be
     * executed
     * @param hour the hour of the day on which the command should be executed,
     * ranging from 0 to 23
     * @param day the day of the week, starting at 1 for Monday, on which the
     * command should be executed
     * @param month The month of the year in which this command should be
     * executed, ranging from 1 to 12
     * @param year the year in which this command should execute. Note that this
     * will break in the year 2147483648 due to the limitations to an Integer
     */
    protected ScheduledMonthCommand(String command, int second, int minute,
	    int hour, int day, int month, int year) {

	super(command);
	time = new int[]{second, minute, hour, day, month, year};
    }

    @Override
    public String getTime() {
	return time[2] + ":" + time[1] + ":" + time[0] + " on day "
		+ time[3] + " in month " + time[4] + " of "
		+ time[5];
    }

    @Override
    public void schedule(Scheduler scheduler) throws SchedulerException {
	HashMap<String, String> dataMap = new HashMap<>();
	dataMap.put("command", this.command);

	JobDetail job = newJob(CommandJob.class)
		.setJobData(new JobDataMap(dataMap))
		.withDescription(this.toString())
		.build();

	StringBuilder dateStringBuilder = new StringBuilder();
	int repeatInSeconds = 0;

	for (int i = 0; i < time.length; i++) {
	    if (time[i] == -1) {
		repeatInSeconds = fillDateString(dateStringBuilder, i);
		break;
	    } else {
		dateStringBuilder.append(time[i]);
		if (i != time.length - 1) {
		    dateStringBuilder.append(":");
		}
	    }
	}
	String dateString = dateStringBuilder.toString();
	try {
	    Trigger trigger = createTrigger("ss:mm:HH:u:MM:yyyy", dateString, repeatInSeconds);
	    scheduler.scheduleJob(job, trigger);
	} catch (ParseException ex) {
	    logger.log(Level.WARNING, "Could not parse generated date", ex);
	}
    }

    @SuppressWarnings("fallthrough")
    private int fillDateString(StringBuilder dateBuilder, int progress) {
	int repeatInSeconds = 0;
	LocalDateTime now = LocalDateTime.now();
	switch (progress) {
	    case 0:
		repeatInSeconds = setIntOnce(repeatInSeconds, 1);
		dateBuilder
			.append(now.get(ChronoField.SECOND_OF_MINUTE))
			.append(":");
	    case 1:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60);
		dateBuilder
			.append(now.get(ChronoField.MINUTE_OF_HOUR))
			.append(":");
	    case 2:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60 * 60);
		dateBuilder
			.append(now.get(ChronoField.HOUR_OF_DAY))
			.append(":");
	    case 3:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60 * 60 * 24);
		dateBuilder
			.append(now.get(ChronoField.DAY_OF_WEEK))
			.append(":");
	    case 4:
		repeatInSeconds = setIntOnce(repeatInSeconds, 60 * 60 * 24);
		dateBuilder
			.append(now.get(ChronoField.MONTH_OF_YEAR))
			.append(":");
	    case 5:
		dateBuilder
			.append(now.get(ChronoField.YEAR));
	}
	return repeatInSeconds;
    }

}
