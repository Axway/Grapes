package org.axway.grapes.maven.materials.stubs;


import org.apache.maven.artifact.Artifact;

import java.util.Collections;
import java.util.List;

public class SimpleProjectStub extends AbstractProjectStub {


    @Override
    public String getProjectPath() {
        return "simple-project";
    }

    @Override
    public List<Artifact> getAttachedArtifacts(){
        return Collections.emptyList();
    }

    @Override
    public List<String> getModules() {
        return Collections.emptyList();
    }

}
