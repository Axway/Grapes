package org.axway.grapes.server.db;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DataUtilsTest {

    @Test
    public void getAllDbArtifacts(){
        final DbModule module = new DbModule();
        module.addArtifact(new DbArtifact());
        final DbModule submodule1 = new DbModule();
        submodule1.addArtifact(new DbArtifact());
        final DbModule submodule2 = new DbModule();
        submodule2.addArtifact(new DbArtifact());
        submodule1.addSubmodule(submodule2);
        module.addSubmodule(submodule1);

        final List<String> gavcs = DataUtils.getAllArtifacts(module);
        assertEquals(3, gavcs.size());

    }

    @Test
    public void getAllArtifacts(){
        final Module module = DataModelFactory.createModule("root", "1");
        module.addArtifact(DataModelFactory.createArtifact("gr1","art1","123","","",""));
        final Module submodule1 = DataModelFactory.createModule("sub1", "1");
        submodule1.addArtifact(DataModelFactory.createArtifact("gr2","art2","321","","",""));
        final Module submodule2 = DataModelFactory.createModule("sub2", "1");
        submodule2.addArtifact(DataModelFactory.createArtifact("gr3","art3","456","","",""));
        submodule1.addSubmodule(submodule2);
        module.addSubmodule(submodule1);

        final Set<Artifact> artifacts = DataUtils.getAllArtifacts(module);
        assertEquals(3, artifacts.size());

    }

    @Test
    public void getAllDependencies(){
        final Module module = DataModelFactory.createModule("root", "1");
        module.addDependency(DataModelFactory.createDependency(DataModelFactory.createArtifact("gr1", "art1", "123", "", "", ""), Scope.SYSTEM));
        final Module submodule1 = DataModelFactory.createModule("sub1", "1");
        submodule1.addDependency(DataModelFactory.createDependency(DataModelFactory.createArtifact("gr2", "art2", "123", "", "", ""), Scope.SYSTEM));
        final Module submodule2 = DataModelFactory.createModule("sub2", "1");
        submodule2.addDependency(DataModelFactory.createDependency(DataModelFactory.createArtifact("gr3", "art3", "123", "", "", ""), Scope.SYSTEM));
        submodule1.addSubmodule(submodule2);
        module.addSubmodule(submodule1);

        final Set<Dependency> dependencies = DataUtils.getAllDependencies(module);
        assertEquals(3, dependencies.size());

    }

    @Test
    public void getAllDbDependencies(){
        final DbModule module = new DbModule();
        module.addDependency("", Scope.TEST);
        final DbModule submodule1 = new DbModule();
        submodule1.addDependency("", Scope.TEST);
        final DbModule submodule2 = new DbModule();
        submodule2.addDependency("", Scope.TEST);
        submodule1.addSubmodule(submodule2);
        module.addSubmodule(submodule1);

        final List<DbDependency> dependencies = DataUtils.getAllDbDependencies(module);
        assertEquals(3, dependencies.size());

    }

    @Test
    public void getModuleName(){
        assertEquals("", DataUtils.getModuleName(":123"));
        assertEquals("modulename", DataUtils.getModuleName("modulename:"));
        assertEquals("modulename", DataUtils.getModuleName("modulename:123"));
        assertEquals("module", DataUtils.getModuleName("module:name:123"));
        assertEquals("modulename123", DataUtils.getModuleName("modulename123"));
    }

    @Test
    public void getModuleVersion(){
        assertEquals("123", DataUtils.getModuleVersion(":123"));
        assertEquals("", DataUtils.getModuleVersion("modulename:"));
        assertEquals("123", DataUtils.getModuleVersion("modulename:123"));
        assertEquals("123", DataUtils.getModuleVersion("module:name:123"));
        assertEquals("modulename123", DataUtils.getModuleVersion("modulename123"));
    }

    @Test
    public void getArtifactFromGavc(){
        DbArtifact artifact = DataUtils.createDbArtifact("groupId:artifactId:1.0.0:classifier:extension");
        assertEquals("groupId", artifact.getGroupId());
        assertEquals("artifactId", artifact.getArtifactId());
        assertEquals("1.0.0", artifact.getVersion());
        assertEquals("classifier", artifact.getClassifier());
        assertEquals("extension", artifact.getExtension());


        artifact = DataUtils.createDbArtifact("groupId:artifactId:1.0.0::");
        assertEquals("groupId", artifact.getGroupId());
        assertEquals("artifactId", artifact.getArtifactId());
        assertEquals("1.0.0", artifact.getVersion());
        assertEquals("", artifact.getClassifier());
        assertEquals("", artifact.getExtension());
    }

    @Test
    public void getModulesSubmodules(){
        DbModule module = new DbModule();

        assertEquals(0, DataUtils.getAllSubmodules(module).size());

        DbModule submodule1 = new DbModule();
        module.addSubmodule(submodule1);
        DbModule submodule2 = new DbModule();
        module.addSubmodule(submodule2);
        assertEquals(2, DataUtils.getAllSubmodules(module).size());

        DbModule subsubmodule = new DbModule();
        submodule1.addSubmodule(subsubmodule);
        assertEquals(3, DataUtils.getAllSubmodules(module).size());
    }


    @Test
    public void getGroupIdFromGavc(){
        final String gavc1 = "groupId:artifactId:version:classifier:extension";
        final String gavc2 = "test";

        assertEquals("groupId", DataUtils.getGroupId(gavc1));
        assertEquals("test", DataUtils.getGroupId(gavc2));
    }

}
