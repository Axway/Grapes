package org.axway.grapes.server.core.services.email;

/**
 * These are keys for messages
 */
public enum MessageKey {

    //
    // Responses to client response
    //
    ARTIFACT_VALIDATION_TYPE_NOT_SUPPORTED,
    ARTIFACT_VALIDATION_NOT_KNOWN,
    ARTIFACT_VALIDATION_NOT_PROMOTED_YET,
    ARTIFACT_VALIDATION_IS_PROMOTED,

    // Email notifications to support team
    ARTIFACT_VALIDATION_EMAIL_SUBJECT,
    ARTIFACT_VALIDATION_EMAIL_BODY,

    // Others
    ARTIFACT_VALIDATION_JIRA_ROOT,
    ARTIFACT_VALIDATION_TICKET_SUMMARY,
    ARTIFACT_VALIDATION_TICKET_BODY
}