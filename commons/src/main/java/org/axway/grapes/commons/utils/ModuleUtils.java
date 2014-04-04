package org.axway.grapes.commons.utils;

import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Module Utils
 *
 * <p>Provide a set of utility method around modules</p>
 *
 * @author jdcoffre
 */
public class ModuleUtils {


    /**
     * Returns all the dependencies of a module
     *
     * @param module Module
     * @return List<Dependency>
     */
    public static List<Dependency> getAllDependencies(final Module module) {
        final Set<Dependency> dependencies = new HashSet<Dependency>();
        dependencies.addAll(module.getDependencies());

        for(Module subModule: module.getSubmodules()){
            dependencies.addAll(getAllDependencies(subModule));
        }

        return new ArrayList<Dependency>(dependencies);
    }


    /**
     * Returns the corporate dependencies of a module
     *
     * @param module Module
     * @param corporateFilters List<String>
     * @return List<Dependency>
     */
    public static List<Dependency> getCorporateDependencies(final Module module, final List<String> corporateFilters) {
        final List<Dependency> corporateDependencies = new ArrayList<Dependency>();
        final Pattern corporatePattern = generateCorporatePattern(corporateFilters);

        for(Dependency dependency: getAllDependencies(module)){
            if(dependency.getTarget().getGavc().matches(corporatePattern.pattern())){
                corporateDependencies.add(dependency);
            }
        }

        return corporateDependencies;
    }


    /**
     * Returns the third party libraries of a module
     *
     * @param module Module
     * @param corporateFilters List<String>
     * @return List<Dependency>
     */
    public static List<Dependency> getThirdPartyLibraries(final Module module, final List<String> corporateFilters) {
        final List<Dependency> thirdParty = new ArrayList<Dependency>();
        final Pattern corporatePattern = generateCorporatePattern(corporateFilters);

        for(Dependency dependency: getAllDependencies(module)){
            if(!dependency.getTarget().getGavc().matches(corporatePattern.pattern())){
                thirdParty.add(dependency);
            }
        }

        return thirdParty;
    }

    private static Pattern generateCorporatePattern(final List<String> corporateFilters) {
        final StringBuilder sb = new StringBuilder();

        if(!corporateFilters.isEmpty()){
            sb.append("^(");
            final Iterator<String> filters = corporateFilters.iterator();

            while(filters.hasNext()){
                sb.append(filters.next());
                if(filters.hasNext()){
                    sb.append("|");
                }
            }
            sb.append(")");
        }

        sb.append("(.*)");

        return Pattern.compile(sb.toString());
    }
}
