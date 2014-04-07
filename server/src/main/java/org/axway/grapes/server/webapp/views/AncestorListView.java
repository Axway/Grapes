package org.axway.grapes.server.webapp.views;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;

/**
 * Created by jdcoffre on 07/04/14.
 */
public class AncestorListView extends DependencyListView {


    public AncestorListView(final String title, final FiltersHolder filters) {
        super(title, filters);
    }


    public void addAncestor(final DbModule ancestor, final Artifact target){
        for(DbDependency dbDependency: DataUtils.getAllDbDependencies(ancestor)){
            if(dbDependency.getTarget().equals(target.getGavc())){
                final Dependency dependency = DataModelFactory.createDependency(target, dbDependency.getScope());
                dependency.setSourceName(ancestor.getName());
                dependency.setSourceVersion(ancestor.getVersion());
                addDependency(dependency);
            }
        }
    }
}
