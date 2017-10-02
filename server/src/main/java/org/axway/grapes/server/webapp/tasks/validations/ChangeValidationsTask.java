package org.axway.grapes.server.webapp.tasks.validations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.resources.PromotionReportTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.*;

public class ChangeValidationsTask extends Task {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeValidationsTask.class);


    public ChangeValidationsTask() {
        super("validations");
    }



    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap,
                        PrintWriter printWriter) throws Exception {

        LOG.info("Got a request to change the validation configuration");

        final ImmutableMap<String, Collection<String>> params = immutableMultimap.asMap();

        if(params.containsKey("error")) {
            final Set<String> newErrors = new HashSet<>();
            final String[] providedErrors = params.get("error").toArray(new String[0]);

            for(String provided : providedErrors) {
                try {
                    PromotionValidation.valueOf(provided);
                    newErrors.add(provided);
                    if(LOG.isInfoEnabled()) {
                        LOG.info(String.format("Validation treated as error: %s", provided));
                    }
                } catch(IllegalArgumentException exc) {
                    LOG.info(String.format("Invalid validation provided: %s Ignoring.", provided));
                }
            }

            PromotionReportTranslator.setErrorStrings(new ArrayList<>(newErrors));
            printWriter.println("Done");
        } else {
            LOG.error("Invalid message to change the validation configuration");
        }
    }

}
