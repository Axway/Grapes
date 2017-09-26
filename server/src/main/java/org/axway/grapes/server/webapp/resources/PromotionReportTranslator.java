package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Comment;
import org.axway.grapes.commons.datamodel.PromotionEvaluationReport;
import org.axway.grapes.server.promo.validations.PromotionValidation;
import org.axway.grapes.server.webapp.views.PromotionReportView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.axway.grapes.server.promo.validations.PromotionValidation.*;

/**
 * Class for translating an instance of <CODE>PromotionReportView</CODE> to
 * an instance of PromotionEvaluationReport
 */
public final class PromotionReportTranslator {

    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();

    /**
     * Utility classes should not have public constructors
     */
    private PromotionReportTranslator() {
    }

    public static PromotionEvaluationReport toReport(final List<String> errorStrings,
                                                     final PromotionReportView promotionReportView) {
        final PromotionEvaluationReport result = new PromotionEvaluationReport();

        final List<PromotionValidation> errors = toPromotionValidations(errorStrings);

        if (promotionReportView.isSnapshot()) {
            appendToReport(errors.contains(VERSION_IS_SNAPSHOT), result, "Version is SNAPSHOT");
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
                    mappedComments.append(String.format("%s. %s (%s on %s) %s", entry.getKey(),
                            comment.getCommentedBy(),
                            comment.getAction(),
                            DATE_FORMAT.format(comment.getCreatedDateTime()),
                            comment.getCommentText()));
                } else {
                    mappedComments.append(entry.getKey());
                }
                isFirstElement = false;
            }

            appendToReport(errors.contains(DO_NOT_USE_DEPS),
                           result,
                           String.format("DO_NOT_USE marked dependencies detected: %s", mappedComments));
        }
        // unpromoted dependency
        if (!promotionReportView.getUnPromotedDependencies().isEmpty()) {
            String err = buildErrorMsg(promotionReportView.getUnPromotedDependencies(), "Corporate dependencies not promoted were detected: %s");

            appendToReport(errors.contains(UNPROMOTED_DEPS), result, err);
        }

        // missing third party dependency license
        if (!promotionReportView.getMissingThirdPartyDependencyLicenses().isEmpty()) {
            String err = buildErrorMsg(promotionReportView.getMissingThirdPartyDependencyLicenses(), "The module you are trying to promote has dependencies that miss the license information: %s");
            appendToReport(errors.contains(PromotionValidation.DEPS_WITH_NO_LICENSES), result, err);
        }
        // third party dependency not accepted licenses
        if (!promotionReportView.getDependenciesWithNotAcceptedLicenses().isEmpty()) {
            String err = buildErrorMsg(promotionReportView.getDependenciesWithNotAcceptedLicenses(), "The module you try to promote makes use of third party dependencies whose licenses are not accepted by Axway: %s");
            appendToReport(errors.contains(DEPS_UNACCEPTABLE_LICENSE), result, err);
        }

        return result;
    }

    /**
     * Get the error message with the dependencies appended
     *
     * @param dependencies - the list with dependencies to be attached to the message
     * @param message      - the custom error message to be displayed to the user
     * @return String
     */
    private static String buildErrorMsg(List<?> dependencies, String message) {
        StringBuilder promotionErrors = new StringBuilder();
        boolean isFirstElement = true;
        for (Object dependency : dependencies) {
            if (!isFirstElement) {
                promotionErrors.append(", ");
            }
            // check if it is an instance of Artifact - add the gavc else append the object
            promotionErrors.append(dependency instanceof Artifact ? ((Artifact) dependency).getGavc() : dependency);

            isFirstElement = false;
        }
        return String.format(message, promotionErrors.toString());
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
