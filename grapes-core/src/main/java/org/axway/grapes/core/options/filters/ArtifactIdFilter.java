package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ArtifactIdFilter implements Filter {

    private String artifactId;

    /**
     * The parameter must never be null
     *
     * @param artifactId
     */
    public ArtifactIdFilter(final String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof Artifact){
            return artifactId.equals( ((Artifact)datamodelObj).getArtifactId());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("artifactId", artifactId);
        return queryParams;
    }
}
