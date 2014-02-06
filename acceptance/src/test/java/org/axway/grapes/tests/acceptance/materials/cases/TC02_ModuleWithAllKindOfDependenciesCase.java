package org.axway.grapes.tests.acceptance.materials.cases;


import java.util.ArrayList;
import java.util.List;

public class TC02_ModuleWithAllKindOfDependenciesCase implements TestCase {

    public static final String MODULE_NAME 			= "tc02Module";
    public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";

    public static final String SUB_MODULE_NAME 			= "tc02Module:sub";
    public static final String SUB_MODULE_VERSION 		= MODULE_VERSION;

    public static final String SUB_SUB_MODULE_NAME 			= "tc02Module:sub:sub";
    public static final String SUB_SUB_MODULE_VERSION 		= MODULE_VERSION;

    public static final String COMP_DEPENDENCY_GROUPID		= TEST_GROUPID + ".test.anotherModule";
    public static final String COMP_DEPENDENCY_ID	 		= "compDep";
    public static final String COMP_DEPENDENCY_EXTENSION    = "dsq";
    public static final String COMP_DEPENDENCY_CLASSIFIER	= "linux";
    public static final String COMP_DEPENDENCY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String COMP_DEPENDENCY_TYPE 	    = "";

    public static final String RUN_DEPENDENCY_GROUPID		= TEST_GROUPID + ".test.anotherModule";
    public static final String RUN_DEPENDENCY_ID	 		= "runDep";
    public static final String RUN_DEPENDENCY_EXTENSION     = "pom";
    public static final String RUN_DEPENDENCY_CLASSIFIER	= "";
    public static final String RUN_DEPENDENCY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String RUN_DEPENDENCY_TYPE 	        = "pom";

    public static final String TEST_DEPENDENCY_GROUPID		= TEST_GROUPID + ".test.anotherModule";
    public static final String TEST_DEPENDENCY_ID	 		= "testDep";
    public static final String TEST_DEPENDENCY_EXTENSION    = "jar";
    public static final String TEST_DEPENDENCY_CLASSIFIER	= "";
    public static final String TEST_DEPENDENCY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String TEST_DEPENDENCY_TYPE 	    = "jar";

    public static final String PRO_DEPENDENCY_GROUPID		= TEST_GROUPID + ".test.anotherModule";
    public static final String PRO_DEPENDENCY_ID	 		= "proDep";
    public static final String PRO_DEPENDENCY_EXTENSION     = "jar";
    public static final String PRO_DEPENDENCY_CLASSIFIER	= "";
    public static final String PRO_DEPENDENCY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String PRO_DEPENDENCY_TYPE 	        = "";

    public static final String COMP_THIRDPARTY_GROUPID		= "org.somewhere.tc02";
    public static final String COMP_THIRDPARTY_ID	 		= "compDep";
    public static final String COMP_THIRDPARTY_EXTENSION    = "dsq";
    public static final String COMP_THIRDPARTY_CLASSIFIER	= "linux";
    public static final String COMP_THIRDPARTY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String COMP_THIRDPARTY_TYPE 	    = "";

    public static final String RUN_THIRDPARTY_GROUPID		= "org.somewhere.tc02";
    public static final String RUN_THIRDPARTY_ID	 		= "runDep";
    public static final String RUN_THIRDPARTY_EXTENSION     = "pom";
    public static final String RUN_THIRDPARTY_CLASSIFIER	= "";
    public static final String RUN_THIRDPARTY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String RUN_THIRDPARTY_TYPE 	        = "pom";

    public static final String TEST_THIRDPARTY_GROUPID		= "org.somewhere.tc02";
    public static final String TEST_THIRDPARTY_ID	 		= "testDep";
    public static final String TEST_THIRDPARTY_EXTENSION    = "jar";
    public static final String TEST_THIRDPARTY_CLASSIFIER	= "";
    public static final String TEST_THIRDPARTY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String TEST_THIRDPARTY_TYPE 	    = "jar";

    public static final String PRO_THIRDPARTY_GROUPID		= "org.somewhere.tc02";
    public static final String PRO_THIRDPARTY_ID	 		= "proDep";
    public static final String PRO_THIRDPARTY_EXTENSION     = "jar";
    public static final String PRO_THIRDPARTY_CLASSIFIER	= "";
    public static final String PRO_THIRDPARTY_VERSION 	    = "1.0.0-SNAPSHOT";
    public static final String PRO_THIRDPARTY_TYPE 	        = "";

    private final List<Artifact> artifacts = new ArrayList<Artifact>();
    private final List<Module> modules = new ArrayList<Module>();
    private final List<License> licenses = new ArrayList<License>();

    public TC02_ModuleWithAllKindOfDependenciesCase() {
        final Artifact artifact1 = DataModelFactory.createArtifact(COMP_DEPENDENCY_GROUPID, COMP_DEPENDENCY_ID, COMP_DEPENDENCY_VERSION, COMP_DEPENDENCY_CLASSIFIER, COMP_DEPENDENCY_TYPE, COMP_DEPENDENCY_EXTENSION);
        final Artifact artifact2 = DataModelFactory.createArtifact(RUN_DEPENDENCY_GROUPID, RUN_DEPENDENCY_ID, RUN_DEPENDENCY_VERSION, RUN_DEPENDENCY_CLASSIFIER,RUN_DEPENDENCY_TYPE, RUN_DEPENDENCY_EXTENSION);
        final Artifact artifact3 = DataModelFactory.createArtifact(TEST_DEPENDENCY_GROUPID, TEST_DEPENDENCY_ID, TEST_DEPENDENCY_VERSION, TEST_DEPENDENCY_CLASSIFIER, TEST_DEPENDENCY_TYPE, TEST_DEPENDENCY_EXTENSION);
        final Artifact artifact4 = DataModelFactory.createArtifact(PRO_DEPENDENCY_GROUPID, PRO_DEPENDENCY_ID, PRO_DEPENDENCY_VERSION, PRO_DEPENDENCY_CLASSIFIER, PRO_DEPENDENCY_TYPE, PRO_DEPENDENCY_EXTENSION);

        final Artifact thirdparty1 = DataModelFactory.createArtifact(COMP_THIRDPARTY_GROUPID, COMP_THIRDPARTY_ID, COMP_THIRDPARTY_VERSION, COMP_THIRDPARTY_CLASSIFIER, COMP_THIRDPARTY_TYPE, COMP_THIRDPARTY_EXTENSION);
        final Artifact thirdparty2 = DataModelFactory.createArtifact(RUN_THIRDPARTY_GROUPID, RUN_THIRDPARTY_ID, RUN_THIRDPARTY_VERSION, RUN_THIRDPARTY_CLASSIFIER,RUN_THIRDPARTY_TYPE, RUN_THIRDPARTY_EXTENSION);
        final Artifact thirdparty3 = DataModelFactory.createArtifact(TEST_THIRDPARTY_GROUPID, TEST_THIRDPARTY_ID, TEST_THIRDPARTY_VERSION, TEST_THIRDPARTY_CLASSIFIER, TEST_THIRDPARTY_TYPE, TEST_THIRDPARTY_EXTENSION);
        final Artifact thirdparty4 = DataModelFactory.createArtifact(PRO_THIRDPARTY_GROUPID, PRO_THIRDPARTY_ID, PRO_THIRDPARTY_VERSION, PRO_THIRDPARTY_CLASSIFIER, PRO_THIRDPARTY_TYPE, PRO_THIRDPARTY_EXTENSION);

        final Module module = DataModelFactory.createModule(MODULE_NAME, MODULE_VERSION);
        final Module submodule = DataModelFactory.createModule(SUB_MODULE_NAME, SUB_MODULE_VERSION);
        final Module subsubmodule = DataModelFactory.createModule(SUB_SUB_MODULE_NAME, SUB_SUB_MODULE_VERSION);


        subsubmodule.addDependency(DataModelFactory.createDependency(artifact1, Scope.COMPILE));
        subsubmodule.addDependency(DataModelFactory.createDependency(thirdparty1, Scope.COMPILE));
        submodule.addDependency(DataModelFactory.createDependency(artifact2, Scope.RUNTIME));
        submodule.addDependency(DataModelFactory.createDependency(thirdparty2, Scope.RUNTIME));
        module.addDependency(DataModelFactory.createDependency(artifact3, Scope.TEST));
        module.addDependency(DataModelFactory.createDependency(thirdparty3, Scope.TEST));
        module.addDependency(DataModelFactory.createDependency(artifact4, Scope.PROVIDED));
        module.addDependency(DataModelFactory.createDependency(thirdparty4, Scope.PROVIDED));
        submodule.addSubmodule(subsubmodule);
        module.addSubmodule(submodule);

        modules.add(module);
        artifacts.add(artifact1);
        artifacts.add(artifact2);
        artifacts.add(artifact3);
        artifacts.add(thirdparty1);
        artifacts.add(thirdparty2);
        artifacts.add(thirdparty3);
        artifacts.add(thirdparty4);
    }
    @Override
    public List<License> getLicenses() {
        return licenses;
    }

    @Override
    public List<Module> getModules() {
        return modules;
    }

    @Override
    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    @Override
    public List<String> getArtifactsToNotUse() {
        return new ArrayList<String>();
    }

    @Override
    public List<String> getModulesToPromote() {
        return new ArrayList<String>();
    }
}
