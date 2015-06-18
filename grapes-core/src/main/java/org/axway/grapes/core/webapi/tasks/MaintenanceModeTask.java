package org.axway.grapes.core.webapi.tasks;

import org.wisdom.api.DefaultController;

public class MaintenanceModeTask extends DefaultController {

   /* private final GrapesServerConfig config;

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
    }*/
}
