package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Get Logs Task
 * 
 * <p>
 * This task is able to print logs created by Grapes. To print logs: POST
 * <host>:<adminPort>/tasks/getLogs
 * <p>
 * 
 */
public class LoggingTask extends Task {

	// path to logs file
	private String filePath;

	public LoggingTask(final String filePath) {
		super("getLogs");
		this.filePath = filePath;
	}

	@Override
	public void execute(
			final ImmutableMultimap<String, String> stringStringImmutableMultimap,
			final PrintWriter printWriter) throws Exception {

		if (filePath == null) {
			printWriter
					.println("ERROR: No log files are found or the path is incorrect.");
			return;
		}

		printWriter.println("LOGS...");
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String x;
			while ((x = br.readLine()) != null) {
				// printing out each line in the file
				printWriter.println(x);
			}

		} catch (FileNotFoundException e) {
			printWriter.println(e);
			e.printStackTrace();
		}
	}

}
