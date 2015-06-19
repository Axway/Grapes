package org.axway.grapes.core.handler;

import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.VersionsService;
import org.axway.grapes.core.version.IncomparableException;
import org.axway.grapes.core.version.NotHandledVersionException;
import org.axway.grapes.model.datamodel.Artifact;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.model.Crud;
import org.wisdom.test.parents.Filter;
import org.wisdom.test.parents.WisdomTest;

import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.util.MyAsserts.assertFalse;
import static com.mongodb.util.MyAsserts.assertNull;
import static com.mongodb.util.MyAsserts.assertTrue;
import static org.junit.Assert.assertEquals;

public class VersionsHandlerIT extends WisdomTest {

    @Inject
    VersionsService versionsService;
    @Inject
    ArtifactService artifactService;

    @Inject
    @Filter("(" + Crud.ENTITY_CLASSNAME_PROPERTY + "=org.axway.grapes.jongo.datamodel.DbArtifact)")
    Crud<Artifact, String> artifactStringCrud;



    @Before
    public void clearDBCollection(){
        Iterable<Artifact> list = artifactStringCrud.findAll();
        artifactStringCrud.delete(list);

    }


    @Test
    public void getTheLastVersionOfRegularVersions() throws IncomparableException, NotHandledVersionException {
        final List<String> versions = new ArrayList<String>();
        // Release older than Snapshot
        versions.add("0.1.0-1");
        versions.add("0.1.0-SNAPSHOT");
        assertEquals("0.1.0-SNAPSHOT", versionsService.getLastVersion(versions));

        // Digits based test
        versions.clear();
        versions.add("2.1.0-1");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        assertEquals("2.1.0-1", versionsService.getLastVersion(versions));

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
        assertEquals("2.1.0-1", versionsService.getLastRelease(versions));
    }

    @Test
    public void getTheLastReleaseIfNoRelease() throws IncomparableException, NotHandledVersionException {
        final List<String> versions = new ArrayList<String>();
        versions.add("0.1.0-SNAPSHOT");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        versions.add("4.0.0-SNAPSHOT");
        assertEquals(null, versionsService.getLastRelease(versions));
    }

    @Test
    public void isUpToDateOnRegularVersions() throws UnknownHostException {
        final Artifact artifact = new Artifact();
        artifact.setGroupId("com.axway.tests");
        artifact.setArtifactId("artifact");
        final List<String> versions = new ArrayList<String>();
        versions.add("0.1.0-SNAPSHOT");
        versions.add("0.1.0-SNAPSHOT");
        versions.add("1.1.0-SNAPSHOT");
        versions.add("2.0.0-SNAPSHOT");
        versions.add("2.1.0-1");
        versions.add("4.0.0-SNAPSHOT");

        artifactService.store(artifact);
        Artifact artifact1 = artifactService.getArtifact(artifact.getGavc());

        artifact1.setVersion("3.0.0-SNAPSHOT");
        artifactService.store(artifact1);
        assertFalse(versionsService.isUpToDate(artifact));

        artifact1.setVersion("2.1.0-1");
        artifactService.store(artifact1);
        assertFalse(versionsService.isUpToDate(artifact1));

        artifact1.setVersion("4.0.0-SNAPSHOT");
        artifactService.store(artifact1);
        assertTrue(versionsService.isUpToDate(artifact1));
    }

    @Test
    public void isUpToDateOnNoneRegularVersions() throws UnknownHostException {
        final Artifact artifact = new Artifact();
        artifact.setGroupId("com.axway.tests");
        artifact.setArtifactId("artifact");
        final List<String> versions = new ArrayList<String>();
        versions.add("bbbbbb");
        versions.add("cccccc");
        versions.add("dddddd");
        versions.add("eeeeee");


        artifact.setVersion("bbbbbb");
        artifactService.store(artifact);
        artifact.setVersion("aaaaaa");
        artifactService.store(artifact);
        assertFalse(versionsService.isUpToDate(artifact));

        artifact.setVersion("zzzzzz");
        artifactService.store(artifact);
        assertTrue(versionsService.isUpToDate(artifact));

        artifact.setVersion("eeeeee");
        artifactService.store(artifact);
        assertFalse(versionsService.isUpToDate(artifact));
    }

    @Test
    public void lastVersionDoesNotExist() throws UnknownHostException, IncomparableException, NotHandledVersionException {
        String lastVersion = versionsService.getLastVersion(new ArrayList<String>());
        assertNull(lastVersion);
    }
}
