package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC08_ModuleWithOneArtifactWithAncestorOfEach implements DependencyCase{

	public static final String MODULE_NAME 			= "tc08Module";
	public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
	public static final String ARTIFACT_GROUPID		= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc08Module";
	public static final String ARTIFACT_ID	 		= "singleArtifact";
	public static final String ARTIFACT_PKG 		= "jar";
	public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
	
	public static final String COMPILE_ANCESTOR_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc08CompAncestor";
	public static final String COMPILE_ANCESTOR_MODULE	= "tc08CompAncestor";
	public static final String COMPILE_ANCESTOR_ID	 	= "tc08CompAncArtifact";
	public static final String COMPILE_ANCESTOR_PKG 	= "jar";
	public static final String COMPILE_ANCESTOR_VERSION	= "1.0.0-SNAPSHOT";
	public static final Scope  COMPILE_ANCESTOR_SCOPE 	= Scope.COMPILE;
	
	public static final String PROVIDED_ANCESTOR_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc08ProAncestor";
	public static final String PROVIDED_ANCESTOR_MODULE	= "tc08ProAncestor";
	public static final String PROVIDED_ANCESTOR_ID	 	= "tc08ProAncArtifact";
	public static final String PROVIDED_ANCESTOR_PKG 	= "jar";
	public static final String PROVIDED_ANCESTOR_VERSION= "1.0.0-SNAPSHOT";
	public static final Scope  PROVIDED_ANCESTOR_SCOPE 	= Scope.COMPILE;
	
	public static final String RUNTIME_ANCESTOR_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc08RunAncestor";
	public static final String RUNTIME_ANCESTOR_MODULE	= "tc08RunAncestor";
	public static final String RUNTIME_ANCESTOR_ID	 	= "tc08RunAncArtifact";
	public static final String RUNTIME_ANCESTOR_PKG 	= "jar";
	public static final String RUNTIME_ANCESTOR_VERSION	= "1.0.0-SNAPSHOT";
	public static final Scope  RUNTIME_ANCESTOR_SCOPE 	= Scope.COMPILE;
	
	public static final String TEST_ANCESTOR_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc08TestAncestor";
	public static final String TEST_ANCESTOR_MODULE	= "tc08TestAncestor";
	public static final String TEST_ANCESTOR_ID	 	= "tc08TestAncArtifact";
	public static final String TEST_ANCESTOR_PKG 	= "jar";
	public static final String TEST_ANCESTOR_VERSION= "1.0.0-SNAPSHOT";
	public static final Scope  TEST_ANCESTOR_SCOPE 	= Scope.COMPILE;

	private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();
	
	public TC08_ModuleWithOneArtifactWithAncestorOfEach() {
		DbArtifact artifact = new DbArtifact();
		artifact.setGroupId(ARTIFACT_GROUPID);
		artifact.setArtifactId(ARTIFACT_ID);
		artifact.setVersion(ARTIFACT_VERSION);
		artifact.setType(ARTIFACT_PKG);
		
		DbModule module = new DbModule();
		module.setName(MODULE_NAME);
		module.setVersion(MODULE_VERSION);
		module.addArtifact(artifact);

		DbArtifact compAncestor = new DbArtifact();
		compAncestor.setGroupId(COMPILE_ANCESTOR_GROUPID);
		compAncestor.setArtifactId(COMPILE_ANCESTOR_ID);
		compAncestor.setVersion(COMPILE_ANCESTOR_VERSION);
		compAncestor.setType(COMPILE_ANCESTOR_PKG);
		
		DbModule compAncestorModule = new DbModule();
		compAncestorModule.setName(COMPILE_ANCESTOR_MODULE);
		compAncestorModule.setVersion(COMPILE_ANCESTOR_VERSION);
		compAncestorModule.addArtifact(compAncestor);
        compAncestorModule.addDependency(artifact.getGavc(), Scope.COMPILE);

		DbArtifact proAncestor = new DbArtifact();
		proAncestor.setGroupId(PROVIDED_ANCESTOR_GROUPID);
		proAncestor.setArtifactId(PROVIDED_ANCESTOR_ID);
		proAncestor.setVersion(PROVIDED_ANCESTOR_VERSION);
		proAncestor.setType(PROVIDED_ANCESTOR_PKG);
		
		DbModule proAncestorModule = new DbModule();
		proAncestorModule.setName(PROVIDED_ANCESTOR_MODULE);
		proAncestorModule.setVersion(PROVIDED_ANCESTOR_VERSION);
		proAncestorModule.addArtifact(proAncestor);
        proAncestorModule.addDependency(artifact.getGavc(), Scope.PROVIDED);

		DbArtifact runAncestor = new DbArtifact();
		runAncestor.setGroupId(RUNTIME_ANCESTOR_GROUPID);
		runAncestor.setArtifactId(RUNTIME_ANCESTOR_ID);
		runAncestor.setVersion(RUNTIME_ANCESTOR_VERSION);
		runAncestor.setType(RUNTIME_ANCESTOR_PKG);
		
		DbModule runAncestorModule = new DbModule();
		runAncestorModule.setName(RUNTIME_ANCESTOR_MODULE);
		runAncestorModule.setVersion(RUNTIME_ANCESTOR_VERSION);
		runAncestorModule.addArtifact(runAncestor);
        runAncestorModule.addDependency(artifact.getGavc(), Scope.RUNTIME);

		DbArtifact testAncestor = new DbArtifact();
		testAncestor.setGroupId(TEST_ANCESTOR_GROUPID);
		testAncestor.setArtifactId(TEST_ANCESTOR_ID);
		testAncestor.setVersion(TEST_ANCESTOR_VERSION);
		testAncestor.setType(TEST_ANCESTOR_PKG);
		
		DbModule testAncestorModule = new DbModule();
		testAncestorModule.setName(TEST_ANCESTOR_MODULE);
		testAncestorModule.setVersion(TEST_ANCESTOR_VERSION);
		testAncestorModule.addArtifact(testAncestor);
        testAncestorModule.addDependency(artifact.getGavc(), Scope.TEST);
		
		
		artifacts.add(artifact);
		artifacts.add(compAncestor);
		artifacts.add(runAncestor);
		artifacts.add(proAncestor);
		artifacts.add(testAncestor);
		
		modules.add(module);
		modules.add(compAncestorModule);
		modules.add(runAncestorModule);
		modules.add(proAncestorModule);
		modules.add(testAncestorModule);
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
