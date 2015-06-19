package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Module;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModuleNameFilter implements Filter {

    private String moduleName;

    /**
     * The parameter must never be null
     *
     * @param moduleName
     */
    public ModuleNameFilter(final String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof Module){
            return moduleName.equals( ((Module)datamodelObj).getName());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("name", moduleName);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return Collections.emptyMap();
    }
}
