package org.axway.grapes.server.webapp.tasks;

import org.axway.grapes.server.config.GrapesServerConfig;
import org.junit.Test;

import java.io.PrintWriter;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;

public class MaintenanceModeTaskTest {

    @Test
    public void testMaintenanceMode(){
        final GrapesServerConfig config = new GrapesServerConfig();
        final MaintenanceModeTask task = new MaintenanceModeTask(config);

        assertFalse(config.isInMaintenance());

        task.execute(null,mock(PrintWriter.class));
        assertTrue(config.isInMaintenance());

        task.execute(null, mock(PrintWriter.class));
        assertFalse(config.isInMaintenance());
    }
}

