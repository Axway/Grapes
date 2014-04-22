package org.axway.grapes.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.FileUtils;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.report.ModuleAggregator;
import org.axway.grapes.maven.report.ModuleBuilder;
import org.axway.grapes.maven.resolver.ArtifactResolver;
import org.axway.grapes.maven.resolver.LicenseResolver;

import java.io.File;
import java.util.List;

/**
 * Goal which gathers and send dependencies information to Grapes.
 *
 * @goal generate
 * @phase package
 */
public class GenerateMojo extends AbstractMojo{

    /**
     * Indicates whether the build will continue even if there are clean errors.
     * If true, an exception will stop the maven execution on error
     * If false, the error will be logged the maven life cycle will continue.
     * @parameter property="grapes.failOnError"
     */
    private boolean failOnError = true;

    /**
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     */
    private RepositorySystem repositorySystem;

    /**
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * The projects in the reactor.
     *
     * @parameter property="reactorProjects"
     * @readonly
     */
    private List<MavenProject> reactorProjects;

    private int i = 1;

    public void execute() throws MojoExecutionException {
        try {
            final ModuleAggregator aggregator = new ModuleAggregator(reactorProjects);
            final ModuleBuilder moduleBuilder = new ModuleBuilder();
            final ArtifactResolver artifactResolver = new ArtifactResolver(repositorySystem, localRepository, getLog());
            final LicenseResolver licenseResolver = new LicenseResolver(repositorySystem, localRepository, getLog());

            getLog().info("Collecting dependency information of " + project.getName());
            final Module module = moduleBuilder.getModule(project, licenseResolver, artifactResolver);

            // Serialize the collected information
            final String serializedModule = JsonUtils.serialize(module);
            getLog().debug("Json module : " + serializedModule);

            // Write file
            final File grapesFolder = GrapesMavenPlugin.getGrapesPluginWorkingFolder(reactorProjects.get(0));
            getLog().info("Serializing the notification in " + grapesFolder.getPath());

            if(project.equals(reactorProjects.get(0))){
                FileUtils.serialize(grapesFolder, serializedModule, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);
            }
            else{
                final String subModuleName = project.getBasedir().getName();
                FileUtils.serialize(grapesFolder, serializedModule, GrapesMavenPlugin.getSubModuleFileName(subModuleName));
            }

            getLog().info("Report consolidation...");
            aggregator.aggregate();

        } catch (Exception e) {

            if(failOnError){
                throw new MojoExecutionException("An error occurred during Grapes reporting." , e);
            }
            else{
                getLog().debug("An error occurred during Grapes reporting.", e);
                getLog().error("Failed to build Grapes report.");
            }
        }
    }
}