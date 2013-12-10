package org.axway.grapes.server.core.reports;


import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Scope;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DependencyReportViewTest {

    @Test
    public void getDepReport(){
        final Dependency dep1 = DataModelFactory.createDependency(DataModelFactory.createArtifact("groupId", "artifactId", "0.1.2", "", "", ""), Scope.COMPILE);
        final Dependency dep2 = DataModelFactory.createDependency(DataModelFactory.createArtifact("groupId", "artifactId", "0.1.2", "", "", ""), Scope.COMPILE);

        final DependencyReport view = new DependencyReport("test");
        view.addDependency(dep1, "1.0.0");
        view.addDependency(dep2, "1.0.0");

        final List<Artifact> target = view.getDependencyTargets();
        assertNotNull(target);
        assertEquals(1, target.size());

        final List<String> versions = view.getVersions(target.get(0));
        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertEquals(dep1.getTarget().getVersion(), versions.get(0));

        final String version = view.getLastVersion(target.get(0));
        assertNotNull(version);
        assertEquals("1.0.0", version);
    }

}
