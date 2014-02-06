package org.axway.grapes.tests.acceptance.materials.cases;


import java.util.ArrayList;
import java.util.List;

public class TC04_ProductCase implements TestCase {

    public static final String MODULE1_NAME			        = "tc04Module1";
    public static final String MODULE1_VERSION              = "2.1.0-SNAPSHOT";

    public static final String MODULE2_NAME			        = "tc04Module2";
    public static final String MODULE2_VERSION              = "4.5.0-123";

    public static final String ARTIFACT21_GROUPID		    = TEST_GROUPID + ".test.producttc04.tc04module2";
    public static final String ARTIFACT21_ID	 		    = "artifact21";
    public static final String ARTIFACT21_EXTENSION         = "pom";
    public static final String ARTIFACT21_CLASSIFIER	    = "";
    public static final String ARTIFACT21_VERSION 	        = MODULE2_VERSION;
    public static final String ARTIFACT21_TYPE 	            = "pom";

    public static final String MODULE3_NAME			        = "tc04Module3";
    public static final String MODULE3_VERSION              = "1.0.0-1";

    public static final String ARTIFACT31_GROUPID		    = TEST_GROUPID + ".test.producttc04.tc04module3";
    public static final String ARTIFACT31_ID	 		    = "artifact31";
    public static final String ARTIFACT31_EXTENSION         = "jar";
    public static final String ARTIFACT31_CLASSIFIER	    = "";
    public static final String ARTIFACT31_VERSION 	        = MODULE3_VERSION;
    public static final String ARTIFACT31_TYPE 	            = "jar";

    public static final String SUB_MODULE3_NAME 			= "tc04Module3:sub";
    public static final String SUB_MODULE3_VERSION 		    = MODULE3_VERSION;

    public static final String ARTIFACT311_GROUPID		    = TEST_GROUPID + ".test.producttc04.tc04module3.sub";
    public static final String ARTIFACT311_ID	 		    = "artifact311";
    public static final String ARTIFACT311_EXTENSION         = "jar";
    public static final String ARTIFACT311_CLASSIFIER	    = "";
    public static final String ARTIFACT311_VERSION 	        = MODULE3_VERSION;
    public static final String ARTIFACT311_TYPE 	        = "jar";

    public static final String SLF4J_DEPENDENCY_GROUPID		= "org.slf4j";
    public static final String SLF4J_DEPENDENCY_ID	 		= "slf4j-simple";
    public static final String SLF4J_DEPENDENCY_EXTENSION   = "jar";
    public static final String SLF4J_DEPENDENCY_CLASSIFIER	= "";
    public static final String SLF4J_DEPENDENCY_VERSION 	= "1.7.5";
    public static final String SLF4J_DEPENDENCY_TYPE 	    = "jar";
    public static final Scope  SLF4J_DEPENDENCY_SCOPE 	    = Scope.PROVIDED;

    public static final String MIT_LICENSE_NAME             = "MIT";
    public static final String MIT_LICENSE_LONG_NAME        = "The MIT License";
    public static final String MIT_LICENSE_URL              = "http://www.opensource.org/licenses/mit-license.php";

    public static final String MODULE4_NAME			        = "tc04Module4";
    public static final String MODULE4_VERSION              = "0.9.0-5";
    public static final String LAST_MODULE4_VERSION         = "1.0.0-1";

    public static final String ARTIFACT41_GROUPID		    = TEST_GROUPID + ".test.producttc04.tc04module4";
    public static final String ARTIFACT41_ID	 		    = "artifact41";
    public static final String ARTIFACT41_EXTENSION         = "jar";
    public static final String ARTIFACT41_CLASSIFIER	    = "";
    public static final String ARTIFACT41_VERSION 	        = MODULE4_VERSION;
    public static final String ARTIFACT41_TYPE 	            = "jar";

    public static final String MONGO_DEPENDENCY_GROUPID		= "org.mongodb";
    public static final String MONGO_DEPENDENCY_ID	 		= "mongo-java-driver";
    public static final String MONGO_DEPENDENCY_EXTENSION   = "jar";
    public static final String MONGO_DEPENDENCY_CLASSIFIER	= "";
    public static final String MONGO_DEPENDENCY_VERSION 	= "2.11.0";
    public static final String MONGO_DEPENDENCY_TYPE 	    = "jar";
    public static final Scope  MONGO_DEPENDENCY_SCOPE 	    = Scope.COMPILE;

    public static final String GPL_LICENSE_NAME             = "GPL3.0";
    public static final String GPL_LICENSE_LONG_NAME        = "GNU General Public License version 3";
    public static final String GPL_LICENSE_URL              = "http://www.opensource.org/licenses/gpl-3.0.html";

    public static final String JUNIT_DEPENDENCY_GROUPID		= "junit";
    public static final String JUNIT_DEPENDENCY_ID	 		= "junit";
    public static final String JUNIT_DEPENDENCY_EXTENSION   = "jar";
    public static final String JUNIT_DEPENDENCY_CLASSIFIER	= "";
    public static final String JUNIT_DEPENDENCY_VERSION 	= "4.11";
    public static final String JUNIT_DEPENDENCY_TYPE 	    = "jar";
    public static final Scope  JUNIT_DEPENDENCY_SCOPE 	    = Scope.TEST;

    public static final String CPL_LICENSE_NAME             = "CPL-1.0";
    public static final String CPL_LICENSE_LONG_NAME        = "Common Public License";
    public static final String CPL_LICENSE_URL              = "http://www.opensource.org/licenses/cpl1.0.txt";

    private final List<Dependency> dependencies = new ArrayList<Dependency>();
    private final List<Artifact> artifacts = new ArrayList<Artifact>();
    private final List<Module> modules = new ArrayList<Module>();
    private final List<License> licenses = new ArrayList<License>();
    private final List<String> toPromote = new ArrayList<String>();
    private final List<String> doNotUse = new ArrayList<String>();

    public TC04_ProductCase() {
        final Module module1 = DataModelFactory.createModule(MODULE1_NAME, MODULE1_VERSION);

        final Module module2 = DataModelFactory.createModule(MODULE2_NAME, MODULE2_VERSION);
        final Artifact artifact21 = DataModelFactory.createArtifact(ARTIFACT21_GROUPID, ARTIFACT21_ID, ARTIFACT21_VERSION, ARTIFACT21_CLASSIFIER, ARTIFACT21_TYPE, ARTIFACT21_EXTENSION);
        module2.addArtifact(artifact21);
        module1.addDependency(DataModelFactory.createDependency(artifact21, Scope.COMPILE));

        final Module module3 = DataModelFactory.createModule(MODULE3_NAME, MODULE3_VERSION);
        final Artifact artifact31 = DataModelFactory.createArtifact(ARTIFACT31_GROUPID, ARTIFACT31_ID, ARTIFACT31_VERSION, ARTIFACT31_CLASSIFIER, ARTIFACT31_TYPE, ARTIFACT31_EXTENSION);
        final Module submodule3 = DataModelFactory.createModule(SUB_MODULE3_NAME, SUB_MODULE3_VERSION);
        final Artifact artifact311 = DataModelFactory.createArtifact(ARTIFACT311_GROUPID, ARTIFACT311_ID, ARTIFACT311_VERSION, ARTIFACT311_CLASSIFIER, ARTIFACT311_TYPE, ARTIFACT311_EXTENSION);
        module3.addArtifact(artifact31);
        module3.addSubmodule(submodule3);
        submodule3.addArtifact(artifact311);
        module1.addDependency(DataModelFactory.createDependency(artifact31, Scope.COMPILE));
        module1.addDependency(DataModelFactory.createDependency(artifact311, Scope.COMPILE));

        final Module module4 = DataModelFactory.createModule(MODULE4_NAME, MODULE4_VERSION);
        final Artifact artifact41 = DataModelFactory.createArtifact(ARTIFACT41_GROUPID, ARTIFACT41_ID, ARTIFACT41_VERSION, ARTIFACT41_CLASSIFIER, ARTIFACT41_TYPE, ARTIFACT41_EXTENSION);
        module4.addArtifact(artifact41);
        submodule3.addDependency(DataModelFactory.createDependency(artifact41, Scope.COMPILE));

        final Artifact junit = DataModelFactory.createArtifact(JUNIT_DEPENDENCY_GROUPID, JUNIT_DEPENDENCY_ID, JUNIT_DEPENDENCY_VERSION, JUNIT_DEPENDENCY_CLASSIFIER, JUNIT_DEPENDENCY_TYPE, JUNIT_DEPENDENCY_EXTENSION);
        final Dependency dep1 = DataModelFactory.createDependency(junit, JUNIT_DEPENDENCY_SCOPE);
        final License cpl = DataModelFactory.createLicense(CPL_LICENSE_NAME, CPL_LICENSE_LONG_NAME, null, null, CPL_LICENSE_URL);
        junit.addLicense(CPL_LICENSE_NAME);
        module1.addDependency(dep1);
        module2.addDependency(dep1);
        module3.addDependency(dep1);
        module4.addDependency(dep1);

        final Artifact slf4j = DataModelFactory.createArtifact(SLF4J_DEPENDENCY_GROUPID, SLF4J_DEPENDENCY_ID, SLF4J_DEPENDENCY_VERSION, SLF4J_DEPENDENCY_CLASSIFIER, SLF4J_DEPENDENCY_TYPE, SLF4J_DEPENDENCY_EXTENSION);
        final Dependency dep2 = DataModelFactory.createDependency(slf4j, SLF4J_DEPENDENCY_SCOPE);
        final License mit = DataModelFactory.createLicense(MIT_LICENSE_NAME, MIT_LICENSE_LONG_NAME, null, null, MIT_LICENSE_URL);
        slf4j.addLicense(MIT_LICENSE_NAME);
        submodule3.addDependency(dep2);

        final Artifact mongo = DataModelFactory.createArtifact(MONGO_DEPENDENCY_GROUPID, MONGO_DEPENDENCY_ID, MONGO_DEPENDENCY_VERSION, MONGO_DEPENDENCY_CLASSIFIER, MONGO_DEPENDENCY_TYPE, MONGO_DEPENDENCY_EXTENSION);
        final Dependency dep3 = DataModelFactory.createDependency(mongo, MONGO_DEPENDENCY_SCOPE);
        final License gpl = DataModelFactory.createLicense(GPL_LICENSE_NAME, GPL_LICENSE_LONG_NAME, null, null, GPL_LICENSE_URL);
        mongo.addLicense(GPL_LICENSE_NAME);
        module4.addDependency(dep3);

        final Module module4New = DataModelFactory.createModule(MODULE4_NAME, LAST_MODULE4_VERSION);
        final Artifact artifact41New = DataModelFactory.createArtifact(ARTIFACT41_GROUPID, ARTIFACT41_ID, LAST_MODULE4_VERSION, ARTIFACT41_CLASSIFIER, ARTIFACT41_TYPE, ARTIFACT41_EXTENSION);
        module4New.addArtifact(artifact41New);
        module4New.addDependency(dep3);

        modules.add(module1);
        modules.add(module2);
        modules.add(module3);
        modules.add(module4);
        modules.add(module4New);
        artifacts.add(artifact21);
        artifacts.add(artifact31);
        artifacts.add(artifact311);
        artifacts.add(junit);
        artifacts.add(slf4j);
        artifacts.add(mongo);
        dependencies.add(dep1);
        dependencies.add(dep2);
        dependencies.add(dep3);
        licenses.add(cpl);
        licenses.add(mit);
        licenses.add(gpl);

        doNotUse.add(mongo.getGavc());
        toPromote.add(module2.getName());
        toPromote.add(module3.getName());
        toPromote.add(module4.getName());
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
        return doNotUse;
    }

    @Override
    public List<String> getModulesToPromote() {
        return toPromote;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }
}
