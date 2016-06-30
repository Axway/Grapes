package org.axway.grapes.commons.datamodel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;



public class ModuleTest {

    @Test
    public void checkIfArtifactsAreCorrectlyAddedToTheModule(){
        Module module = new Module();
        module.setName("name");
        module.setVersion("1.0.0-1");

        assertEquals(0, module.getArtifacts().size());

        module.addArtifact(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "1.0.0-SNAPSHOT", null, null, null));
        assertEquals(1, module.getArtifacts().size());

        Artifact artifact = module.getArtifacts().iterator().next();
    }

    @Test
    public void checkIfASetOfArtifactAreCorrectlyAddedToTheModule(){
        Module module = new Module();
        assertEquals(0, module.getArtifacts().size());

        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "1.0.0-SNAPSHOT", null, null, null));
        artifacts.add(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "2.0.0-SNAPSHOT", null, null, null));

        module.addAllArtifacts(artifacts);
        assertEquals(2, module.getArtifacts().size());
    }

    @Test
    public void checkIfArtifactsAreAddedOnlyOnceToTheModule(){
        Module module = new Module();
        assertEquals(0, module.getArtifacts().size());

        module.addArtifact(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "1.0.0-SNAPSHOT", null, null, null));
        assertEquals(1, module.getArtifacts().size());

        module.addArtifact(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "1.0.0-SNAPSHOT", null, null, null));
        assertEquals(1, module.getArtifacts().size());
    }

    @Test
    public void checkIfSubmoduleAreCorrectlyAddedToTheModule(){
        Module module = new Module();
        module.setName("name");
        module.setVersion("1.0.0-1");
        module.setPromoted(true);

        assertEquals(0, module.getArtifacts().size());

        module.addSubmodule(DataModelFactory.createModule("submodule", "version"));
        module.addSubmodule(DataModelFactory.createModule("submodule", "version"));
        assertEquals(1, module.getSubmodules().size());

        Module submodule = module.getSubmodules().iterator().next();
        assertEquals("submodule", submodule.getName());
        assertEquals("version", submodule.getVersion());
        assertEquals(true, submodule.isPromoted());
    }

    @Test
    public void setModulePromotion(){
        Module module = new Module();
        module.addArtifact(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "1.0.0-SNAPSHOT", null, null, null));
        module.addSubmodule(DataModelFactory.createModule("submodule", "version"));

        assertFalse(module.isPromoted());
        assertFalse(module.getArtifacts().iterator().next().isPromoted());

        module.setPromoted(true);

        assertTrue(module.isPromoted());
        assertTrue(module.getArtifacts().iterator().next().isPromoted());
        assertTrue(module.getSubmodules().iterator().next().isPromoted());
    }

    @Test
    public void checkWhenModulesAreEquals(){
        Module module = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        Module otherModule = new Module();
        assertFalse(module.equals(otherModule));

        otherModule.setName("module");
        assertFalse(module.equals(otherModule));

        otherModule.setVersion("1.0.0-SNAPSHOT");
        assertTrue(module.equals(otherModule));

        assertFalse(module.equals("test"));
    }

    @Test
    public void ifModuleIsPromotedTheAddedArtifactsArePromotedAlso(){
        Module module = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
        module.setPromoted(true);
        module.addArtifact(DataModelFactory.createArtifact("com.axwa.ecd.d2d.test", "test", "1.0.0-SNAPSHOT", "", "", null));

        assertTrue(module.getArtifacts().iterator().next().isPromoted());

    }

    @Test
    public void addNewDependency(){
        Module module = new Module();
        module.setName("name");
        module.setVersion("1.0.0-1");
        Artifact dependency = new Artifact();

        dependency.setArtifactId("test");
        dependency.setGroupId("com.my.company");
        dependency.setVersion("1.0.0-SNAPSHOT");

        assertEquals(0, module.getDependencies().size());

        module.addDependency(DataModelFactory.createDependency(dependency, Scope.COMPILE));
        assertEquals(1, module.getDependencies().size());

        Dependency dep = module.getDependencies().iterator().next();
    }

    @Test
    public void ifAddManyTimeTheSameDependency(){
        Module module = new Module();
        Artifact dependency = new Artifact();

        dependency.setArtifactId("test");
        dependency.setGroupId("com.my.company");
        dependency.setVersion("1.0.0-SNAPSHOT");

        assertEquals(0, module.getDependencies().size());

        module.addDependency(DataModelFactory.createDependency(dependency, Scope.COMPILE));
        module.addDependency(DataModelFactory.createDependency(dependency, Scope.COMPILE));
        assertEquals(1, module.getDependencies().size());
    }
    
    @Test
    public void testDelivery(){
    	Delivery delivery = new Delivery();
    	delivery.setCommercialName("test Name");
    	delivery.setCommercialVersion("1.0.0");
    	
        Module module = new Module();
    	module.setDeliveries(delivery);
    	
    	assertEquals(2, module.deliveryStatusCount());
    	
    	delivery.setCommercialName("");
    	delivery.setCommercialVersion("1.0.0");
    	module.setDeliveries(delivery);
    	
    	assertEquals(1, module.deliveryStatusCount());
    	
    	delivery.setCommercialName("");
    	delivery.setCommercialVersion("");
    	module.setDeliveries(delivery);
    	
    	assertEquals(0, module.deliveryStatusCount());
    }
}
