package org.axway.grapes.tests.acceptance.materials.cases;


import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;

import java.util.List;

public interface TestCase {

    public static final String TEST_GROUPID = "com.company";

    public List<License> getLicenses();

    public List<Module> getModules();

    public List<Artifact> getArtifacts();

    public List<String> getArtifactsToNotUse();

    public List<String> getModulesToPromote();

}
