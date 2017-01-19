package org.axway.grapes.server.core.services.email;

import org.axway.grapes.commons.datamodel.ArtifactQuery;
import org.axway.grapes.server.config.Messages;
import org.axway.grapes.server.webapp.links.JiraLinkGenerator;

import static org.axway.grapes.server.config.Messages.getMessage;
import static org.axway.grapes.server.core.services.email.MessageKey.*;

import java.util.List;

/**
 * Utility class for creating strings out of configuration entries containing
 * string placeholders merged with actual values
 */
public class TemplateReplacer {

    private TemplateReplacer() {}

    public static String buildArtifactNotSupportedResponse(final List<String> supported) {
        return String.format(Messages.getMessage(ARTIFACT_VALIDATION_TYPE_NOT_SUPPORTED),
                supported.toString());
    }

    public static String buildArtifactNotPromotedResponse(final ArtifactQuery q, final String ticketLink) {
        String msg = Messages.getMessage(ARTIFACT_VALIDATION_NOT_PROMOTED);

        return String.format(msg,
                            q.getStage() == 0 ? "uploading" : "promoting",
                            q.getName(),
                            q.getSha256(),
                            ticketLink);
    }


    public static String buildArtifactValidationSubject(final String fileName) {
        return String.format(Messages.getMessage(ARTIFACT_VALIDATION_EMAIL_SUBJECT),
                fileName);
    }

    public static String buildArtifactValidationBody(final ArtifactQuery q, final String jenkinsJobLink) {
        return String.format(Messages.getMessage(ARTIFACT_VALIDATION_EMAIL_BODY),
                q.getUser(),
                q.getStage() == 0 ? "upload" : "publish",
                q.getName(),
                q.getSha256(),
                q.getLocation(),
                jenkinsJobLink);
    }

    public static String buildArtifactNotificationJiraLink(final ArtifactQuery q) {

        String jiraRoot = Messages.getMessage(ARTIFACT_VALIDATION_JIRA_ROOT);

        if(jiraRoot.equals(ARTIFACT_VALIDATION_JIRA_ROOT.toString())) {
            jiraRoot = "https://techweb.axway.com/jira/";
        }

        JiraLinkGenerator gen = new JiraLinkGenerator(jiraRoot);
        return gen.generateLink(
                String.format(getMessage(ARTIFACT_VALIDATION_TICKET_SUMMARY), q.getName()),
                String.format(getMessage(ARTIFACT_VALIDATION_TICKET_BODY),
                        q.getName(),
                        q.getType(),
                        q.getSha256(),
                        q.getLocation()),
                q.getUser());

    }
}
