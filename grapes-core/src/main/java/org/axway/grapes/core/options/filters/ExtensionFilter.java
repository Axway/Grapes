package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;

import java.util.Collections;
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
        if(datamodelObj instanceof Artifact){
            return extension.equals( ((Artifact)datamodelObj).getExtension());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("extension", extension);
        return fields;
    }
}
