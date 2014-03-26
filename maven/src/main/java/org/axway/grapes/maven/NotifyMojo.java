package org.axway.grapes.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.utils.FileUtils;
import org.axway.grapes.utils.client.GrapesClient;

import java.io.File;
import java.util.List;

/**
 * Goal which gathers and send dependencies information to Grapes.
 *
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
     * The projects in the reactor.
     *
     * @parameter property="reactorProjects"
     * @readonly
     */
    private List<MavenProject> reactorProjects;

    public void execute() throws MojoExecutionException {
        // Execute only one time
        if(project.equals(reactorProjects.get(0))){
            try {
                final File workingFolder = GrapesMavenPlugin.getGrapesPluginWorkingFolder(reactorProjects.get(0));
                final Module rootModule = GrapesMavenPlugin.getModule(workingFolder, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);
                getLog().info("Sending " + rootModule.getName() + "...");

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
    }
}