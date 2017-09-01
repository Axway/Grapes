package org.axway.grapes.server.db.datamodel;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Database Comment
 * <p>
 * <p>Class that holds the representation of the comments stored in the database.
 * The id is used to identify the DbComments Object.
 * </p>
 */
public class DbComment {

    public static final String DATA_MODEL_VERSION = "DATAMODEL_VERSION";
    private String datamodelVersion = DbCollections.DATAMODEL_VERSION;

    public static final String ENTITY_ID_DB_FIELD = "entityId";
    private String entityId = "";

    public static final String ENTITY_TYPE_DB_FIELD = "entityType";
    private String entityType = "";

    public static final String ACTION_TEXT_DB_FIELD = "action";
    private String action = "";

    public static final String COMMENT_TEXT_DB_FIELD = "commentText";
    private String commentText = "";

    public static final String COMMENTED_BY_DB_FIELD = "commentedBy";
    private String commentedBy = "";

    public static final String CREATED_DATE_DB_FIELD = "createdDateTime";
    private Date createdDateTime = null;

    public String getDbCommentText() {
        return commentText;
    }

    public void setDbCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getDbCommentedBy() {
        return commentedBy;
    }

    public void setDbCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public Date getDbCreatedDateTime() {
        return createdDateTime;
    }

    public void setDbCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getDatamodelVersion() {
        return datamodelVersion;
    }

    public void setDatamodelVersion(String datamodelVersion) {
        this.datamodelVersion = datamodelVersion;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbComment dbComment = (DbComment) o;

        if (datamodelVersion != null ? !datamodelVersion.equals(dbComment.datamodelVersion) : dbComment.datamodelVersion != null)
            return false;
        if (entityId != null ? !entityId.equals(dbComment.entityId) : dbComment.entityId != null) return false;
        if (entityType != null ? !entityType.equals(dbComment.entityType) : dbComment.entityType != null) return false;
        if (action != null ? !action.equals(dbComment.action) : dbComment.action != null) return false;
        if (commentText != null ? !commentText.equals(dbComment.commentText) : dbComment.commentText != null)
            return false;
        if (commentedBy != null ? !commentedBy.equals(dbComment.commentedBy) : dbComment.commentedBy != null)
            return false;
        return createdDateTime != null ? createdDateTime.equals(dbComment.createdDateTime) : dbComment.createdDateTime == null;
    }

    @Override
    public int hashCode() {
        int result = datamodelVersion != null ? datamodelVersion.hashCode() : 0;
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (commentText != null ? commentText.hashCode() : 0);
        result = 31 * result + (commentedBy != null ? commentedBy.hashCode() : 0);
        result = 31 * result + (createdDateTime != null ? createdDateTime.hashCode() : 0);
        return result;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof DbComment)) {
//            return false;
//        }
//        final DbComment dbComment = (DbComment) obj;
//        return StringUtils.equals(
//                StringUtils.trimToEmpty(this.getEntityType()),
//                StringUtils.trimToEmpty(dbComment.getEntityType()))
//                && StringUtils.equals(
//                StringUtils.trimToEmpty(this.getEntityId()),
//                StringUtils.trimToEmpty(dbComment.getEntityId()))
//                && StringUtils.equals(
//                StringUtils.trimToEmpty(this.getDbCommentedBy()),
//                StringUtils.trimToEmpty(dbComment.getDbCommentedBy()))
//                && StringUtils.equals(StringUtils.trimToEmpty(this.getDbCommentText()),
//                StringUtils.trimToEmpty(dbComment.getDbCommentText()));
//    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
