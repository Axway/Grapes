package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

import java.util.HashMap;
import java.util.Map;

public class ExtensionFilter implements Filter {

    private String extension;

    /**
     * The parameter must never be null
     *
     * @param extension
     */
    public ExtensionFilter(final String extension) {
        this.extension = extension;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbArtifact){
            return extension.equals( ((DbArtifact)datamodelObj).getExtension());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(DbArtifact.EXTENSION_DB_FIELD, extension);
        return fields;
    }
}
