package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC01_ModuleWithOneArtifactWithoutDependency implements DependencyCase{

	public static final String MODULE_NAME 			= "tc01Module";
	public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
	public static final String ARTIFACT_GROUPID		= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc01Module";
	public static final String ARTIFACT_ID	 		= "singleArtifact";
	public static final String ARTIFACT_EXTENSION = "pom";
	public static final String ARTIFACT_CLASSIFIER	= "linux";
    public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
    public static final String ARTIFACT_TYPE 	    = "";
	
	private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();
	
	public TC01_ModuleWithOneArtifactWithoutDependency() {
		DbArtifact artifact = new DbArtifact();
		artifact.setGroupId(ARTIFACT_GROUPID);
		artifact.setArtifactId(ARTIFACT_ID);
		artifact.setVersion(ARTIFACT_VERSION);
		artifact.setClassifier(ARTIFACT_CLASSIFIER);
        artifact.setExtension(ARTIFACT_EXTENSION);
        artifact.setType(ARTIFACT_TYPE);
		
		DbModule module = new DbModule();
		module.setName(MODULE_NAME);
		module.setVersion(MODULE_VERSION);
		module.addArtifact(artifact);
        module.setOrganization(GrapesTestUtils.ORGANIZATION_NAME_4TEST);
		
		modules.add(module);
		artifacts.add(artifact);
	}

	@Override
	public List<DbArtifact> dbArtifactsToLoad() {
		return artifacts;
	}

	@Override
	public List<DbModule> dbModulesToLoad() {
		return modules;
	}

    @Override
    public List<DbLicense> dbLicensesToLoad() {
        return licenses;
    }

    public static Module getModule(){
		Module module = DataModelFactory.createModule(MODULE_NAME, MODULE_VERSION);
		Artifact artifact = DataModelFactory.createArtifact(ARTIFACT_GROUPID, ARTIFACT_ID, ARTIFACT_VERSION, ARTIFACT_CLASSIFIER, ARTIFACT_TYPE, ARTIFACT_EXTENSION);
		module.addArtifact(artifact);
		
		return module;
	}

}
