package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC02_ModuleWithOneArtifactAndDependencyOfEachType implements DependencyCase{

	public static final String MODULE_NAME 			= "tc02Module";
	public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
	public static final String ARTIFACT_GROUPID		= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc02Module";
	public static final String ARTIFACT_ID	 		= "singleArtifact";
    public static final String ARTIFACT_EXTENSION	= "jar";
    public static final String ARTIFACT_TYPE	    = "jar";
	public static final String ARTIFACT_CLASSIFIER	= "linux";
	public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";

    public static final String COMPILE_DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST +".tc02CompDependency";
    public static final String COMPILE_DEPENDENCY_MODULE	= "tc02CompDependency";
    public static final String COMPILE_DEPENDENCY_ID	 	= "tc02CompDepArtifact";
    public static final String COMPILE_DEPENDENCY_EXTENSION = "jar";
    public static final String COMPILE_DEPENDENCY_TYPE 		= "jar";
    public static final String COMPILE_DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
    public static final Scope  COMPILE_DEPENDENCY_SCOPE 	= Scope.COMPILE;

    public static final String PROVIDED_DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST +".tc02ProDependency";
    public static final String PROVIDED_DEPENDENCY_MODULE	= "tc02ProDependency";
    public static final String PROVIDED_DEPENDENCY_ID	 	= "tc02ProDepArtifact";
    public static final String PROVIDED_DEPENDENCY_EXTENSION= "jar";
    public static final String PROVIDED_DEPENDENCY_TYPE 	= "component";
    public static final String PROVIDED_DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
    public static final Scope  PROVIDED_DEPENDENCY_SCOPE 	= Scope.PROVIDED;

    public static final String RUNTIME_DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST +".tc02RunDependency";
    public static final String RUNTIME_DEPENDENCY_MODULE	= "tc02RunDependency";
    public static final String RUNTIME_DEPENDENCY_ID	 	= "tc02RunDepArtifact";
    public static final String RUNTIME_DEPENDENCY_EXTENSION = "jar";
    public static final String RUNTIME_DEPENDENCY_TYPE 		= "jar";
    public static final String RUNTIME_DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
    public static final Scope  RUNTIME_DEPENDENCY_SCOPE 	= Scope.RUNTIME;

    public static final String TEST_DEPENDENCY_GROUPID	= GrapesTestUtils.CORPORATE_GROUPID_4TEST +".tc02TestDependency";
    public static final String TEST_DEPENDENCY_MODULE	= "tc02TestDependency";
    public static final String TEST_DEPENDENCY_ID	 	= "tc02TestDepArtifact";
    public static final String TEST_DEPENDENCY_EXTENSION= "jar";
    public static final String TEST_DEPENDENCY_TYPE 	= "jar";
    public static final String TEST_DEPENDENCY_VERSION 	= "1.0.0-SNAPSHOT";
    public static final Scope  TEST_DEPENDENCY_SCOPE 	= Scope.TEST;
	
	private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();

	public TC02_ModuleWithOneArtifactAndDependencyOfEachType() {
		DbArtifact artifact = new DbArtifact();
		artifact.setGroupId(ARTIFACT_GROUPID);
		artifact.setArtifactId(ARTIFACT_ID);
		artifact.setVersion(ARTIFACT_VERSION);
        artifact.setType(ARTIFACT_TYPE);
        artifact.setExtension(ARTIFACT_EXTENSION);
		artifact.setClassifier(ARTIFACT_CLASSIFIER);
		
		DbModule module = new DbModule();
		module.setName(MODULE_NAME);
		module.setVersion(MODULE_VERSION);
		module.addArtifact(artifact);

        DbArtifact compDependency = new DbArtifact();
        compDependency.setGroupId(COMPILE_DEPENDENCY_GROUPID);
        compDependency.setArtifactId(COMPILE_DEPENDENCY_ID);
        compDependency.setVersion(COMPILE_DEPENDENCY_VERSION);
        compDependency.setType(COMPILE_DEPENDENCY_TYPE);
        compDependency.setExtension(COMPILE_DEPENDENCY_EXTENSION);

        DbModule compModule = new DbModule();
        compModule.setName(COMPILE_DEPENDENCY_MODULE);
        compModule.setVersion(COMPILE_DEPENDENCY_VERSION);
        compModule.addArtifact(compDependency);

        module.addDependency(compDependency.getGavc(), Scope.COMPILE);


        DbArtifact proDependency = new DbArtifact();
        proDependency.setGroupId(PROVIDED_DEPENDENCY_GROUPID);
        proDependency.setArtifactId(PROVIDED_DEPENDENCY_ID);
        proDependency.setVersion(PROVIDED_DEPENDENCY_VERSION);
        proDependency.setType(PROVIDED_DEPENDENCY_TYPE);
        proDependency.setExtension(PROVIDED_DEPENDENCY_EXTENSION);

        DbModule proModule = new DbModule();
        proModule.setName(PROVIDED_DEPENDENCY_MODULE);
        proModule.setVersion(PROVIDED_DEPENDENCY_VERSION);
        proModule.addArtifact(proDependency);

        module.addDependency(proDependency.getGavc(), Scope.PROVIDED);


        DbArtifact runDependency = new DbArtifact();
        runDependency.setGroupId(RUNTIME_DEPENDENCY_GROUPID);
        runDependency.setArtifactId(RUNTIME_DEPENDENCY_ID);
        runDependency.setVersion(RUNTIME_DEPENDENCY_VERSION);
        runDependency.setType(RUNTIME_DEPENDENCY_TYPE);
        runDependency.setExtension(RUNTIME_DEPENDENCY_EXTENSION);

        DbModule runModule = new DbModule();
        runModule.setName(RUNTIME_DEPENDENCY_MODULE);
        runModule.setVersion(RUNTIME_DEPENDENCY_VERSION);
        runModule.addArtifact(runDependency);

        module.addDependency(runDependency.getGavc(), Scope.RUNTIME);


        DbArtifact testDependency = new DbArtifact();
        testDependency.setGroupId(TEST_DEPENDENCY_GROUPID);
        testDependency.setArtifactId(TEST_DEPENDENCY_ID);
        testDependency.setVersion(TEST_DEPENDENCY_VERSION);
        testDependency.setType(TEST_DEPENDENCY_TYPE);
        testDependency.setExtension(TEST_DEPENDENCY_EXTENSION);

        DbModule testModule = new DbModule();
        testModule.setName(TEST_DEPENDENCY_MODULE);
        testModule.setVersion(TEST_DEPENDENCY_VERSION);
        testModule.addArtifact(testDependency);

        module.addDependency(testDependency.getGavc(), Scope.TEST);

        artifacts.add(artifact);
        artifacts.add(compDependency);
        artifacts.add(proDependency);
        artifacts.add(runDependency);
		artifacts.add(testDependency);
        modules.add(module);
        modules.add(compModule);
        modules.add(proModule);
        modules.add(runModule);
		modules.add(testModule);
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
