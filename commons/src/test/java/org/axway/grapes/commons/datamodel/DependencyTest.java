package org.axway.grapes.commons.datamodel;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DependencyTest {
	
	@Test
	public void checkIfTwoArtifactsAreEquals(){
		Artifact artifact = DataModelFactory.createArtifact("com.my.company", "test", "1.0.0-SNAPSHOT", "win32", "jar", "jar");
		Artifact artifact2 = DataModelFactory.createArtifact("com.my.company", "test", "1.0.0-SNAPSHOT", "win32", "jar", "jar");

		Dependency dep1 = DataModelFactory.createDependency(artifact, Scope.TEST);
		Dependency dep2 = DataModelFactory.createDependency(artifact2, Scope.TEST);
		Dependency dep3 = DataModelFactory.createDependency(artifact2, Scope.COMPILE);

		assertTrue(dep1.equals(dep2));
		assertFalse(dep1.equals(dep3));
		assertFalse(dep1.equals("test"));
	}

}


