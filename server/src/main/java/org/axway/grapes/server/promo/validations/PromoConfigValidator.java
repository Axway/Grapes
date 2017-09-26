package org.axway.grapes.server.promo.validations;

import org.axway.grapes.server.config.PromoValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PromoConfigValidator {

    private static final Logger LOG = LoggerFactory.getLogger(PromoConfigValidator.class);

    public void testValidity(PromoValidationConfig cfg) {
        final List<String> invalidEntries = cfg.getErrors()
                .stream()
                .filter(entry -> matchingCount(entry) == 0)
                .collect(Collectors.toList());

        if(!invalidEntries.isEmpty()) {
            invalidEntries.forEach(wrong -> LOG.warn(String.format("[%s] is not a valid promotion validation name. It will be ignored.", wrong)));

            if(LOG.isInfoEnabled()) {
                LOG.info(String.format("Available names: %s",
                        Arrays.stream(PromotionValidation.values())
                                .map(PromotionValidation::name)
                                .collect(Collectors.toList())
                                .toString()));
            }

            cfg.purge(invalidEntries);
        }

        if(LOG.isInfoEnabled()) {
            LOG.info("");
            LOG.info("Promotion validation(s) considered errors");
            cfg.getErrors().stream()
                    .map(PromotionValidation::valueOf)
                    .forEach(v -> LOG.info(String.format("%s (%s)", v.name(), v.getDescription())));

            LOG.info("");
            LOG.info("Promotion validation(s) considered warnings");
            Arrays.stream(PromotionValidation.values())
                    .filter(entry -> !cfg.getErrors().contains(entry.name()))
                    .forEach(v -> LOG.info(String.format("%s (%s)", v.name(), v.getDescription())));
            LOG.info("");
        }

    }

    private long matchingCount(String entry) {
        return Arrays.stream(PromotionValidation.values())
              .filter(v -> v.name().equals(entry))
              .count();

    }
}
