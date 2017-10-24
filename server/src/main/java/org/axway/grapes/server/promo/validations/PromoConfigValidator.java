package org.axway.grapes.server.promo.validations;

import org.axway.grapes.commons.datamodel.Tag;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PromoConfigValidator {

    private static final Logger LOG = LoggerFactory.getLogger(PromoConfigValidator.class);


    public void testValidity(PromoValidationConfig cfg) {
        toValidErrors(cfg);
        toValidTags(cfg);
    }

    private void toValidTags(final PromoValidationConfig cfg) {
        toValidTags(cfg, cfg.getTagConfig().getCritical(), Tag.CRITICAL);
        toValidTags(cfg, cfg.getTagConfig().getMajor(), Tag.MAJOR);
        toValidTags(cfg, cfg.getTagConfig().getMinor(), Tag.MINOR);
    }

    private void toValidTags(final PromoValidationConfig cfg, final List<String> inputs, Tag t) {
        cfg.purgeFromTag(t, getInvalidValues(inputs));
    }

    private List<String> getInvalidValues(List<String> input) {
        return input
                .stream()
                .filter(entry -> matchingCount(entry) == 0)
                .collect(Collectors.toList());
    }

    private void toValidErrors(final PromoValidationConfig cfg) {
        final List<String> invalidEntries = getInvalidValues(cfg.getErrors());

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

    }

    private long matchingCount(String entry) {
        return Arrays.stream(PromotionValidation.values())
              .filter(v -> v.name().equals(entry))
              .count();

    }
}
