package org.axway.grapes.commons.datamodel;

import org.axway.grapes.commons.exceptions.UnsupportedScopeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataModelFactoryTest {

    @Test
    public void checkModuleGeneration(){
        Module module = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");

        assertNotNull(module);
        assertEquals("module", module.getName());
        assertEquals("1.0.0-SNAPSHOT", module.getVersion());
        assertFalse(module.isPromoted());
        assertTrue(module.getArtifacts().isEmpty());

    }

    @Test
    public void checkArtifactGeneration(){
        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "artifact", "1.0.0-SNAPSHOT", "win32", "jar", "jar");

        assertNotNull(artifact);
        assertEquals("com.my.company", artifact.getGroupId());
        assertEquals("artifact", artifact.getArtifactId());
        assertEquals("1.0.0-SNAPSHOT", artifact.getVersion());
        assertEquals("jar", artifact.getType());
        assertEquals("win32", artifact.getClassifier());
        assertFalse(artifact.isPromoted());

    }

    @Test
    public void checkDependencyGeneration() throws UnsupportedScopeException {
        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "artifact", "1.0.0-SNAPSHOT", "win32", "jar", "jar");
        Dependency dependency1 = DataModelFactory.createDependency(artifact, "compile");
        Dependency dependency2 = DataModelFactory.createDependency(artifact, "Runtime");
        Dependency dependency3 = DataModelFactory.createDependency(artifact, "PROVIDED");
        Dependency dependency4 = DataModelFactory.createDependency(artifact, "tesT");
        Dependency dependency5 = DataModelFactory.createDependency(artifact, "System");
        Dependency dependency6 = DataModelFactory.createDependency(artifact, "import");

        assertEquals(Scope.COMPILE, dependency1.getScope());
        assertEquals(Scope.RUNTIME, dependency2.getScope());
        assertEquals(Scope.PROVIDED, dependency3.getScope());
        assertEquals(Scope.TEST, dependency4.getScope());
        assertEquals(Scope.SYSTEM, dependency5.getScope());
        assertEquals(Scope.IMPORT, dependency6.getScope());

    }


    @Test
    public void checkUnsuportedScope(){
        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "artifact", "1.0.0-SNAPSHOT", "win32", "jar", "jar");

        Exception exception = null;

        try {
            DataModelFactory.createDependency(artifact, "wrongScope");
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);

    }

}
