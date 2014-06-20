package org.axway.grapes.server.materials.cases;

import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public class TC05_ModuleWithOneArtifactAndThirdParty implements DependencyCase{

	public static final String MODULE_NAME 			= "tc05Module";
	public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
	public static final String ARTIFACT_GROUPID		= GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".tc05Module";
	public static final String ARTIFACT_ID	 		= "singleArtifact";
    public static final String ARTIFACT_TYPE 		= "jar";
    public static final String ARTIFACT_EXTENSION	= "jar";
	public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
	
	public static final String COMPILE_THIRDPARTY_GROUPID	= "org.test.tc05CompThirdParty";
	public static final String COMPILE_THIRDPARTY_ID	 	= "tc05CompThirdParty";
    public static final String COMPILE_THIRDPARTY_TYPE 		= "jar";
    public static final String COMPILE_THIRDPARTY_EXTENSION = "jar";
	public static final String COMPILE_THIRDPARTY_VERSION 	= "0.8.1-12";
	public static final Scope  COMPILE_THIRDPARTY_SCOPE		= Scope.COMPILE;
	
	public static final String RUNTIME_THIRDPARTY_GROUPID	= "org.test.tc05RunThirdParty";
	public static final String RUNTIME_THIRDPARTY_ID	 	= "tc05RunThirdParty";
    public static final String RUNTIME_THIRDPARTY_TYPE 		= "jar";
    public static final String RUNTIME_THIRDPARTY_EXTENSION	= "jar";
	public static final String RUNTIME_THIRDPARTY_VERSION 	= "1.0.0-15";
	public static final Scope  RUNTIME_THIRDPARTY_SCOPE 	= Scope.RUNTIME;
	
	public static final String PROVIDED_THIRDPARTY_GROUPID	= "org.test.tc05ProThirdParty";
	public static final String PROVIDED_THIRDPARTY_ID	 	= "tc05ProThirdParty";
    public static final String PROVIDED_THIRDPARTY_TYPE		= "jar";
    public static final String PROVIDED_THIRDPARTY_EXTENSION= "jar";
	public static final String PROVIDED_THIRDPARTY_VERSION 	= "3.8.0";
	public static final Scope  PROVIDED_THIRDPARTY_SCOPE	= Scope.PROVIDED;
	
	public static final String TEST_THIRDPARTY_GROUPID	= "org.test.tc05TesThirdParty";
	public static final String TEST_THIRDPARTY_ID	 	= "tc05TesThirdParty";
    public static final String TEST_THIRDPARTY_TYPE		= "jar";
    public static final String TEST_THIRDPARTY_EXTENSION= "jar";
	public static final String TEST_THIRDPARTY_VERSION 	= "0.8.1-SNAPSHOT";
	public static final Scope  TEST_THIRDPARTY_SCOPE 	= Scope.TEST;
	
	public static final String LICENSE_NAME 		= "name";
	public static final String LICENSE_LONG_NAME 	= "longName";
	public static final String LICENSE_REGEXP 		= "regexp";
	public static final String LICENSE_URL 			= "url";
	public static final String LICENSE_COMMENT 		= "blablabla";

	private final List<DbModule> modules = new ArrayList<DbModule>();
    private final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();
    private final List<DbLicense> licenses = new ArrayList<DbLicense>();
	
	public TC05_ModuleWithOneArtifactAndThirdParty() {
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
        module.setOrganization(GrapesTestUtils.ORGANIZATION_NAME_4TEST);
		
		DbArtifact compDependency = new DbArtifact();
		compDependency.setGroupId(COMPILE_THIRDPARTY_GROUPID);
		compDependency.setArtifactId(COMPILE_THIRDPARTY_ID);
		compDependency.setVersion(COMPILE_THIRDPARTY_VERSION);
        compDependency.setType(COMPILE_THIRDPARTY_TYPE);
        compDependency.setExtension(COMPILE_THIRDPARTY_EXTENSION);
		
		DbLicense license = getDbLicense();		
		compDependency.addLicense(license);

		DbArtifact runDependency = new DbArtifact();
		runDependency.setGroupId(RUNTIME_THIRDPARTY_GROUPID);
		runDependency.setArtifactId(RUNTIME_THIRDPARTY_ID);
		runDependency.setVersion(RUNTIME_THIRDPARTY_VERSION);
        runDependency.setType(RUNTIME_THIRDPARTY_TYPE);
        runDependency.setExtension(RUNTIME_THIRDPARTY_EXTENSION);

		DbArtifact proDependency = new DbArtifact();
		proDependency.setGroupId(PROVIDED_THIRDPARTY_GROUPID);
		proDependency.setArtifactId(PROVIDED_THIRDPARTY_ID);
		proDependency.setVersion(PROVIDED_THIRDPARTY_VERSION);
        proDependency.setType(PROVIDED_THIRDPARTY_TYPE);
        proDependency.setExtension(PROVIDED_THIRDPARTY_EXTENSION);

		DbArtifact testDependency = new DbArtifact();
		testDependency.setGroupId(TEST_THIRDPARTY_GROUPID);
		testDependency.setArtifactId(TEST_THIRDPARTY_ID);
		testDependency.setVersion(TEST_THIRDPARTY_VERSION);
        testDependency.setType(TEST_THIRDPARTY_TYPE);
        testDependency.setExtension(TEST_THIRDPARTY_EXTENSION);

        module.addDependency(compDependency.getGavc(), Scope.COMPILE);
        module.addDependency(proDependency.getGavc(), Scope.PROVIDED);
        module.addDependency(runDependency.getGavc(), Scope.RUNTIME);
        module.addDependency(testDependency.getGavc(), Scope.TEST);

		artifacts.add(artifact);
		artifacts.add(compDependency);
		artifacts.add(runDependency);
		artifacts.add(proDependency);
		artifacts.add(testDependency);
		
		modules.add(module);
	}
	
	static public DbLicense getDbLicense() {
		final DbLicense license = new DbLicense();
		license.setName(LICENSE_NAME);
		license.setLongName(LICENSE_LONG_NAME);
		license.setRegexp(LICENSE_REGEXP);
		license.setUrl(LICENSE_URL);
		license.setComments(LICENSE_COMMENT);
		return license;
	}
	
	static public License getLicense() {
		return DataModelFactory.createLicense(LICENSE_NAME, LICENSE_LONG_NAME, LICENSE_COMMENT, LICENSE_REGEXP, LICENSE_URL);
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
