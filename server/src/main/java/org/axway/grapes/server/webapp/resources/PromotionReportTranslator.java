package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Comment;
import org.axway.grapes.commons.datamodel.PromotionEvaluationReport;
import org.axway.grapes.commons.datamodel.Tag;
import org.axway.grapes.server.config.PromoValidationConfig;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.axway.grapes.commons.datamodel.Tag.MAJOR;
import static org.axway.grapes.commons.datamodel.Tag.MINOR;
import static org.axway.grapes.server.promo.validations.PromotionValidation.*;

/**
 * Class for translating an instance of <CODE>PromotionReportView</CODE> to
 * an instance of PromotionEvaluationReport
 */
public final class PromotionReportTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionReportTranslator.class);
    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();

    static final String SNAPSHOT_VERSION_MSG = "Module version is SNAPSHOT";
    static final String MISSING_LICENSE_MSG = "Third party dependencies missing license terms: ";
    static final String UNACCEPTABLE_LICENSE_MSG = "Third party dependencies under licenses not accepted: ";
    static final String UNPROMOTED_MSG = "Corporate dependencies not promoted: ";
    static final String DO_NOT_USE_MSG = "Dependencies marked as not usable: ";

    private static PromoValidationConfig promoValidationCfg = null;

    /**
     * Utility classes should not have public constructors
     */
    private PromotionReportTranslator() {
    }

    public static void setConfig(PromoValidationConfig cfg) {
        if(null == cfg) {
            throw new IllegalArgumentException("Configuration must not be null");
        }

        promoValidationCfg = cfg;
    }


    public static PromoValidationConfig getConfig() {
        return promoValidationCfg;
    }

    public static PromotionEvaluationReport toReport(final PromotionReportView promotionReportView,
                                                     final PromotionValidation... excluded) {

        final PromotionEvaluationReport result = new PromotionEvaluationReport();

        if(null == promotionReportView) {
            result.addMessage("Null argument", MAJOR);
            return result;
        }

        List<PromotionValidation> exclusions = Arrays.asList(excluded);

        if (promotionReportView.isSnapshot() && !exclusions.contains(VERSION_IS_SNAPSHOT)) {
            append(result, VERSION_IS_SNAPSHOT, SNAPSHOT_VERSION_MSG);
        }

        // do not use dependency
        if(!exclusions.contains(DO_NOT_USE_DEPS)) {
            if (!promotionReportView.getDoNotUseArtifacts().isEmpty()) {
                boolean isFirstElement = true;
                StringBuilder mappedComments = new StringBuilder();
                for (Map.Entry<Artifact, Comment> entry : promotionReportView.getDoNotUseArtifacts().entrySet()) {
                    if (!isFirstElement) {
                        mappedComments.append(", ");
                    }


                    final Comment comment = entry.getValue();
                    if (comment != null) {
                        mappedComments.append(String.format("%s. %s (%s on %s) %s", entry.getKey().getGavc(),
                                comment.getCommentedBy(),
                                comment.getAction(),
                                DATE_FORMAT.format(comment.getCreatedDateTime()),
                                comment.getCommentText()));
                    } else {
                        mappedComments.append(entry.getKey().getGavc());
                    }
                    isFirstElement = false;
                }

                append(result,
                        DO_NOT_USE_DEPS,
                        String.format("%s %s", DO_NOT_USE_MSG, mappedComments));
            }
        }


        if(!exclusions.contains(UNPROMOTED_DEPS)) {
            // unpromoted dependency
            if (!promotionReportView.getUnPromotedDependencies().isEmpty()) {
                String err = buildErrorMsg(promotionReportView.getUnPromotedDependencies(), UNPROMOTED_MSG + " %s");
                append(result, UNPROMOTED_DEPS, err);
            }
        }

        if(!exclusions.contains(DEPS_WITH_NO_LICENSES)) {
            // missing third party dependency license
            if (!promotionReportView.getMissingLicenses().isEmpty()) {
                String err = buildErrorMsg(
                        promotionReportView.getMissingLicenses().stream().map(Artifact::getGavc).collect(Collectors.toList()),
                        MISSING_LICENSE_MSG + "%s");
                append(result, DEPS_WITH_NO_LICENSES, err);
            }
        }

        if(!exclusions.contains(DEPS_UNACCEPTABLE_LICENSE)) {
            // third party dependency not accepted licenses
            if (!promotionReportView.getDependenciesWithNotAcceptedLicenses().isEmpty()) {
                String err = buildErrorMsg(promotionReportView.getDependenciesWithNotAcceptedLicenses(),
                        " licensed as ",
                        UNACCEPTABLE_LICENSE_MSG + " %s");
                append(result, DEPS_UNACCEPTABLE_LICENSE, err);
            }
        }

        return result;
    }


    private static String buildErrorMsg(final Map<String, String> entries,
                                        final String inBetween,
                                        final String message) {

        final StringBuilder buffer = new StringBuilder();
        entries.forEach((a, b) -> {
            buffer.append(a);
            buffer.append(inBetween);
            buffer.append(b);
            buffer.append(", ");
        });

        buffer.setLength(buffer.length() - 2);
        return String.format(message, buffer.toString());
    }
    /**
     * Get the error message with the dependencies appended
     *
     * @param dependencies - the list with dependencies to be attached to the message
     * @param message      - the custom error message to be displayed to the user
     * @return String
     */
    private static String buildErrorMsg(List<String> dependencies, String message) {
        final StringBuilder buffer = new StringBuilder();
        boolean isFirstElement = true;
        for (String dependency : dependencies) {
            if (!isFirstElement) {
                buffer.append(", ");
            }
            // check if it is an instance of Artifact - add the gavc else append the object
            buffer.append(dependency);

            isFirstElement = false;
        }
        return String.format(message, buffer.toString());
    }

    private static void append(final PromotionEvaluationReport report,
                               final PromotionValidation v,
                               final String message) {

        if(null == promoValidationCfg) {
            LOG.error("Configuration hasn't been set");
            return;
        }

        if(promoValidationCfg.getErrors().contains(v.name())) {
            report.setPromotable(false);
        }

        Tag tag = MINOR;
        if(null != promoValidationCfg.getTagConfig()) {
            tag = promoValidationCfg.getTagConfig().getTag(v.name());
        }

        report.addMessage(message, tag);
    }


}
