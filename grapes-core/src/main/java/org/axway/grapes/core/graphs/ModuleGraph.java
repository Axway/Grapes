package org.axway.grapes.core.graphs;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;
;

public class ModuleGraph extends AbstractGraph {

	@Override
	public String getId(final Module module) {
		return module.getName();
	}

	@Override
	public String getId(final Artifact artifact) {
		return artifact.getArtifactId();
	}



}
