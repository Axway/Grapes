package org.axway.grapes.server.webapp.tasks.validations;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.resources.PromotionReportTranslator;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class ShowValidationsTask extends Task {

    public ShowValidationsTask() {
        super("showValidations");
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap,
                        PrintWriter printWriter) throws Exception {
        final List<String> errors = PromotionReportTranslator.getErrors();


        printWriter.println("Validations treated as errors");
        errors.forEach(e -> {
            final PromotionValidation v = PromotionValidation.valueOf(e);
            printWriter.println(String.format(String.format("%s - %s", e, v.getDescription())));
        });
        printWriter.println("Done printing list");

        printWriter.println("");
        printWriter.println("");

        printWriter.println("All registered validations");
        Arrays.stream(PromotionValidation.values()).forEach(v -> {
            printWriter.println(String.format(String.format("%s - %s", v.name(), v.getDescription())));
        });
        printWriter.println("Done printing validations");
    }

}
