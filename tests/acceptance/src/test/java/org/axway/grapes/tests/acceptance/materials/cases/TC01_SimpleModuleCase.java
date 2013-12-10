package org.axway.grapes.tests.acceptance.materials.cases;


import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;

import java.util.ArrayList;
import java.util.List;

public class TC01_SimpleModuleCase implements TestCase {

    public static final String MODULE_NAME 			= "tc01Module";
    public static final String MODULE_VERSION 		= "1.0.0-SNAPSHOT";
    public static final String ARTIFACT_GROUPID		= TEST_GROUPID + ".test.tc01Module";
    public static final String ARTIFACT_ID	 		= "singleArtifact";
    public static final String ARTIFACT_EXTENSION   = "pom";
    public static final String ARTIFACT_CLASSIFIER	= "linux";
    public static final String ARTIFACT_VERSION 	= "1.0.0-SNAPSHOT";
    public static final String ARTIFACT_TYPE 	    = "";

    private final List<Artifact> artifacts = new ArrayList<Artifact>();
    private final List<Module> modules = new ArrayList<Module>();
    private final List<License> licenses = new ArrayList<License>();

    public TC01_SimpleModuleCase() {
        final Artifact artifact = DataModelFactory.createArtifact(ARTIFACT_GROUPID, ARTIFACT_ID, ARTIFACT_VERSION, ARTIFACT_CLASSIFIER, ARTIFACT_TYPE, ARTIFACT_EXTENSION);

        final Module module = DataModelFactory.createModule(MODULE_NAME, MODULE_VERSION);
        module.addArtifact(artifact);

        modules.add(module);
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
}
