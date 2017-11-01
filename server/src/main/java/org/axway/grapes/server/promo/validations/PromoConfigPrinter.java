package org.axway.grapes.server.promo.validations;

import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.config.TagsConfig;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PromoConfigPrinter {

    private PromoConfigPrinter() {}

    public static void display(PromoValidationConfig cfg, Consumer<String> c) {
        useErrorStatus(cfg.getErrors(), c);
        useTagStatus(cfg.getTagConfig(), c);
    }

    private static void useTagStatus(final TagsConfig tagConfig, final Consumer<String> c) {
        StringBuilder b = new StringBuilder(System.lineSeparator());
        b.append("Message Tag Configuration");
        b.append(System.lineSeparator());
        Arrays.stream(PromotionValidation.values())
                .forEach(v -> b.append(String.format("- %s :: %s_warning %s", v.name(), tagConfig.getTag(v.name()).toString().toLowerCase(), System.lineSeparator())));

        c.accept(b.toString());
    }

    private static void useErrorStatus(final List<String> errors, final Consumer<String> c) {
        StringBuilder b = new StringBuilder(System.lineSeparator());
        b.append("Promotion blockers");
        b.append(System.lineSeparator());
        errors.stream()
                .map(PromotionValidation::valueOf)
                .forEach(v -> b.append(String.format("- %s (%s)%S", v.name(), v.getDescription(), System.lineSeparator())));

        b.append("");
        b.append(System.lineSeparator());
        b.append("Promotion warnings");
        b.append(System.lineSeparator());
        Arrays.stream(PromotionValidation.values())
                .filter(entry -> !errors.contains(entry.name()))
                .forEach(v -> b.append(String.format("- %s (%s)%s", v.name(), v.getDescription(), System.lineSeparator())));

        c.accept(b.toString());
    }

}
