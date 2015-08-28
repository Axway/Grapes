package org.axway.grapes.jongo.datamodel;

import com.google.common.collect.Iterables;
import org.axway.grapes.jongo.datamodel.DbArtifact;
import org.axway.grapes.jongo.datamodel.DbModule;
import org.axway.grapes.model.datamodel.Scope;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DbModuleTest {

    @Test
    public void checkUID() {
        DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-2");

        assertEquals("module:1.0.0-2", module.getId());

    }

    @Test
    public void checkStaticGenerationOfUID() {
        DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-2");

        assertEquals(module.getId(), DbModule.generateID(module.getName(), module.getVersion()));

    }

    @Test
    public void checkToString() {
        DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0-2");

        assertEquals("Name: module, Version: 1.0.0-2", module.toString());

    }

    @Test
    public void checkArtifactManagement() {
        DbModule module = new DbModule();

        DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("com.axway.test");
        artifact.setArtifactId("UidTest");
        artifact.setVersion("1.0.0-SNAPSHOT");
        artifact.setClassifier("win");
        artifact.setType("jar");

        assertEquals(0, module.getArtifacts().size());

        module.addArtifact(artifact);
        assertEquals(1, module.getArtifacts().size());
        assertEquals(artifact.getGavc(), Iterables.get(module.getArtifacts(), 0));

        module.flushArtifacts();
        assertEquals(0, module.getArtifacts().size());
    }

    @Test
    public void checkDependencyManagement() {
        DbModule module = new DbModule();
        DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("test");

        module.addDependency(artifact, Scope.COMPILE);
        assertEquals(1, module.getDependencies().size());
        assertEquals(Scope.COMPILE,Iterables.get(module.getDependencies(), 0).getScope());
       assertEquals(artifact.getGavc(), Iterables.get(module.getDependencies(), 0).getTarget());

        module.flushDependencies();
        assertEquals(0, module.getDependencies().size());

    }

    @Test
    public void checkSubmoduleManagement() {
        DbModule module = new DbModule();

        DbModule module2 = new DbModule();
        module2.setName("name");
        module2.setVersion("1.0.0-3");
        module.addSubmodule(module2);

        assertEquals(1, module.getSubmodules().size());
        assertEquals(module2,Iterables.get(module.getSubmodules(),0));

        module.flushSubmodules();
        assertEquals(0, module.getSubmodules().size());
    }

    //todo @Test

    public void checkUpdateHasAndUse(){
        final DbModule module = new DbModule();

        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupId");
        artifact.setArtifactId("artifactId");
        artifact.setVersion("1");
        module.addArtifact(artifact);
        module.addDependency(artifact, Scope.COMPILE);


        final DbModule submodule = new DbModule();

        final DbArtifact artifact2 = new DbArtifact();
        artifact2.setGroupId("groupId2");
        artifact2.setArtifactId("artifactId2");
        artifact2.setVersion("1");
        submodule.addArtifact(artifact2);
        submodule.addDependency(artifact2, Scope.COMPILE);
        module.addSubmodule(submodule);

        module.updateHasAndUse();

        assertEquals(2, module.getHas().size());
        assertEquals(2, module.getUses().size());
    }
}
