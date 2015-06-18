package org.axway.grapes.core.webapi.tasks;

import com.google.common.collect.ImmutableMultimap;
import org.wisdom.api.DefaultController;

import java.io.PrintWriter;

public class KillTask extends DefaultController {




    public void execute(final ImmutableMultimap<String, String> stringStringImmutableMultimap, final PrintWriter printWriter) {
        // TODO: find an other way to do this
        printWriter.print("Kill the current JVM");
        printWriter.flush();
        System.exit(0);
    }
}
