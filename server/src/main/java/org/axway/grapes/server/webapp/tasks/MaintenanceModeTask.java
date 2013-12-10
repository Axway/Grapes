package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.config.GrapesServerConfig;

import java.io.PrintWriter;

public class MaintenanceModeTask extends Task {

    private final GrapesServerConfig config;

    public MaintenanceModeTask(final GrapesServerConfig config) {
        super("maintenance");
        this.config = config;
    }

    @Override
    public void execute(final ImmutableMultimap<String, String> args, final PrintWriter printWriter) {
        if(config.isInMaintenance()){
            config.setMaintenanceMode(false);
            printWriter.println("Maintenance has been disabled.");
        }
        else {
            config.setMaintenanceMode(true);
            printWriter.println("Maintenance mode is now active.");
        }
    }
}
