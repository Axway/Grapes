package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC07_ModuleWithOneArtifactWithLoopDependencies implements DependencyCase{

	public static final String MODULE_NAME 			= "tc07Module";
	public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
	public static final String ARTIFACT_GROUPID		= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc07Module";
	public static final String ARTIFACT_ID	 		= "singleArtifact";
	public static final String ARTIFACT_PKG 		= "jar";
	public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
	
	public static final String DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc07Dependency";
	public static final String DEPENDENCY_MODULE	= "tc07Dependency";
	public static final String DEPENDENCY_ID		= "tc07DepArtifact";
	public static final String DEPENDENCY_PKG 		= "jar";
	public static final String DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
	public static final Scope  DEPENDENCY_SCOPE 	= Scope.COMPILE;

	private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();
	
	public TC07_ModuleWithOneArtifactWithLoopDependencies() {
		DbArtifact artifact = new DbArtifact();
		artifact.setGroupId(ARTIFACT_GROUPID);
		artifact.setArtifactId(ARTIFACT_ID);
		artifact.setVersion(ARTIFACT_VERSION);
		artifact.setType(ARTIFACT_PKG);
		
		DbModule module = new DbModule();
		module.setName(MODULE_NAME);
		module.setVersion(MODULE_VERSION);
		module.addArtifact(artifact);

		DbArtifact dependency = new DbArtifact();
		dependency.setGroupId(DEPENDENCY_GROUPID);
		dependency.setArtifactId(DEPENDENCY_ID);
		dependency.setVersion(DEPENDENCY_VERSION);
		dependency.setType(DEPENDENCY_PKG);
		
		DbModule depModule = new DbModule();
		depModule.setName(DEPENDENCY_MODULE);
		depModule.setVersion(DEPENDENCY_VERSION);
		depModule.addArtifact(dependency);

        depModule.addDependency(artifact.getGavc(), Scope.COMPILE);
		module.addDependency(dependency.getGavc(), Scope.COMPILE);

		artifacts.add(artifact);
		artifacts.add(dependency);
		
		modules.add(module);
		modules.add(depModule);
		
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
