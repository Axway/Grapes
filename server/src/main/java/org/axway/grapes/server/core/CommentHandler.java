package org.axway.grapes.server.core;


import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbComment;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Comment handler class
 * Manages all operation regarding Comments. It can get/update Comment of the database.
 */
public class CommentHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactHandler.class);

    private final RepositoryHandler repositoryHandler;

    public CommentHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }


    /**
     * Save / update comment to the database
     *
     * @param comment - Comment entity
     */
    public void store(DbComment comment) {
        repositoryHandler.store(comment);
    }

    /**
     * Get a list of comments made for a particular entity
     *
     * @param entityId - id of the commented entity
     * @param entityType - type of the entity
     * @return list of comments
     */
    public List<DbComment> getComments(String entityId, String entityType) {
        return repositoryHandler.getComments(entityId, entityType);
    }

    /**
     * Store a comment based on comment text, gavc and user information
     *
     * @param gavc - entity id
     * @param commentText - comment text
     * @param credential - user credentials
     * @param entityType - type of the entity
     */
    public void store(String gavc, String commentText, DbCredential credential, String entityType) {
        DbComment comment = new DbComment();
        comment.setEntityId(gavc);
        comment.setEntityType(entityType);
        comment.setCommentedBy(credential.getUser());
        comment.setCommentText(commentText);
        comment.setCreatedDateTime(new Date());

        repositoryHandler.store(comment);
    }

    /**
     * Get the last comment made for a particular entity
     *
     * @param entityId - id of the entity
     * @param entityType - type of the entity
     * @return the latest comment
     */
    public DbComment getLatestComment(String entityId, String entityType) {
        return repositoryHandler.getLatestComment(entityId, entityType);
    }

}
