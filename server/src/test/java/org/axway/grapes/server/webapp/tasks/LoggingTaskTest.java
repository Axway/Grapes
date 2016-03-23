package org.axway.grapes.server.webapp.tasks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileWriter;

import java.io.PrintWriter;

import org.junit.After;

import org.junit.Before;

import org.junit.Test;

/* 
 * Logging Task Test writes a dummy file log and by invoking a fake instance of
 * the class PrintWriter verifies the number of times the function println is called. 
 *
 */

public class LoggingTaskTest {

	File file = new File("log.txt");
	private int lineCount = 5;

	@Before
	public void prepareLogFile() throws Exception {
		// TODO prepare a file and populate it with five lines
		FileWriter writer = new FileWriter(file, true);
		PrintWriter printWriter = new PrintWriter(writer);
		String x = "LOGS...";
		for (int i = 0; i < lineCount; i++) {
			// printing out each line in the file
			printWriter.println(x);
		}
	}

	@After
	public void cleanupLogFile() {
		// TODO delete the log file from the disk
		file.delete();
	}

	@Test
	public void test() {
		PrintWriter printWriter = mock(PrintWriter.class);
		final LoggingTask sut = new LoggingTask(file.getAbsolutePath());
		try {
			sut.execute(null, printWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//number of lines written in the file plus one for the message that says LOGS...
		verify(printWriter, times(6)).println("LOGS...");

	}

}
