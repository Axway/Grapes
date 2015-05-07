package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;

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
        if(datamodelObj instanceof Module){
            return version.equals( ((Module)datamodelObj).getVersion());
        }
        if(datamodelObj instanceof Artifact){
            return version.equals( ((Artifact)datamodelObj).getVersion());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("version", version);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("version", version);
        return fields;
    }
}
