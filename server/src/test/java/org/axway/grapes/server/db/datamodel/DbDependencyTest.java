package org.axway.grapes.server.db.datamodel;

import org.axway.grapes.commons.datamodel.Scope;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DbDependencyTest {

    @Test
    public void checkThatTwoDependenciesAreEquals(){
        final DbDependency dependency1 = new DbDependency("source", "target", Scope.RUNTIME );
        assertEquals(dependency1, dependency1);

        DbDependency dependency2 = new DbDependency(null, null, null);
        assertNotEquals(dependency1, dependency2);

        dependency2 = new DbDependency("source", null, null);
        assertNotEquals(dependency1, dependency2);

        dependency2 = new DbDependency("source", "target", null);
        assertNotEquals(dependency1, dependency2);

        dependency2 = new DbDependency("source", "target", Scope.COMPILE);
        assertNotEquals(dependency1, dependency2);

        dependency2 = new DbDependency("source", "target", Scope.RUNTIME);
        assertEquals(dependency1, dependency2);
    }
}
