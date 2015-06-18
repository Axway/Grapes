package org.axway.grapes.model.utils;

import org.axway.grapes.model.datamodel.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonUtilsTest {

    @Test
    public void testOrganizationSerializationUnserialization() throws Exception {
        Organization organization = DataModelFactory.createOrganization("name");
        Organization organization2 = null;
        Exception exception = null;

        try{
            String serializedLicense = JsonUtils.serialize(organization);
            organization2 = JsonUtils.unserializeOrganization(serializedLicense);
        }catch (Exception e) {
            exception = e;
            System.out.println(e);
        }

        assertNull(exception);
        assertNotNull(organization2);
        assertTrue(organization.equals(organization2));
    }

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
//this causes a circulare dependency
        assertNull(exception);
        assertNotNull(module2);
        assertTrue(module1.equals(module2));
    }

    @Test
    public void testBuildInfoSerializationUnserialization() throws Exception {
        final Map<String, String> buildInfo = new HashMap<String, String>();
        buildInfo.put("test","test.test.test");

        Map<String, String> buildInfo2 = null;
        Exception exception = null;

        try{
            String serializedBuildInfo = JsonUtils.serialize(buildInfo);
            buildInfo2 = JsonUtils.unserializeBuildInfo(serializedBuildInfo);
        }catch (Exception e) {
            exception = e;
            System.out.println(e);
        }

        assertNull(exception);
        assertNotNull(buildInfo2);
        assertTrue(buildInfo.equals(buildInfo2));
    }
}
