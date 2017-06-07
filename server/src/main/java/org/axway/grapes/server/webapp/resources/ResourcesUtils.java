package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.server.webapp.views.PromotionReportView;

import java.util.*;

/**
 * <p> Utility class for resource classes that performs resource error collection</p>
 */
public final class ResourcesUtils {

    /**
     * Check and add error to the promotion report if any exist
     *
     * @param promotionReportView - the calculated report for a module
     * @return - map object with error list
     */

    public static Map<String, Object> checkPromotionErrors(PromotionReportView promotionReportView) {

        Map<String, Object> errors = new HashMap<String, Object>();
        List<String> errorList = new ArrayList<String>();

        errors.put("canBePromoted", promotionReportView.canBePromoted());

        if (promotionReportView.isSnapshot()) {
            errorList.add("Version is SNAPSHOT");
        }
        // do not use dependency
        if (!promotionReportView.getDoNotUseArtifacts().isEmpty()) {
            String err = addErrors(promotionReportView.getDoNotUseArtifacts(), "DO_NOT_USE marked dependencies detected: %s");
            errorList.add(err);
        }
        // unpromoted dependency
        if (!promotionReportView.getUnPromotedDependencies().isEmpty()) {
            String err = addErrors(promotionReportView.getUnPromotedDependencies(), "Un promoted dependencies detected: %s");
            errorList.add(err);
        }
        // missing third party dependency license
        if (!promotionReportView.getMissingThirdPartyDependencyLicenses().isEmpty()) {
            String err = addErrors(promotionReportView.getMissingThirdPartyDependencyLicenses(), "The module you are trying to promote has dependencies that miss the license information: %s");
            errorList.add(err);
        }
        // third party dependency not accepted licenses
        if (!promotionReportView.getDependenciesWithNotAcceptedLicenses().isEmpty()) {
            String err = addErrors(promotionReportView.getDependenciesWithNotAcceptedLicenses(), "The module you try to promote makes use of third party dependencies whose licenses are not accepted by Axway: %s");
            errorList.add(err);
        }

        errors.put("errors", errorList);
        return errors;
    }

    /**
     * Get the error message with the dependencies appended
     *
     * @param dependencies - the list with dependencies to be attached to the message
     * @param message      - the custom error message to be displayed to the user
     * @return String
     */
    private static String addErrors(List<?> dependencies, String message) {
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
}
