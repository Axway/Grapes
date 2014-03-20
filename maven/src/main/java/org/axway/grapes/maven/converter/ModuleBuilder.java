package org.axway.grapes.maven.converter;

import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.maven.resolver.ArtifactResolver;
import org.axway.grapes.maven.resolver.LicenseResolver;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Data GrapesTranslator Implementation
 *
 * <p>Ensures the transformation from Maven data model to Grapes data model.</p>
 *
 * @author jdcoffre
 */
public class ModuleBuilder {


    private Module rootModule;

    private final List<Module> subModules = new ArrayList<Module>();

    private final Hashtable<String, List<String>> subModuleDictionary = new Hashtable<String, List<String>>();
    private final Hashtable<String, Module> modulesDictionary= new Hashtable<String, Module>();


    /**
     * Fill module information with maven project information
     *
     * @param project MavenProject
     */
    public void addModule(final MavenProject project, final LicenseResolver licenseResolver, final ArtifactResolver artifactResolver) throws MojoExecutionException {
        final Module module = getModule(project, licenseResolver, artifactResolver);

        // First module to build is always root one
        if(rootModule == null){
            rootModule = module;
        }
        // The others are sub-modules
        else{
            module.setSubmodule(true);
            subModules.add(module);
        }

        // To easily build the module tree
        modulesDictionary.put(project.getBasedir().getName(), module);
    }

    /**
     * Generate the complete module tree regarding the information that has been collected
     *
     * @return Module
     */
    public Module build(){
        return build(rootModule);
    }

    /**
     * build sub-module tree of a module regarding the information that has been collected
     *
     * @param module Module
     * @return Module
     */
    private Module build(final Module module){
        for(Module subModule: getSubModules(module.getName())){
            module.addSubmodule(build(subModule));
        }
        return module;
    }

    /**
     * Turn a maven project (Maven data model) into a module (Grapes data model)
     *
     * @param project MavenProject
     * @return Module
     */
    private Module getModule(final MavenProject project, final LicenseResolver licenseResolver, final ArtifactResolver artifactResolver) throws MojoExecutionException {

        final Module module = GrapesTranslator.getGrapesModule(project);
        final List<License> licenses = licenseResolver.resolve(project);

        // Trick to add project pom file as a module artifact
        final Artifact pomArtifact = GrapesTranslator.getGrapesArtifact(project.getArtifact());
        addLicenses(pomArtifact, licenses);
        pomArtifact.setType("pom");
        pomArtifact.setExtension("xml");
        module.addArtifact(pomArtifact);
        // End of trick

        /* Manage Artifacts */
        artifactResolver.resolveArtifact(project, project.getArtifact());
        final Artifact mainArtifact = GrapesTranslator.getGrapesArtifact(project.getArtifact());
        addLicenses(mainArtifact, licenses);
        module.addArtifact(mainArtifact);

        for(int i = 0 ; i < project.getAttachedArtifacts().size() ; i++){
            artifactResolver.resolveArtifact(project, project.getAttachedArtifacts().get(i));
            final Artifact attachedArtifact = GrapesTranslator.getGrapesArtifact(project.getAttachedArtifacts().get(i));
            // handle licenses
            addLicenses(attachedArtifact, licenses);
            module.addArtifact(attachedArtifact);
        }

        /* Manage Dependencies */
        for(int i = 0 ; i < project.getDependencies().size() ; i++){
            final Dependency dependency = GrapesTranslator.getGrapesDependency(
                    artifactResolver.resolveArtifact(project, project.getDependencies().get(i)),
                        project.getDependencies().get(i).getScope());

            // handle licenses
            for(License license: licenseResolver.resolve(
                    project,
                    dependency.getTarget().getGroupId(),
                    dependency.getTarget().getArtifactId(),
                    dependency.getTarget().getVersion())){
                dependency.getTarget().addLicense(license.getName());
            }

            module.addDependency(dependency);
        }

        /*Prepare sub-modules*/
        subModuleDictionary.put(module.getName(), new ArrayList<String>());
        for(String subModuleName: project.getModules()){
            subModuleDictionary.get(module.getName()).add(subModuleName);
        }

        return module;
    }


    /**
     * Fill the artifact with the licenses name of the license list
     * @param mainArtifact Artifact
     * @param licenses List<License>
     */
    private void addLicenses(final Artifact mainArtifact, final List<License> licenses) {
        for(License license: licenses){
            mainArtifact.addLicense(license.getName());
        }
    }

    /**
     * Return the subModules of a module
     *
     * @param moduleName String
     * @return List<Module>
     */
    private List<Module> getSubModules(final String moduleName) {
        final List<Module> subModules = new ArrayList<Module>();

        for(String subModule: subModuleDictionary.get(moduleName)){
            subModules.add(modulesDictionary.get(subModule));
        }

        return subModules;
    }

}
