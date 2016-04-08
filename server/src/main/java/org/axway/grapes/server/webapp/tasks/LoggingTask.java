package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

import java.io.*;
import java.util.IllegalFormatCodePointException;

/**
 * Get the content of the log file to the authorized user
 * 
 * <p>
 * This task is able to print logs created by Grapes. To print logs: POST
 * <host>:<adminPort>/tasks/getLogs
 * <p>
 * 
 */
public class LoggingTask extends Task {

	private String filePath;

	public LoggingTask(final String filePath) {
		super("getLogs");

        validate(filePath);

		this.filePath = filePath;
	}

	@Override
	public void execute(
			final ImmutableMultimap<String, String> stringStringImmutableMultimap,
			final PrintWriter printWriter) throws Exception {

        try {
            validate(filePath);
        } catch(IllegalArgumentException e) {
            printWriter.println(e.getMessage());
            return;
        }

		printWriter.println("LOGS...");
		BufferedReader br = null;
        FileReader fr = null;
		try {
            fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				// printing out each line in the file
				printWriter.println(line);
			}

		} catch (FileNotFoundException e) {
			printWriter.println("Cannot find the log file");
			e.printStackTrace();
		}
        finally {
            if(br != null) {
                br.close();
            }

            if(fr != null) {
                fr.close();
            }
        }
    }

    private void validate(String filePath) throws IllegalArgumentException {
        if(filePath == null)
            throw new IllegalArgumentException("Log file path cannot be null");

        File f = new File(filePath);

        if(!f.exists()) {
            throw new IllegalArgumentException("Cannot find the log file");
        }

        if(!f.canRead())
            throw new IllegalArgumentException("Log file cannot be read");
    }

}