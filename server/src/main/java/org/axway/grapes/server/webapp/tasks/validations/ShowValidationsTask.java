package org.axway.grapes.server.webapp.tasks.validations;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.promo.validations.PromoConfigPrinter;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.resources.PromotionReportTranslator;

import java.io.PrintWriter;
import java.util.Arrays;

public class ShowValidationsTask extends Task {

    public ShowValidationsTask() {
        super("showValidations");
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap,
                        PrintWriter printWriter) throws Exception {

        final PromoValidationConfig config = PromotionReportTranslator.getConfig();
        PromoConfigPrinter.display(config, printWriter::println);

        printWriter.println("All available validations");
        Arrays.stream(PromotionValidation.values())
                .forEach(v -> printWriter.println(String.format("%s - %s", v.name(), v.getDescription())));

        printWriter.println("Done printing validations");
    }

}
