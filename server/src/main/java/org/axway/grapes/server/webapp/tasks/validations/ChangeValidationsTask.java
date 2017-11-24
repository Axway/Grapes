package org.axway.grapes.server.webapp.tasks.validations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.core.CacheUtils;
import org.axway.grapes.server.core.cache.CacheName;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.resources.PromotionReportTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.*;

public class ChangeValidationsTask extends Task {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeValidationsTask.class);
    private static final String ERROR_PARAM_KEY = "error";
    private static final String NONE_VALUE = "NONE";

    private final CacheUtils cacheUtils = new CacheUtils();

    public ChangeValidationsTask() {
        super("validations");
    }



    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap,
                        PrintWriter printWriter) throws Exception {

        LOG.info("Got a request to change the validation configuration");

        final ImmutableMap<String, Collection<String>> params = immutableMultimap.asMap();

        if (!params.containsKey(ERROR_PARAM_KEY)) {
            LOG.error("Invalid message to change the validation configuration");
            printWriter.println(String.format(
                    "Invalid message to change the validation configuration. Use x-www-form-urlencoded as " +
                            "request body and include discrete entries named %s for each of the validations. " +
                            "To set all as warnings, use %s field with the special value %s.",
                    ERROR_PARAM_KEY,
                    ERROR_PARAM_KEY,
                    NONE_VALUE));
            return;
        }


        final long noneCount = params.get(ERROR_PARAM_KEY).stream().filter(e -> e.equals(NONE_VALUE)).count();

        if(noneCount > 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Found %s value contained. All validations will become warnings.", NONE_VALUE));
            }

            final PromoValidationConfig config = PromotionReportTranslator.getConfig();
            config.setErrors(Collections.emptyList());
            printWriter.println("Done");
            return;
        }

        final Set<String> newErrors = new HashSet<>();
        final String[] providedErrors = params.get(ERROR_PARAM_KEY).toArray(new String[0]);
        for (String provided : providedErrors) {
            try {
                PromotionValidation.valueOf(provided);
                newErrors.add(provided);
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("Validation treated as error: %s", provided));
                }
            } catch (IllegalArgumentException exc) {
                LOG.info(String.format("Invalid validation provided: %s Ignoring.", provided));
            }
        }

        PromotionReportTranslator.getConfig().setErrors(new ArrayList<>(newErrors));
        printWriter.println("Done");

        cacheUtils.clear(CacheName.PROMOTION_REPORTS);
    }

}
