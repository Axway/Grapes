package org.axway.grapes.jenkins.reports;

import hudson.model.AbstractProject;
import hudson.model.Action;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.ModuleUtils;
import org.axway.grapes.jenkins.GrapesPlugin;
import org.axway.grapes.utils.client.GrapesClient;
import org.axway.grapes.utils.client.GrapesCommunicationException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Build action to access Grapes report for configured jobs
 *
 * @author jdcoffre
 *
 */
public class GrapesBuildAction implements Action {

    private final static String REPORT_ACTION_ICON = "img/report-icon.png";

    // The action is attached to a build of this project
    private AbstractProject<?, ?> project;

    // The module file that has been sent during the build is this one
    private Module module;

    private Map<Dependency, String> dependencies;
    private List<Dependency> thirdParty;
    private List<Dependency> ancestors;

    private boolean initOk = false;

    /**
     * Initiate the report
     *
     * @param module Module
     * @param grapesClient GrapesClient
     */
    public GrapesBuildAction(final Module module, final GrapesClient grapesClient) {
       if(grapesClient == null ||
                module == null ){
            return;
        }

        // Init the report with Grapes server information
        try{
            final List<String> corporateFilters = grapesClient.getCorporateFilters();
            dependencies = new HashMap<Dependency, String>();

            final List<Dependency> dependencies = ModuleUtils.getCorporateDependencies(module, corporateFilters);
            for(Dependency dependency: dependencies){
                final String lastVersion = getLastVersion(grapesClient, dependency);
                this.dependencies.put(dependency, lastVersion);
            }

            thirdParty = ModuleUtils.getThirdPartyLibraries(module, corporateFilters);
            ancestors = grapesClient.getModuleAncestors(module.getName(), module.getVersion()).getDependencies();

            initOk = true;

        } catch (Exception e){
            GrapesPlugin.getLogger().log(Level.WARNING, "Failed to generate build dependency report for " + module.getName(), e);
        }

    }

    public String getIconFileName() {
        return GrapesPlugin.getPluginResourcePath() + REPORT_ACTION_ICON;
    }

    public String getDisplayName() {
        return "Grapes Report";
    }

    public String getUrlName() {
        return "grapes";
    }

    /**
     * Returns the last version of a dependency
     *
     *
     * @param grapesClient
     * @param dependency Dependency
     * @return String
     */
    public String getLastVersion(final GrapesClient grapesClient, final Dependency dependency){
        final Logger logger = LogManager.getLogManager().getLogger("hudson.WebAppMain");
        try{
            final Artifact target = dependency.getTarget();
            return grapesClient.getArtifactLastVersion(target.getGavc());
        } catch (GrapesCommunicationException e) {
            logger.info("Failed to get last version of : " + dependency.getTarget().getGavc());
        }
         return "";
    }

    /**
     * Returns the license of a dependency
     *
     * @param dependency Dependency
     * @return String
     */
    public String getLicenses(final Dependency dependency){
        final Artifact target = dependency.getTarget();
        final List<String> licensesList = target.getLicenses();

        final StringBuilder sb = new StringBuilder();

        if(licensesList != null){
            Collections.sort(licensesList);
            for(String license : licensesList){
                sb.append(license);
                sb.append( " " );
            }
        }

        return sb.toString();
    }


    public Map<Dependency, String> getCorporateDependencies(){
        return dependencies;
    }

    public List<Dependency> getAncestors(){
        return ancestors;
    }

    public List<Dependency> getThirdPartyLibraries(){
        return thirdParty;
    }

    public Module getModule() {
        return module;
    }

    public boolean isInitOk() {
        return initOk;
    }

}