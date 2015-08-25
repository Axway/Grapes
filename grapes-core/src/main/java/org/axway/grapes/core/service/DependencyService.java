package org.axway.grapes.core.service;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.reports.DepedencyReport2;
import org.axway.grapes.core.reports.DependencyReport;
import org.axway.grapes.model.datamodel.Dependency;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */

public interface DependencyService {
   List<Dependency> getModuleDependencies(String moduleId, FiltersHolder filters);

   DepedencyReport2 getDependencyReport(String moduleId, FiltersHolder filters);
}
