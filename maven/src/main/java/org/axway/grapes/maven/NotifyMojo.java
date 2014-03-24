package org.axway.grapes.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.converter.ModuleBuilder;
import org.axway.grapes.maven.resolver.ArtifactResolver;
import org.axway.grapes.maven.resolver.LicenseResolver;
import org.axway.grapes.maven.utils.FileUtils;
import org.axway.grapes.utils.client.GrapesClient;

import java.io.File;
import java.util.List;

/**
 * Goal which gathers and send dependencies information to Grapes.
 *
 * @instantiationStrategy singleton
 * @threadSafe true
 * @goal notify
 * @phase install
 */
public class NotifyMojo extends AbstractMojo{

    /**
     * Host of the targeted Grapes server
     * @parameter property="grapes.host"
     * @required
     */
    private String host;

    /**
     * Port of the targeted Grapes server
     * @parameter property="grapes.port"
     */
    private String port;

    /**
     * Grapes user to use during the notification
     * @parameter property="grapes.user"
     */
    private String user;

    /**
     * Password of the Grapes user
     * @parameter property="grapes.password"
     */
    private String password;

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
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    private List<MavenProject> reactorProjects;

    private int i = 1;

    public void execute() throws MojoExecutionException {
        try {
            final ModuleBuilder moduleBuilder = new ModuleBuilder();
            final ArtifactResolver artifactResolver = new ArtifactResolver(repositorySystem, localRepository, getLog());
            final LicenseResolver licenseResolver = new LicenseResolver(repositorySystem, localRepository, getLog());

            getLog().info("Collecting dependency information of " + project.getName());
            final Module module = moduleBuilder.getModule(project, licenseResolver, artifactResolver);
            final String serializedModule = JsonUtils.serialize(module);
            getLog().debug("Json module : " + serializedModule);

            final File grapesFolder = GrapesMavenPlugin.getGrapesPluginWorkingFolder(project);
            getLog().info("Serializing the notification in " + grapesFolder.getPath());
            FileUtils.serialize(grapesFolder, serializedModule, GrapesMavenPlugin.TMP_MODULE_JSON_FILE_NAME);

            if(isLastModule()) {
                getLog().info("Reports aggregation");
                final ModuleAggregator aggregator = new ModuleAggregator(reactorProjects);
                final Module rootModule = aggregator.aggregate();
                aggregator.cleanTmpFiles();

                getLog().info("Sending " + rootModule.getName() + "..");

                getLog().info("Connection to Grapes");
                getLog().info("Host: " + host);
                getLog().info("Port: " + port);
                getLog().info("User: " + user);
                final GrapesClient client = new GrapesClient(host, port);

                if(!client.isServerAvailable()){
                    throw new MojoExecutionException("Grapes is unreachable");
                }

                client.postModule(rootModule, user, password);

                getLog().info("Information successfully sent");
            }

        } catch (Exception e) {

            if(failOnError){
                throw new MojoExecutionException("An error occurred during Grapes server Notification." , e);
            }
            else{
                getLog().debug("An error occurred during Grapes server Notification.", e);
                getLog().info("Failed to send information to Grapes");
            }
        }
    }

    /**
     * Checks if the current project is the last project to build
     *
     * @return boolean
     */
    private boolean isLastModule(){
        final int projectSize = reactorProjects.size();
        final MavenProject lastProject = reactorProjects.get(projectSize - 1);

        return project.equals(lastProject);
    }
}