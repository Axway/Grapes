package org.axway.grapes.server.core;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbComment;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CommentHandlerTest {

    @Test
    public void store() throws Exception {
        DbComment dbComment = new DbComment();

        final RepositoryHandler repo = mock(RepositoryHandler.class);
        final CommentHandler commentHandler = new CommentHandler(repo);
        commentHandler.store(dbComment);

        verify(repo, times(1)).store(dbComment);
    }

    @Test
    public void getComments() throws Exception {

        final String entityId = "com.axway.test:1.0.0::jar";
        final String entityType = "DbArtifact";
        DbComment dbComment = new DbComment();
        dbComment.setEntityId(entityId);
        dbComment.setEntityType(entityType);
        dbComment.setDbCommentText("test comment");
        dbComment.setDbCommentedBy("testUser");
        dbComment.setDbCreatedDateTime(new Date());

        DbComment dbCommentSecond = new DbComment();
        dbCommentSecond.setEntityId(entityId);
        dbCommentSecond.setEntityType(entityType);
        dbCommentSecond.setDbCommentText("test comment second");
        dbCommentSecond.setDbCommentedBy("testUser");
        dbCommentSecond.setDbCreatedDateTime(new Date());

        final RepositoryHandler repo = mock(RepositoryHandler.class);
        final CommentHandler commentHandler = new CommentHandler(repo);
        commentHandler.store(dbComment);
        commentHandler.store(dbCommentSecond);

        List<DbComment> commentList = new ArrayList<>();
        commentList.add(dbComment);
        commentList.add(dbCommentSecond);

        when(repo.getComments(entityId, entityType)).thenReturn(commentList);

        assertEquals(repo.getComments(entityId, entityType), commentList);
    }

    @Test
    public void storeViaParameters() throws Exception {

        final String entityId = "com.axway.test:1.0.0::jar";
        final String entityType = "DbArtifact";
        DbComment dbComment = new DbComment();
        dbComment.setEntityId(entityId);
        dbComment.setEntityType(entityType);
        dbComment.setAction("some action");
        dbComment.setDbCommentText("test comment");
        dbComment.setDbCommentedBy("testUser");
        dbComment.setDbCreatedDateTime(new Date());

        DbCredential credential = new DbCredential();
        credential.setUser("testUser");

        final RepositoryHandler repo = mock(RepositoryHandler.class);
        final CommentHandler commentHandler = new CommentHandler(repo);
        commentHandler.store(entityId, dbComment.getAction(), "test comment", credential, entityType);

        verify(repo, times(1)).store(dbComment);
    }

    @Test
    public void getLatestComment() throws Exception {

        final String entityId = "com.axway.test:1.0.0::jar";
        final String entityType = "DbArtifact";

        DbComment dbComment = new DbComment();
        dbComment.setEntityId(entityId);
        dbComment.setEntityType(entityType);
        dbComment.setDbCommentText("test comment");
        dbComment.setDbCommentedBy("testUser");
        dbComment.setDbCreatedDateTime(new Date());

        DbComment dbCommentLatest = new DbComment();
        dbCommentLatest.setEntityId(entityId);
        dbCommentLatest.setEntityType(entityType);
        dbCommentLatest.setDbCommentText("test comment latest");
        dbCommentLatest.setDbCommentedBy("testUser");
        dbCommentLatest.setDbCreatedDateTime(new Date());

        final RepositoryHandler repo = mock(RepositoryHandler.class);
        final CommentHandler commentHandler = new CommentHandler(repo);
        commentHandler.store(dbComment);
        commentHandler.store(dbCommentLatest);

        when(repo.getLatestComment(entityId,entityType)).thenReturn(dbCommentLatest);

        assertEquals(commentHandler.getLatestComment(entityId, entityType), dbCommentLatest);
    }

}