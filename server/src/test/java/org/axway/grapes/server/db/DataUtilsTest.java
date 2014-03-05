package org.axway.grapes.server.db;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DataUtilsTest {

    @Test
    public void testGetDbLicense() throws Exception {
        final License license = DataModelFactory.createLicense("name", "longName", "comments", "regexp", "url");
        final DbLicense dbLicense = DataUtils.getDbLicense(license);

        assertEquals(license.getName(), dbLicense.getName());
        assertEquals(license.getLongName(), dbLicense.getLongName());
        assertEquals(license.getComments(), dbLicense.getComments());
        assertEquals(license.getRegexp(), dbLicense.getRegexp());
        assertEquals(license.getUrl(), dbLicense.getUrl());

    }

    @Test
    public void testGetDbArtifact(){
        final Artifact artifact = DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extension");
        artifact.setSize("10Mo");
        artifact.setDownloadUrl("http://www.nowhere.com");
        artifact.setProvider("http://www.nowhere.com/provider");

        final DbArtifact dbArtifact = DataUtils.getDbArtifact(artifact);

        assertEquals(artifact.getGroupId(), dbArtifact.getGroupId());
        assertEquals(artifact.getArtifactId(), dbArtifact.getArtifactId());
        assertEquals(artifact.getVersion(), dbArtifact.getVersion());
        assertEquals(artifact.getClassifier(), dbArtifact.getClassifier());
        assertEquals(artifact.getType(), dbArtifact.getType());
        assertEquals(artifact.getExtension(), dbArtifact.getExtension());
        assertEquals(artifact.getSize(), dbArtifact.getSize());
        assertEquals(artifact.getDownloadUrl(), dbArtifact.getDownloadUrl());
        assertEquals(artifact.getProvider(), dbArtifact.getProvider());

    }

    @Test
    public void testGetArtifact(){
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId("groupId");
        dbArtifact.setArtifactId("artifactId");
        dbArtifact.setVersion("1.0.0-SNAPSHOT");
        dbArtifact.setClassifier("win");
        dbArtifact.setType("component");
        dbArtifact.setExtension("jar");
        dbArtifact.setDownloadUrl("nowhere");
        dbArtifact.setSize("10Mo");
        dbArtifact.setProvider("provider");

        final DbLicense license = new DbLicense();
        license.setName("licenseId");
        dbArtifact.addLicense(license);

        final Artifact artifact = DataUtils.getArtifact(dbArtifact);

        assertEquals(dbArtifact.getGroupId(), artifact.getGroupId());
        assertEquals(dbArtifact.getArtifactId(), artifact.getArtifactId());
        assertEquals(dbArtifact.getVersion(), artifact.getVersion());
        assertEquals(dbArtifact.getClassifier(), artifact.getClassifier());
        assertEquals(dbArtifact.getType(), artifact.getType());
        assertEquals(dbArtifact.getExtension(), artifact.getExtension());
        assertEquals(dbArtifact.getSize(), artifact.getSize());
        assertEquals(dbArtifact.getDownloadUrl(), artifact.getDownloadUrl());
        assertEquals(dbArtifact.getProvider(), artifact.getProvider());
        assertEquals(1, artifact.getLicenses().size());
        assertEquals("licenseId", artifact.getLicenses().get(0));

    }

    @Test
    public void getDbModule(){
        final Module module = DataModelFactory.createModule("root", "1.0.0-SNAPSHOT");
        final Artifact artifact = DataModelFactory.createArtifact("com.axway.root", "artifact1", "1.0.0-SNAPSHOT", "win", "component", "jar");
        module.addArtifact(artifact);

        final Artifact thirdparty = DataModelFactory.createArtifact("org.apache", "all", "6.8.0-5426", "", "", "jar");
        final Dependency dependency = DataModelFactory.createDependency(thirdparty, Scope.COMPILE);
        module.addDependency(dependency);

        final Module submodule = DataModelFactory.createModule("sub1", "1.0.0-SNAPSHOT");
        final Artifact artifact2 = DataModelFactory.createArtifact("com.axway.root.sub1", "artifactSub1", "1.0.0-SNAPSHOT", "", "", "jar");
        submodule.addArtifact(artifact2);
        final Artifact thirdparty2 = DataModelFactory.createArtifact("org.lol", "all", "1.2.3-4", "", "", "jar");
        final Dependency dependency2 = DataModelFactory.createDependency(thirdparty2, Scope.PROVIDED);
        submodule.addDependency(dependency2);
        module.addSubmodule(submodule);

        final DbModule dbModule = DataUtils.getDbModule(module);
        assertEquals(module.getName(), dbModule.getName());
        assertEquals(module.getVersion(), dbModule.getVersion());
        assertEquals(1, dbModule.getArtifacts().size());
        assertEquals(artifact.getGavc(), dbModule.getArtifacts().get(0));
        assertEquals(1, dbModule.getDependencies().size());
        assertEquals(thirdparty.getGavc(), dbModule.getDependencies().get(0).getTarget());
        assertEquals(DbModule.generateID(module.getName(), module.getVersion()), dbModule.getDependencies().get(0).getSource());
        assertEquals(dependency.getScope(), dbModule.getDependencies().get(0).getScope());
        assertEquals(1, dbModule.getSubmodules().size());

        final DbModule dbSubmodule = dbModule.getSubmodules().get(0);
        assertEquals("root:sub1" , dbSubmodule.getName());
        assertEquals(submodule.getVersion(), dbSubmodule.getVersion());
        assertEquals(1, dbSubmodule.getArtifacts().size());
        assertEquals(artifact2.getGavc(), dbSubmodule.getArtifacts().get(0));
        assertEquals(1, dbSubmodule.getDependencies().size());
        assertEquals(thirdparty2.getGavc(), dbSubmodule.getDependencies().get(0).getTarget());
        assertEquals(DbModule.generateID(submodule.getName(), submodule.getVersion()), dbSubmodule.getDependencies().get(0).getSource());
        assertEquals(dependency2.getScope(), dbSubmodule.getDependencies().get(0).getScope());

    }

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
        DbArtifact artifact = DataUtils.createArtifact("groupId:artifactId:1.0.0:classifier:extension");
        assertEquals("groupId", artifact.getGroupId());
        assertEquals("artifactId", artifact.getArtifactId());
        assertEquals("1.0.0", artifact.getVersion());
        assertEquals("classifier", artifact.getClassifier());
        assertEquals("extension", artifact.getExtension());


        artifact = DataUtils.createArtifact("groupId:artifactId:1.0.0::");
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
