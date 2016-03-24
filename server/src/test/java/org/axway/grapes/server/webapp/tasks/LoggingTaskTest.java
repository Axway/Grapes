package org.axway.grapes.server.webapp.tasks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileWriter;

import java.io.PrintWriter;

import org.junit.After;

import org.junit.Before;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/* 
 * Logging Task Test writes a dummy file log and by invoking a fake instance of
 * the class PrintWriter verifies the number of times the function println is called. 
 *
 */

public class LoggingTaskTest {

	private File file = new File("log.txt");
	private int lineCount = 5;

    @Rule
    public ExpectedException exc = ExpectedException.none();

    final PrintWriter printWriter = mock(PrintWriter.class);

	@Before
	public void prepareLogFile() throws Exception {
		FileWriter writer = new FileWriter(file, true);
		PrintWriter printWriter = new PrintWriter(writer);
		String x = "LOGS...";
		for (int i = 0; i < lineCount; i++) {
			// printing out each line in the file
			printWriter.println(String.format("#%s log entry", (i + 1)));
		}
        printWriter.flush();
        printWriter.close();
    }

	@After
	public void cleanupLogFile() {
		file.delete();
	}

	@Test
	public void testLogRetrieval() {
		final LoggingTask sut = new LoggingTask(file.getAbsolutePath());
		try {
			sut.execute(null, printWriter);
		} catch (Exception e) {
			e.printStackTrace();
            fail("Exception while executing. Details: " + e.getMessage());
		}

        //
		// number of lines written in the file plus one for the message that says LOGS...
        //
		verify(printWriter, times(lineCount + 1)).println(anyString());
	}

    @Test
    public void testNullLogPathEntry() {
        exc.expect(IllegalArgumentException.class);
        final LoggingTask sut = new LoggingTask(null);
    }

    @Test
    public void testFileNotFound() {
        File f = new File("x:/no/such/log/path/log.txt");
        if(f.exists()) {
            final boolean delete = f.delete();
            if(!delete) {
                fail("Cannot remove log file");
            }
        }
        exc.expect(IllegalArgumentException.class);
        final LoggingTask sut = new LoggingTask(f.getAbsolutePath());
    }

    @Test
    public void testFileNotFoundAtRuntime() {
        final LoggingTask sut = new LoggingTask(file.getAbsolutePath());
        try {
            file.delete();
            assertFalse(file.exists());

            sut.execute(null, printWriter);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception while executing. Details: " + e.getMessage());
        }

        verify(printWriter, times(1)).println("Cannot find the log file");
    }

}
