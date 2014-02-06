package org.axway.grapes.tests.acceptance.materials.cases;


import java.util.ArrayList;
import java.util.List;

public class TC03_SimpleAncestorCase implements TestCase {

    public static final String MODULE_NAME 			= "tc03Module";
    public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
    public static final String ARTIFACT_GROUPID		= TEST_GROUPID + ".test.tc03Module";
    public static final String ARTIFACT_ID	 		= "singleArtifact";
    public static final String ARTIFACT_EXTENSION   = "jar";
    public static final String ARTIFACT_CLASSIFIER	= "";
    public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
    public static final String ARTIFACT_TYPE 	    = "jar";

    public static final String ANCESTOR_MODULE_NAME 		= "tc03Ancestor";
    public static final String ANCESTOR_MODULE_VERSION 		= "1.0.0-SNAPSHOT";

    private final List<Artifact> artifacts = new ArrayList<Artifact>();
    private final List<Module> modules = new ArrayList<Module>();
    private final List<License> licenses = new ArrayList<License>();

    public TC03_SimpleAncestorCase() {
        final Artifact artifact = DataModelFactory.createArtifact(ARTIFACT_GROUPID, ARTIFACT_ID, ARTIFACT_VERSION, ARTIFACT_CLASSIFIER, ARTIFACT_TYPE, ARTIFACT_EXTENSION);

        final Module module = DataModelFactory.createModule(MODULE_NAME, MODULE_VERSION);
        module.addArtifact(artifact);

        Module ancestor = DataModelFactory.createModule(ANCESTOR_MODULE_NAME, ANCESTOR_MODULE_VERSION);
        ancestor.addDependency(DataModelFactory.createDependency(artifact, Scope.COMPILE));

        modules.add(module);
        modules.add(ancestor);
        artifacts.add(artifact);
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

    public static Module getAncestor() {
        final Module module = DataModelFactory.createModule(ANCESTOR_MODULE_NAME, ANCESTOR_MODULE_VERSION);
        final Artifact dependency = DataModelFactory.createArtifact(ARTIFACT_GROUPID, ARTIFACT_ID, ARTIFACT_VERSION, ARTIFACT_CLASSIFIER, ARTIFACT_TYPE, ARTIFACT_EXTENSION);
        module.addDependency(DataModelFactory.createDependency(dependency, Scope.COMPILE));

        return module;
    }
}
