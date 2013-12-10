package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC06_ModuleWithOneArtifactWithTransitiveDependencies implements DependencyCase{

	public static final String MODULE_NAME 		    = "tc06Module";
	public static final String MODULE_VERSION 	    = "1.0.0-SNAPSHOT";
	public static final String ARTIFACT_GROUPID	    = GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc06Module";
	public static final String ARTIFACT_ID	 	    = "singleArtifact";
    public static final String ARTIFACT_TYPE 		= "jar";
    public static final String ARTIFACT_EXTENSION	= "jar";
	public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
	
	public static final String DEPENDENCY_GROUPID		= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc06Dependency";
	public static final String DEPENDENCY_MODULE		= "tc06Dependency";
	public static final String DEPENDENCY_ID	 		= "tc06DepArtifact";
    public static final String DEPENDENCY_TYPE 		    = "jar";
    public static final String DEPENDENCY_EXTENSION	    = "jar";
	public static final String DEPENDENCY_VERSION 	    = "1.0.0-SNAPSHOT";
	public static final Scope  DEPENDENCY_SCOPE 		= Scope.COMPILE;
	
	public static final String TRANSITIVE_DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc06TransDependency";
	public static final String TRANSITIVE_DEPENDENCY_MODULE	    = "tc06TransDependency";
	public static final String TRANSITIVE_DEPENDENCY_ID	 	    = "tc06TransDepArtifact";
    public static final String TRANSITIVE_DEPENDENCY_TYPE 		= "jar";
    public static final String TRANSITIVE_DEPENDENCY_EXTENSION	= "jar";
	public static final String TRANSITIVE_DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
	public static final Scope  TRANSITIVE_DEPENDENCY_SCOPE 	= Scope.COMPILE;

	private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();
	
	public TC06_ModuleWithOneArtifactWithTransitiveDependencies() {
		DbArtifact artifact = new DbArtifact();
		artifact.setGroupId(ARTIFACT_GROUPID);
		artifact.setArtifactId(ARTIFACT_ID);
		artifact.setVersion(ARTIFACT_VERSION);
        artifact.setType(ARTIFACT_TYPE);
        artifact.setExtension(ARTIFACT_EXTENSION);
		
		DbModule module = new DbModule();
		module.setName(MODULE_NAME);
		module.setVersion(MODULE_VERSION);
		module.addArtifact(artifact);

		DbArtifact dependency = new DbArtifact();
		dependency.setGroupId(DEPENDENCY_GROUPID);
		dependency.setArtifactId(DEPENDENCY_ID);
		dependency.setVersion(DEPENDENCY_VERSION);
        dependency.setType(DEPENDENCY_TYPE);
        dependency.setExtension(DEPENDENCY_EXTENSION);
		
		DbModule depModule = new DbModule();
		depModule.setName(DEPENDENCY_MODULE);
		depModule.setVersion(DEPENDENCY_VERSION);
		depModule.addArtifact(dependency);

		DbArtifact transDependency = new DbArtifact();
		transDependency.setGroupId(TRANSITIVE_DEPENDENCY_GROUPID);
		transDependency.setArtifactId(TRANSITIVE_DEPENDENCY_ID);
		transDependency.setVersion(TRANSITIVE_DEPENDENCY_VERSION);
        transDependency.setType(TRANSITIVE_DEPENDENCY_TYPE);
        transDependency.setExtension(TRANSITIVE_DEPENDENCY_EXTENSION);
		
		DbModule transModule = new DbModule();
		transModule.setName(TRANSITIVE_DEPENDENCY_MODULE);
		transModule.setVersion(TRANSITIVE_DEPENDENCY_VERSION);
		transModule.addArtifact(transDependency);
		
		depModule.addDependency(transDependency.getGavc(), Scope.COMPILE);
        module.addDependency(dependency.getGavc(), Scope.COMPILE);

		artifacts.add(artifact);
		artifacts.add(dependency);
		artifacts.add(transDependency);
		
		modules.add(module);
		modules.add(depModule);
		modules.add(transModule);
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

}
