package org.axway.grapes.commons.utils;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.commons.reports.DependencyList;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonUtilsTest {
	
	@Test
	public void testLicenseSerializationUnserialization() throws Exception {
		License license = DataModelFactory.createLicense("name", "longName", "comments", "regexp", "url");
		License license2 = null;
		Exception exception = null;
		
		try{
			String serializedLicense = JsonUtils.serialize(license);
			license2 = JsonUtils.unserializeLicense(serializedLicense);		
		}catch (Exception e) {
			exception = e;
			System.out.println(e);
		} 

		assertNull(exception);
		assertNotNull(license2);
		assertTrue(license.equals(license2));
	}
	
	@Test
	public void testArtifactSerializationUnserialization() throws Exception {
		Artifact artifact = DataModelFactory.createArtifact("com.my.company", "artifact1", "1.0.0-SNAPSHOT", null, "jar", null);
		artifact.addLicense("license1");
		artifact.setDownloadUrl("www.www.www");
		artifact.setSize("1Go");
		
		Artifact artifact2 = null;
		Exception exception = null;
		
		try{
			String serializedArtifact = JsonUtils.serialize(artifact);
			artifact2 = JsonUtils.unserializeArtifact(serializedArtifact);		
		}catch (Exception e) {
			exception = e;
			System.out.println(e);
		} 

		assertNull(exception);
		assertNotNull(artifact2);
		assertTrue(artifact.equals(artifact2));
	}

	@Test
	public void testModuleSerializationUnserialization() throws Exception {
		Module module1 = DataModelFactory.createModule("module", "1.0.0-SNAPSHOT");
		Artifact artifact1 = DataModelFactory.createArtifact("com.my.company", "artifact1", "1.0.0-SNAPSHOT", null, "jar", "jar");
		Artifact artifact2 = DataModelFactory.createArtifact("com.my.company", "artifact2", "1.0.0-SNAPSHOT", null, "jar", "jar");
		
		Artifact dependency1 = DataModelFactory.createArtifact("com.my.company", "dependency1", "1.0.0-SNAPSHOT", null, "jar", "jar");
		Artifact dependency2 = DataModelFactory.createArtifact("com.my.company", "dependency2", "1.0.0-SNAPSHOT", null, "jar", "jar");
		Artifact dependency3 = DataModelFactory.createArtifact("com.my.company", "dependency3", "1.0.0-SNAPSHOT", null, "jar", "jar");
		Artifact dependency4 = DataModelFactory.createArtifact("com.my.company", "dependency4", "1.0.0-SNAPSHOT", null, "jar", "jar");

		module1.addDependency(DataModelFactory.createDependency(dependency1, Scope.COMPILE));
		module1.addDependency(DataModelFactory.createDependency(dependency2, Scope.PROVIDED));
		module1.addDependency(DataModelFactory.createDependency(dependency3, Scope.RUNTIME));
		module1.addDependency(DataModelFactory.createDependency(dependency4, Scope.TEST));
		
		module1.addArtifact(artifact1);
		module1.addArtifact(artifact2);
		
		Module module2 = null;
		Exception exception = null;
		
		try{
			String serializedModule = JsonUtils.serialize(module1);
			module2 = JsonUtils.unserializeModule(serializedModule);			
		}catch (Exception e) {
			exception = e;
			System.out.println(e);
		} 

		assertNull(exception);
		assertNotNull(module2);
		assertTrue(module1.equals(module2));
	}

	@Test
	public void testDependencyListUnserialization() throws Exception {
		final DependencyList list = new DependencyList();
		
		final Dependency dependency = DataModelFactory.createDependency(DataModelFactory.createArtifact("groupId", "artifactId", "version", "classifier", "type", "extention"), Scope.COMPILE);
		list.addDependency(dependency);
		
		Exception exception = null;
		
		try{
			String serializedDep = JsonUtils.serialize(list);
			JsonUtils.unserializeDependencyList(serializedDep);		
		}catch (Exception e) {
			exception = e;
		} 

		assertNull(exception);
	}
}
