package org.axway.grapes.commons.datamodel;

import org.axway.grapes.commons.exceptions.UnsupportedScopeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.junit.Assert.*;

public class DataModelFactoryTest {

    @Rule
    public ExpectedException exc = ExpectedException.none();

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
    public void checkDependencyGeneration() throws UnsupportedScopeException{
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
    public void checkUnsupportedScope() throws UnsupportedScopeException {
        Artifact artifact = DataModelFactory.createArtifact("com.my.company", "artifact", "1.0.0-SNAPSHOT", "win32", "jar", "jar");

        exc.expect(UnsupportedScopeException.class);
        DataModelFactory.createDependency(artifact, "wrongScope");
//        Exception exception = null;
//
//        try {
//            DataModelFactory.createDependency(artifact, "wrongScope");
//        } catch (Exception e) {
//            exception = e;
//        }
//        assertNotNull(exception);

    }

    @Test
    public void createComment() throws UnsupportedScopeException {
        Comment comment = DataModelFactory.createComment("com.axway.test:1.0.0::jar", "DbArtifact", "test comment", "testUser", new Date());
        assertNotNull(comment);
        assertEquals("com.axway.test:1.0.0::jar", comment.getEntityId());
        assertEquals("DbArtifact", comment.getEntityType());
        assertEquals("test comment", comment.getCommentText());
        assertEquals("testUser", comment.getCommentedBy());
    }

}
