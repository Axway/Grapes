package org.axway.grapes.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.utils.client.GrapesClient;

/**
 * Goal which send the dependency information to Grapes.
 *
 * @goal notify
 * 
 * @phase install
 */
public class GrapesMojo  extends AbstractMojo{

    /**
     * Grapes host
     * @parameter property="grapes.host"
     * @required
     */
    private String host;

    /**
     * Grapes port
     * @parameter property="grapes.port"
     */
    private String port;

    /**
     * Grapes user
     * @parameter property="grapes.user"
     */
    private String user;

    /**
     * Grapes password
     * @parameter property="grapes.password"
     */
    private String password;

    /**
     * If true, an exception will stop the maven execution on error
     * If false, the error will be logged the maven life cycle will continue.
     *
     * @parameter property="failOnError"
     */
    private boolean failOnError = true;

    /**
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject project;


    public void execute() throws MojoExecutionException
    {
        try {
            getLog().info("Collecting dependency information");
            final DataMapper dataMapper = new DataMapper(getLog());
            final Module module = dataMapper.getModule(project);
            getLog().debug("Object to send: " + JsonUtils.serialize(module));

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
