package org.axway.grapes.commons.datamodel;

import org.junit.Test;

import static org.junit.Assert.*;


public class ArtifactTest {


    @Test
    public void checksWhenTwoArtifactsAreEquals(){

        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1.0.0-SNAPSHOT", "win32", "jar", "jar");
        Artifact artifact2 = new Artifact();

        assertFalse(artifact.equals(artifact2));

        artifact2.setArtifactId("test");
        assertFalse(artifact.equals(artifact2));

        artifact2.setGroupId("com.my.company");
        assertFalse(artifact.equals(artifact2));

        artifact2.setVersion("1.0.0-SNAPSHOT");
        assertFalse(artifact.equals(artifact2));

        artifact2.setClassifier("win32");
        assertFalse(artifact.equals(artifact2));

        artifact2.setType("jar");
        assertFalse(artifact.equals(artifact2));

        artifact2.setExtension("jar");
        assertTrue(artifact.equals(artifact2));

        assertFalse(artifact.equals("test"));
    }



    @Test
    public void twoDifferentObjectAreNotEquals(){

        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1.0.0-SNAPSHOT", "win32", "jar", "jar");
        Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);
        assertFalse(artifact.equals(dependency));
    }

    @Test
    public void testArtifactToString(){
        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1.0.0-SNAPSHOT", "lin64", "jar", "jar");
        assertEquals("com.my.company:test:1.0.0-SNAPSHOT:lin64:jar:jar", artifact.toString());
    }

    @Test
    public void getArtifactGavc(){
        Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        assertEquals("groupId:artifactId:version:classifier:extension", artifact.getGavc());
    }
}


