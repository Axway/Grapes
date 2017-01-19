package org.axway.grapes.server.core.services.email;

import org.axway.grapes.commons.datamodel.ArtifactQuery;
import org.axway.grapes.server.config.Messages;

/**
 * Utility class for creating strings out of configuration entries containing
 * string placeholders merged with actual values
 */
public class TemplateReplacer {

    public static String buildArtifactValidationSubject(String fileName) {
        return String.format(Messages.getMessage(MessageKey.ARTIFACT_NOTIFICATION_EMAIL_SUBJECT_KEY.toString()), fileName);
    }

    public static String buildArtifactValidationBody(ArtifactQuery q) {
        return String.format(Messages.getMessage(MessageKey.DEFAULT_ARTIFACT_NOT_KNOWN_NOTIFICATION_EMAIL_BODY.toString()),
                q.getUser(), q.getName(), q.getSha256(), q.getLocation());
    }
}
