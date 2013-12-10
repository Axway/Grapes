package org.axway.grapes.server.core;

import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.NotHandledVersionException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.util.MyAsserts.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VersionsHandlerTest {

    private final RepositoryHandler repositoryHandler;
    private final VersionsHandler versionsHandler;

    public VersionsHandlerTest(){
        repositoryHandler = mock(RepositoryHandler.class);
        versionsHandler = new VersionsHandler(repositoryHandler);
    }

    @Test
    public void getTheLastVersionOfRegularVersions() throws IncomparableException, NotHandledVersionException {
        final List<String> versions = new ArrayList<String>();
        // Release older than Snapshot
        versions.add("0.1.0-1");
        versions.add("0.1.0-SNAPSHOT");
        assertEquals("0.1.0-SNAPSHOT", versionsHandler.getLastVersion(versions));

        // Digits based test
        versions.clear();
        versions.add("2.1.0-1");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        assertEquals("2.1.0-1", versionsHandler.getLastVersion(versions));
    }

    @Test
    public void getTheLastReleaseOfRegularVersions() throws IncomparableException, NotHandledVersionException {
        final List<String> versions = new ArrayList<String>();
        versions.add("0.1.0-1");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("2.1.0-1");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        versions.add("4.0.0-SNAPSHOT");
        assertEquals("2.1.0-1", versionsHandler.getLastRelease(versions));
    }

    @Test
    public void getTheLastReleaseIfNoRelease() throws IncomparableException, NotHandledVersionException {
        final List<String> versions = new ArrayList<String>();
        versions.add("0.1.0-SNAPSHOT");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        versions.add("4.0.0-SNAPSHOT");
        assertEquals(null, versionsHandler.getLastRelease(versions));
    }

    @Test
    public void isUpToDateOnRegularVersions() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("com.axway.tests");
        artifact.setArtifactId("artifact");
        final List<String> versions = new ArrayList<String>();
        versions.add("0.1.0-SNAPSHOT");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        versions.add("2.1.0-1");
        versions.add("4.0.0-SNAPSHOT");

        when(repositoryHandler.getArtifactVersions((DbArtifact) anyObject())).thenReturn(versions);

        artifact.setVersion("3.0.0-SNAPSHOT");
        assertFalse(versionsHandler.isUpToDate(artifact));

        artifact.setVersion("2.1.0-1");
        assertTrue(versionsHandler.isUpToDate(artifact));

        artifact.setVersion("4.0.0-SNAPSHOT");
        assertTrue(versionsHandler.isUpToDate(artifact));
    }

    @Test
    public void isUpToDateOnNoneRegularVersions() throws UnknownHostException {
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("com.axway.tests");
        artifact.setArtifactId("artifact");
        final List<String> versions = new ArrayList<String>();
        versions.add("bbbbbb");
        versions.add("cccccc");
        versions.add("dddddd");
        versions.add("eeeeee");

        when(repositoryHandler.getArtifactVersions((DbArtifact) anyObject())).thenReturn(versions);

        artifact.setVersion("aaaaaa");
        assertFalse(versionsHandler.isUpToDate(artifact));

        artifact.setVersion("zzzzzz");
        assertTrue(versionsHandler.isUpToDate(artifact));

        artifact.setVersion("eeeeee");
        assertTrue(versionsHandler.isUpToDate(artifact));
    }

    @Test
    public void lastVersionDoesNotExist() throws UnknownHostException, IncomparableException, NotHandledVersionException {
        String lastVersion = versionsHandler.getLastVersion(new ArrayList<String>());
        assertNull(lastVersion);
    }
}
