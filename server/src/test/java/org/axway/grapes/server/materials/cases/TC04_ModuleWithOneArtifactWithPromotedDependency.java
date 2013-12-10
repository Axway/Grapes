package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC04_ModuleWithOneArtifactWithPromotedDependency implements DependencyCase{

	public static final String MODULE_NAME 			= "tc04Module";
	public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
	
	public static final String DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc04Dependency";
	public static final String DEPENDENCY_MODULE	= "tc04Dependency";
	public static final String DEPENDENCY_ID	 	= "tc04DepArtifact";
    public static final String DEPENDENCY_TYPE 		= "jar";
    public static final String DEPENDENCY_EXTENSION	= "jar";
	public static final String DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
	public static final Scope  DEPENDENCY_SCOPE 	= Scope.COMPILE;

	private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();
	
	public TC04_ModuleWithOneArtifactWithPromotedDependency() {
		DbModule module = new DbModule();
		module.setName(MODULE_NAME);
		module.setVersion(MODULE_VERSION);

		DbArtifact dependency = new DbArtifact();
		dependency.setGroupId(DEPENDENCY_GROUPID);
		dependency.setArtifactId(DEPENDENCY_ID);
		dependency.setVersion(DEPENDENCY_VERSION);
        dependency.setType(DEPENDENCY_TYPE);
        dependency.setExtension(DEPENDENCY_EXTENSION);
		dependency.setPromoted(true);
		
		DbModule extModule = new DbModule();
		extModule.setName(DEPENDENCY_MODULE);
		extModule.setVersion(DEPENDENCY_VERSION);
		extModule.addArtifact(dependency);
		extModule.setPromoted(true);

        module.addDependency(dependency.getGavc(), Scope.COMPILE);

		artifacts.add(dependency);
		
		modules.add(module);
		modules.add(extModule);
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
