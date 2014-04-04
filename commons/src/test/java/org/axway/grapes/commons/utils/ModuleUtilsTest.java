package org.axway.grapes.commons.utils;

import org.axway.grapes.commons.datamodel.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModuleUtilsTest {

    @Test
    public void testGetAllDependencies(){
        final Module module = DataModelFactory.createModule("module1", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);

        module.addDependency(dependency);

        final List<Dependency> dependencies = ModuleUtils.getAllDependencies(module);
        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());
        assertEquals(dependency, dependencies.get(0));
    }

    @Test
    public void testGetAllDependenciesOfAnEmptyModule(){
        final Module module = DataModelFactory.createModule("module1", "1.0.0-SNAPSHOT");

        final List<Dependency> dependencies = ModuleUtils.getAllDependencies(module);
        assertNotNull(dependencies);
        assertEquals(0, dependencies.size());
    }

    @Test
    public void testGetAllDependenciesWithSubModules(){
        final Module module = DataModelFactory.createModule("module1", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);

        final Module subModule = DataModelFactory.createModule("module11", "1.0.0-SNAPSHOT");
        subModule.addDependency(dependency);

        module.addSubmodule(subModule);

        final List<Dependency> dependencies = ModuleUtils.getAllDependencies(module);
        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());
        assertEquals(dependency, dependencies.get(0));
    }

    @Test
    public void testGetAllDependenciesAvoidDuplications(){
        final Module module = DataModelFactory.createModule("module1", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("com.my.company", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency = DataModelFactory.createDependency(artifact, Scope.COMPILE);

        final Module subModule = DataModelFactory.createModule("module11", "1.0.0-SNAPSHOT");
        subModule.addDependency(dependency);

        module.addSubmodule(subModule);
        module.addDependency(dependency);

        final List<Dependency> dependencies = ModuleUtils.getAllDependencies(module);
        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());
        assertEquals(dependency, dependencies.get(0));
    }

    @Test
    public void testGetCorporateDependencies(){
        final Module module = DataModelFactory.createModule("module1", "1.0.0-SNAPSHOT");
        final Artifact artifact1 = DataModelFactory.createArtifact("com.my.company.test", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency1 = DataModelFactory.createDependency(artifact1, Scope.COMPILE);

        final Artifact artifact2 = DataModelFactory.createArtifact("org.all", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency2 = DataModelFactory.createDependency(artifact2, Scope.COMPILE);

        module.addDependency(dependency1);
        module.addDependency(dependency2);

        final List<String> corporateFilters = new ArrayList<String>();
        corporateFilters.add("com.my.company");

        final List<Dependency> dependencies = ModuleUtils.getCorporateDependencies(module, corporateFilters);
        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());
        assertEquals(dependency1, dependencies.get(0));
    }

    @Test
    public void testGetThirdPartyLibraries(){
        final Module module = DataModelFactory.createModule("module1", "1.0.0-SNAPSHOT");
        final Artifact artifact1 = DataModelFactory.createArtifact("com.my.company.test", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency1 = DataModelFactory.createDependency(artifact1, Scope.COMPILE);

        final Artifact artifact2 = DataModelFactory.createArtifact("org.all", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
        final Dependency dependency2 = DataModelFactory.createDependency(artifact2, Scope.COMPILE);

        module.addDependency(dependency1);
        module.addDependency(dependency2);

        final List<String> corporateFilters = new ArrayList<String>();
        corporateFilters.add("com.my.company");

        final List<Dependency> dependencies = ModuleUtils.getThirdPartyLibraries(module, corporateFilters);
        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());
        assertEquals(dependency2, dependencies.get(0));
    }
}
