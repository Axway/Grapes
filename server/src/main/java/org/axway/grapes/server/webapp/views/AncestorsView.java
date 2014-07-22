package org.axway.grapes.server.webapp.views;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.core.options.Decorator;
import org.axway.grapes.server.db.DataUtils;

import java.util.List;

/**
 * Ancestors View
 *
 * <p>Handles ancestors list for the web-app display. It is able to generate tables that contains custom
 * dependencies information.</p>
 *
 * @author jdcoffre
 */
public class AncestorsView extends DependencyListView {


    public AncestorsView(final String title, final List<License> licenses, final Decorator decorator) {
        super(title, licenses, decorator);
    }


    public void addAncestor(final Module ancestor, final Artifact artifactId) {
        for(Dependency dependency: DataUtils.getAllDependencies(ancestor)){
            if(dependency.getTarget().equals(artifactId)){
                dependency.setSourceName(ancestor.getName());
                dependency.setSourceVersion(ancestor.getVersion());
                addDependency(dependency);
            }
        }
    }
}
