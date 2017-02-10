package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.webapp.views.serialization.ModuleSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@JsonSerialize(using=ModuleSerializer.class)
public class ModuleView  extends View{

    private Module module;
    private  Map<Module, Integer> submodules;

    private String organization;

    public ModuleView() {
        super("ModuleView.ftl");
    }

    public void setModule(final Module module) {
        this.module = module;
        submodules = new HashMap<Module, Integer>();

        for (final Module submodule : module.getSubmodules()) {
            addSubmodules(submodule, 1);
        }

    }

    private void addSubmodules(final Module submodule, final Integer depth) {
        submodules.put(submodule, depth);
        for(final Module subsubmodule: submodule.getSubmodules()){
            addSubmodules(subsubmodule, depth + 1);
        }
    }

    public Module getModule() {
        return module;
    }

    public Set<Module> getSubmodules() {
        return submodules.keySet();
    }

    public Integer getDepth(final Module submodule) {
        return submodules.get(submodule);
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }
}