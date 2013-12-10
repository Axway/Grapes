package org.axway.grapes.server.core.graphs;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraph {
	private final List<String> treatedElements = new ArrayList<String>();
	private List<GraphElement> elements = new ArrayList<GraphElement>();
	private List<GraphDependency> dependencies = new ArrayList<GraphDependency>();
	
	public List<GraphElement> getElements() {
		return elements;
	}
	public void setElements(final List<GraphElement> elements) {
		this.elements = elements;
	}
	public List<GraphDependency> getDependencies() {
		return dependencies;
	}
	public void setDependencies(final List<GraphDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void addElement(final String id, final String version, final boolean root){
		final GraphElement element = new GraphElement();
		element.setValue(id);
		element.setVersion(version);
		element.setRoot(root);
		
		if(!elements.contains(element)){
			elements.add(element);
		}
		
		treated(id);
	}

	public void addDependency(final String sourceId, final String targetId, final Scope scope){
        if(sourceId.equals(targetId)){
            return;
        }

		final GraphDependency jsonDep = new GraphDependency();
		jsonDep.setSource(sourceId);
		jsonDep.setTarget(targetId);
		jsonDep.setType(scope.toString());

		dependencies.add(jsonDep);
	}
	
	public boolean isTreated(final String elementId){
		return treatedElements.contains(elementId);
	}
	
	public void treated(final String id){
		treatedElements.add(id);
	}

	public abstract String getId(final DbModule module);
	
	public abstract String getId(final DbArtifact artifact);
	
	public abstract String getId(final Artifact artifact);
	
}
