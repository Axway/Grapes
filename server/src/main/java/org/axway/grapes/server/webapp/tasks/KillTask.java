package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

import java.io.PrintWriter;

public class KillTask extends Task {

    public KillTask() {
        super("kill");
    }

    @Override
    public void execute(final ImmutableMultimap<String, String> stringStringImmutableMultimap, final PrintWriter printWriter) {
        // TODO: find an other way to do this
        printWriter.print("Kill the current JVM");
        printWriter.flush();
        System.exit(0);
    }
}
