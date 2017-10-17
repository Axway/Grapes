package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Comment;
import org.axway.grapes.commons.datamodel.PromotionEvaluationReport;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.views.PromotionReportView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.axway.grapes.server.promo.validations.PromotionValidation.*;

/**
 * Class for translating an instance of <CODE>PromotionReportView</CODE> to
 * an instance of PromotionEvaluationReport
 */
public final class PromotionReportTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionReportTranslator.class);
    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();

    public static final String SNAPSHOT_VERSION_MSG = "Module version is SNAPSHOT";
    public static final String MISSING_LICENSE_MSG = "Third party dependencies missing license terms: ";
    public static final String UNACCEPTABLE_LICENSE_MSG = "Third party dependencies under licenses not accepted: ";
    public static final String UNPROMOTED_MSG = "Corporate dependencies not promoted: ";
    public static final String DO_NOT_USE_MSG = "Dependencies marked as not usable: ";

    private static List<String> errorStrings = new ArrayList<>();

    /**
     * Utility classes should not have public constructors
     */
    private PromotionReportTranslator() {
    }

    public static void setErrorStrings(List<String> errors) {
        // Not calling this method would attract treating all the validations as warnings
        if(LOG.isInfoEnabled()) {
            LOG.info(String.format("Setting validation errors %s", errors.toString()));
        }

        errorStrings.clear();
        errorStrings.addAll(errors);
    }

    public static List<String> getErrors() {
        return Collections.unmodifiableList(errorStrings);
    }

    public static PromotionEvaluationReport toReport(final PromotionReportView promotionReportView) {

        final PromotionEvaluationReport result = new PromotionEvaluationReport();

        if(null == promotionReportView) {
            result.addWarning("Null argument");
            return result;
        }

        final List<PromotionValidation> errors = toPromotionValidations(errorStrings);

        if (promotionReportView.isSnapshot()) {
            appendToReport(errors.contains(VERSION_IS_SNAPSHOT), result, SNAPSHOT_VERSION_MSG);
        }

        // do not use dependency
        if (!promotionReportView.getDoNotUseArtifacts().isEmpty()) {
            boolean isFirstElement = true;
            StringBuilder mappedComments = new StringBuilder();
            for (Map.Entry<Artifact, Comment> entry : promotionReportView.getDoNotUseArtifacts().entrySet()) {
                if (!isFirstElement) {
                    mappedComments.append(", ");
                }


                final Comment comment = entry.getValue();
                if(comment != null) {
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

            appendToReport(errors.contains(DO_NOT_USE_DEPS),
                           result,
                           String.format("%s %s", DO_NOT_USE_MSG, mappedComments));
        }
        // unpromoted dependency
        if (!promotionReportView.getUnPromotedDependencies().isEmpty()) {
            String err = buildErrorMsg(promotionReportView.getUnPromotedDependencies(), UNPROMOTED_MSG + " %s");
            appendToReport(errors.contains(UNPROMOTED_DEPS), result, err);
        }

        // missing third party dependency license
        if (!promotionReportView.getMissingLicenses().isEmpty()) {
            String err = buildErrorMsg(
                    promotionReportView.getMissingLicenses().stream().map(Artifact::getGavc).collect(Collectors.toList()),
                    MISSING_LICENSE_MSG + "%s");
            appendToReport(errors.contains(PromotionValidation.DEPS_WITH_NO_LICENSES), result, err);
        }
        // third party dependency not accepted licenses
        if (!promotionReportView.getDependenciesWithNotAcceptedLicenses().isEmpty()) {
            String err = buildErrorMsg(promotionReportView.getDependenciesWithNotAcceptedLicenses(),
                    " licensed as ",
                    UNACCEPTABLE_LICENSE_MSG + " %s");
            appendToReport(errors.contains(DEPS_UNACCEPTABLE_LICENSE), result, err);
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

    private static List<PromotionValidation> toPromotionValidations(final List<String> errorStrings) {
        return errorStrings
                .stream()
                .map(PromotionValidation::valueOf)
                .collect(Collectors.toList());
    }


    private static void appendToReport(final boolean isError,
                                final PromotionEvaluationReport report,
                                final String details) {

        if(isError) {
            report.addError(details);
        } else {
            report.addWarning(details);
        }
    }

}
