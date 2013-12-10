package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.webapp.views.serialization.DependencyListSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonSerialize(using=DependencyListSerializer.class)
public class DependencyListView extends View{

    private final FiltersHolder filters;
    private final String title;
    private Boolean showTarget = true;

    private final List<Dependency> dependencies = new ArrayList<Dependency>();

    public DependencyListView(final String title, final FiltersHolder filters) {
		super("DependencyListView.ftl");
        this.title = title;
        this.filters = filters;
	}

    public String getTitle() {
        return title;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public Boolean getShowTarget() {
        return showTarget;
    }

    public FiltersHolder getFilters() {
        return filters;
    }

    public void setShowTarget(final Boolean showTarget) {
        this.showTarget = showTarget;
    }

    public void addDependency(final Dependency dependency) {
        if(!dependencies.contains(dependency)){
            dependencies.add(dependency);
        }
    }

    public void addAll(final List<Dependency> dependencies) {
        for(Dependency dependency: dependencies){
            addDependency(dependency);
        }
    }

    public List<Artifact> getDependencyTarget(){
        final List<Artifact> targets = new ArrayList<Artifact>();
        final List<String> treatedGavcs = new ArrayList<String>();

        for(Dependency dependency: dependencies){
            if(!treatedGavcs.contains(dependency.getTarget().getGavc())){
                targets.add(dependency.getTarget());
                treatedGavcs.add(dependency.getTarget().getGavc());
            }
        }

        DataUtils.sort(targets);
        return  targets;
    }

    public List<String> getScopes(final String targetGavc){
        final List<String> scopes = new ArrayList<String>();

        for(Dependency dependency: dependencies){
            if(dependency.getTarget().getGavc().equals(targetGavc)
                    && !scopes.contains(dependency.getScope().toString())){
                scopes.add(dependency.getScope().toString());
            }
        }

        Collections.sort(scopes);
        return  scopes;
    }
}
