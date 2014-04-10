package org.axway.grapes.maven.materials.stubs;


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.axway.grapes.commons.datamodel.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiModuleProjectStub extends AbstractProjectStub {

    @Override
    public String getProjectPath() {
        return "multi-module-project";
    }

    @Override
    public List<Artifact> getAttachedArtifacts() {
        final Artifact attachedArtifact = new DefaultArtifact(
                getGroupId(),
                getArtifactId(),
                getVersion(),
                Scope.COMPILE.toString(),
                "zip",
                "materials",
                new DefaultArtifactHandler()
        );
        return Collections.singletonList(attachedArtifact);
    }

    @Override
    public List<String> getModules() {
        final List<String> modules = new ArrayList<String>();
        modules.add("subModule1");
        modules.add("subModule2");

        return modules;
    }
}
