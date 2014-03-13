package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

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
        if(datamodelObj instanceof DbArtifact){
            return artifactId.equals( ((DbArtifact)datamodelObj).getArtifactId());
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
        queryParams.put(DbArtifact.ARTIFACTID_DB_FIELD, artifactId);
        return queryParams;
    }
}
