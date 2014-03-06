package org.axway.grapes.maven.converter;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
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

    private final Log log;

    private final LicenseResolver licenseResolver;
    private final ArtifactResolver artifactResolver;

    private Module rootModule;
    private final List<Module> subModules = new ArrayList<Module>();
    private Hashtable<String, String> parentDictionary = new Hashtable<String, String>();
    private Hashtable<String, Module> modulesDictionary= new Hashtable<String, Module>();

    public ModuleBuilder(final RepositorySystem repositorySystem, final ArtifactRepository localRepository, final Log log) {
        this.licenseResolver = new LicenseResolver(repositorySystem, localRepository, log);
        this.artifactResolver = new ArtifactResolver(repositorySystem, localRepository, log);
        this.log = log;
    }

    /**
     * Fill module information with maven project information
     *
     * @param project MavenProject
     */
    public void addModule(final MavenProject project) throws MojoExecutionException {
        final Module module = getModule(project);

        // If does not exist then root project
        if(rootModule == null){
            rootModule = module;
        }
        // If exist then sub project
        else{
            module.setSubmodule(true);
            subModules.add(module);
        }

        // To easily build the module tree
        modulesDictionary.put(project.getName(), module);
    }

    public Module build(){
        // Build module tree
        for(String subModule: parentDictionary.keySet()){
            // Cannot be null otherwise the POM file does no compile
            final String parentName = parentDictionary.get(subModule);
            final Module parent = modulesDictionary.get(parentName);
            parent.addSubmodule(modulesDictionary.get(subModule));
        }

        return rootModule;
    }

    /**
     * Turn a maven project (Maven data model) into a module (Grapes data model)
     *
     * @param project MavenProject
     * @return Module
     */
    private Module getModule(final MavenProject project) throws MojoExecutionException {

        final Module module = GrapesTranslator.getGrapesModule(project);
        final List<License> licenses = licenseResolver.resolve(project);

        /* Manage Artifacts */
        final Artifact mainArtifact = GrapesTranslator.getGrapesArtifact(project.getArtifact());
        addLicenses(mainArtifact, licenses);
        module.addArtifact(mainArtifact);

        // Trick to add project pom file as a module artifact
        final Artifact pomArtifact = GrapesTranslator.getGrapesArtifact(project.getArtifact());
        addLicenses(pomArtifact, licenses);
        pomArtifact.setType("pom");
        pomArtifact.setExtension("xml");
        module.addArtifact(pomArtifact);
        // End of trick

        for(int i = 0 ; i < project.getAttachedArtifacts().size() ; i++){
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
        for(String subModuleName: project.getModules()){
            parentDictionary.put(subModuleName, project.getName());
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

}
