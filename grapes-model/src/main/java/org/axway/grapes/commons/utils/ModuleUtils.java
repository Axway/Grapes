package org.axway.grapes.commons.utils;

import org.axway.grapes.commons.datamodel.Artifact;
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
public final class ModuleUtils {


    private ModuleUtils(){
        // hide utility class constructor
    }

    /**
     * Returns all the Artifacts of the module
     *
     * @param module Module
     * @return List<Artifact>
     */
    public static List<Artifact> getAllArtifacts(final Module module){
        final List<Artifact> artifacts = new ArrayList<Artifact>();

        for(Module subModule: module.getSubmodules()){
            artifacts.addAll(getAllArtifacts(subModule));
        }

        artifacts.addAll(module.getArtifacts());

        return artifacts;
    }

    /**
     * Returns all the dependencies of a module
     *
     * @param module Module
     * @return List<Dependency>
     */
    public static List<Dependency> getAllDependencies(final Module module) {
        final Set<Dependency> dependencies = new HashSet<Dependency>();
        final List<String> producedArtifacts = new ArrayList<String>();
        for(Artifact artifact: getAllArtifacts(module)){
            producedArtifacts.add(artifact.getGavc());
        }

        dependencies.addAll(getAllDependencies(module, producedArtifacts));

        return new ArrayList<Dependency>(dependencies);
    }

    /**
     * Returns all the dependencies taken into account the artifact of the module that will be removed from the dependencies
     *
     * @param module Module
     * @param producedArtifacts List<String>
     * @return Set<Dependency>
     */
    public static Set<Dependency> getAllDependencies(final Module module, final List<String> producedArtifacts) {
        final Set<Dependency> dependencies = new HashSet<Dependency>();

        for(Dependency dependency: module.getDependencies()){
            if(!producedArtifacts.contains(dependency.getTarget().getGavc())){
                dependencies.add(dependency);
            }
        }

        for(Module subModule: module.getSubmodules()){
            dependencies.addAll(getAllDependencies(subModule, producedArtifacts));
        }

        return dependencies;
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
