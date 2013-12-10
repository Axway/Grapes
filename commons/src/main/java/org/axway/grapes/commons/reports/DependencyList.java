package org.axway.grapes.commons.reports;

import org.axway.grapes.commons.datamodel.Dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * Dependency List
 * 
 * <p>Holds a list of dependencies. This class is used for Grapes server reports.</p>
 * 
 * @author jdcoffre
 */
public class DependencyList {
	
	private List<Dependency> dependencies = new ArrayList<Dependency>();
	
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(final List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void addDependency(final Dependency dependency){
		dependencies.add(dependency);
	}

}
