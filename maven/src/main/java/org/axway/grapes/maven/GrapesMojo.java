package org.axway.grapes.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.converter.ModuleBuilder;
import org.axway.grapes.utils.client.GrapesClient;

import java.io.File;

/**
 * Goal which gathers and send dependencies information to Grapes.
 *
 * @goal notify
 * @phase install
 */
public class GrapesMojo  extends AbstractMojo{

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
     * If this parameter is set at true, a notification will be serialized in target/grapes folder
     * @parameter property="grapes.serialize"
     */
    private boolean serialize = false;

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

    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Collecting dependency information");
            final ModuleBuilder moduleBuilder = new ModuleBuilder(repositorySystem, localRepository, project, getLog());
            final Module module = moduleBuilder.build(project);
            final String serializedModule = JsonUtils.serialize(module);
            getLog().debug("Object to send: " + serializedModule);

            if(serialize){
                final File grapesFolder = new File(project.getBuild().getDirectory() + "/grapes");
                Utils.serialize(grapesFolder, serializedModule, "module.json");
                getLog().info("Serializing the notification in " + grapesFolder.getPath());
            }

            getLog().info("Connection to Grapes");
            getLog().info("Host: " + host);
            getLog().info("Port: " + port);
            getLog().info("User: " + user);
            final GrapesClient client = new GrapesClient(host, port);

            if(!client.isServerAvailable()){
                throw new MojoExecutionException("Grapes is unreachable");
            }

            client.postModule(module, user, password);
        } catch (Exception e) {

            if(failOnError){
                throw new MojoExecutionException("An error occurred during Grapes server Notification." , e);
            }
            else{
                getLog().debug("An error occurred during Grapes server Notification.", e);
                getLog().info("Failed to send information to Grapes");
            }

            return;
        }

        getLog().info("Information successfully sent");
    }
}
