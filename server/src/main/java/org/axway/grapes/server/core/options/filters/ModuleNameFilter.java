package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbModule;

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
        if(datamodelObj instanceof DbModule){
            return moduleName.equals( ((DbModule)datamodelObj).getName());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(DbModule.NAME_DB_FIELD, moduleName);
        return fields;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        return new HashMap<String, Object>();
    }
}
