package org.axway.grapes.jenkins;


import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.maven.AbstractMavenProject;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.jenkins.config.GrapesConfig;
import org.axway.grapes.utils.client.GrapesClient;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GrapesNotifier extends Notifier {



    // Name of current Grapes configuration
    private String configName;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public GrapesNotifier(final String configName) {
        this.configName = configName;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    /**
     * Performs grapes notification, at the end of the build.
     *
     * @param build
     * @param launcher
     * @param listener
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();

        try{
            // Retrieve the module from Json file
            final FilePath moduleFilePath = getModuleFilePath(build);

            // Init the build report action
            if(moduleFilePath.exists()){
                logger.println("[GRAPES] Grapes Module file detected.");
                final Module module = GrapesPlugin.getModule(new File(String.valueOf(moduleFilePath)));
                final GrapesConfig config = getConfig();


                logger.println("[GRAPES] Connection to Grapes");
                logger.println("[GRAPES] Host: " + config.getHost());
                logger.println("[GRAPES] Port: " + config.getPort());

                final GrapesClient client = new GrapesClient(config.getHost(), String.valueOf(config.getPort()));

                if(client.isServerAvailable()){
                    String user = null, password = null;

                    if(config.getPublisherCredentials() != null){
                        user = config.getPublisherCredentials().getUsername();
                        password = config.getPublisherCredentials().getPassword();
                    }

                    client.postModule(module, user, password);
                    logger.println("[GRAPES] Information successfully sent");
                }
                else{
                    logger.println("[GRAPES] Notification not sent.");
                    logger.println("[GRAPES] Grapes server is not reachable.");
                }
            }

            // Archive Grapes module file.
            final FilePath reportFolder = GrapesPlugin.getReportFolder(build);
            if(moduleFilePath.exists()){
                moduleFilePath.copyTo(reportFolder);
            }
            else{
                logger.println("[GRAPES] WARNING: Grapes module file does not exist.");
                logger.println("[GRAPES] WARNING: Make sure that you still need Grapes Jenkins for this job and if the configuration of the plugin is ok ");
            }

        }catch (Exception e){
            LogManager.getLogManager().getLogger("hudson.WebAppMain").log(Level.SEVERE, "[GRAPES] Failed to send notification: ", e);
            logger.println("[GRAPES] Failed send module report");
        }

        return true;

    }

    /**
     * Returns the current Grapes configuration of the job
     *
     * @return GrapesConfig
     */
    private GrapesConfig getConfig() {
        final GrapesNotifierDescriptor descriptor = (GrapesNotifierDescriptor) getDescriptor();
        return descriptor.getConfiguration(configName);
    }

    /**
     * Returs the location of Grapes module file
     *
     * @param build AbstractBuild<?, ?>
     * @return FilePath
     */
    private FilePath getModuleFilePath(final AbstractBuild<?, ?> build) {
        return build.getWorkspace().child("target/" + GrapesPlugin.GRAPES_WORKING_FOLDER + "/" + GrapesPlugin.GRAPES_MODULE_FILE);
    }


    /**
     * Descriptor for {@link GrapesNotifier}. Used as a singleton.
     *
     * <p>
     * See <tt>src/main/resources/org/axway/grapes/jenkins/GrapesNotifier/*.jelly</tt>
     * for the actual HTML fragment for the config screen.
     */
    @Extension
    public static final class GrapesNotifierDescriptor extends BuildStepDescriptor<Publisher> {

        private volatile List<GrapesConfig> servers;

        public GrapesNotifierDescriptor() {
            load();
        }

        public List<GrapesConfig> getServers() {
            return servers;
        }

        public void setServers(final List<GrapesConfig> servers) {
            this.servers = servers;
        }

        /**
         * Returns true if this task is applicable to the given project.
         * Limit the use of the notifier to maven jobs for now...
         *
         * @param jobType Class<? extends AbstractProject>
         * @return boolean
         */
        @Override
        public boolean isApplicable(@SuppressWarnings("rawtypes") final Class<? extends AbstractProject> jobType) {
            return AbstractMavenProject.class.isAssignableFrom(jobType);
        }

        /**
         * Returns a stored Grapes configuration regarding its name
         *
         * @param configName String
         * @return GrapesConfig
         */
        public GrapesConfig getConfiguration(final String configName) {
            final Logger logger = LogManager.getLogManager().getLogger("hudson.WebAppMain");
            for(GrapesConfig config : servers){
                if(config.getName().equals(configName)){
                    return config;
                }
            }

            logger.severe("[GRAPES] No Grapes configuration for " + configName);
            return null;
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) {
            req.bindJSON(this, json);
            save();
            return true;
        }

        /**
         * Returns the display name of the task in the job config
         *
         * @return String
         */
        @Override
        public String getDisplayName() {
            return "Configure Grapes Notifications";
        }

        // Configuration Validation

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value String This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckName(@QueryParameter final String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set a name");
            }
            if (value.length() < 4) {
                return FormValidation.warning("Isn't the name too short?");
            }

            return FormValidation.ok();
        }

        /**
         * Performs on-the-fly validation of the form field 'host'.
         *
         * @param value String This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckHost(@QueryParameter final String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("Please set a valid host");
            }
            return FormValidation.ok();
        }


        /**
         * Performs on-the-fly validation of the form field 'credentials'.
         *
         * @param host String
         * @param port String
         * @param timeout String
         * @return
         */
        public FormValidation doTestConnection(@QueryParameter final String host, @QueryParameter final String port, @QueryParameter final String timeout) {
            final GrapesClient client = new GrapesClient(host, port);

            if (client.isServerAvailable()) {
                return FormValidation.ok("Success.");
            }
            else {
                return FormValidation.warning("Server not reachable!");
            }

        }
    }
}
