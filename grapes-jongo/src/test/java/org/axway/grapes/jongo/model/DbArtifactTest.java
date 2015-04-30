package org.axway.grapes.jongo.model;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.DataModelFactory;
import org.axway.grapes.jongo.datamodel.DbArtifact;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DbArtifactTest {
    @Test
    public void checkGavc() {
        DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("com.axway.test");
        artifact.setArtifactId("UidTest");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.setClassifier("win");
        artifact.setType("jar");
        artifact.setExtension("jar");
        assertEquals("com.axway.test:UidTest:1.0.0-SNAPSHOT:win:jar", artifact.getGavc());
    }

    @Test
    public void checkToString() {
        DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("com.axway.test");
        artifact.setArtifactId("UidTest");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.setClassifier("win");
        artifact.setType("jar");
        assertEquals("GroupId: com.axway.test, ArtifactId: UidTest, Version: 1.0.0-SNAPSHOT", artifact.toString());

    }

    @Test
    public void checkGAVCStaticGeneration() {
        DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("com.axway.test");
        dbArtifact.setArtifactId("UidTest");
        dbArtifact.setVersion("1.0.0-SNAPSHOT");
        dbArtifact.setClassifier("win");
        dbArtifact.setType("jar");
        dbArtifact.setExtension("jar");
        System.out.println(dbArtifact.getGroupId());

        Artifact artifact = DataModelFactory.createArtifact(
                dbArtifact.getGroupId(),
                dbArtifact.getArtifactId(),
                dbArtifact.getVersion(),
                dbArtifact.getClassifier(),
                dbArtifact.getType(),
                dbArtifact.getExtension());

        assertEquals(dbArtifact.getGavc(), DbArtifact.generateGAVC(artifact));
        assertEquals(dbArtifact.getGavc(), DbArtifact.generateGAVC(dbArtifact.getGroupId(), dbArtifact.getArtifactId(), dbArtifact.getVersion(), dbArtifact.getClassifier(), dbArtifact.getExtension()));
    }



    }