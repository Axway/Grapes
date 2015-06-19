package org.axway.grapes.core.service;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;

import java.util.List;
import java.util.Set;

/**
 * Created by jennifer on 5/11/15.
 */
public interface MiscService {
    List<String> getAllArtifactsGavcs(Module module);

    Set<Artifact> getAllArtifacts(Module module);
}
