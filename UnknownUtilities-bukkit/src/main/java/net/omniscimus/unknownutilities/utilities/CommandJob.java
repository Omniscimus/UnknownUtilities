package net.omniscimus.unknownutilities.utilities;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CommandJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
	String command = context.getMergedJobDataMap().getString("command");
	ScheduledCommandsUtility.executeCommand(command);
    }
    
}
