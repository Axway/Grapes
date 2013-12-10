package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.HashMap;
import java.util.Map;

public class VersionFilter implements Filter {

    private String version;

    /**
     * The parameter must never be null
     *
     * @param version
     */
    public VersionFilter(final String version) {
        this.version = version;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbModule){
            return version.equals( ((DbModule)datamodelObj).getVersion());
        }
        if(datamodelObj instanceof DbArtifact){
            return version.equals( ((DbArtifact)datamodelObj).getVersion());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(DbModule.VERSION_DB_FIELD, version);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(DbArtifact.VERSION_DB_FIELD, version);
        return fields;
    }
}
