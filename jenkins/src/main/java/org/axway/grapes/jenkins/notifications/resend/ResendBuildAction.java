package org.axway.grapes.jenkins.notifications.resend;

import hudson.FilePath;
import hudson.model.Action;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.FileUtils;
import org.axway.grapes.commons.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

/**
 * Resend Action
 *
 * <p>Associated to a build, this action is created when the notification failed. The aim is to keep the information about modules to resend.</p>
 * <p>The configuration for resend is stored in the job configuration. This action will not be displayed in the build because notification resend are performed from the administration panel.</p>
 *
 * @author jdcoffre
 */
public class ResendBuildAction implements Action {

    private static final String SENT_INFO_FILE = ".sent";

    private final FilePath reportPath;
    private String moduleName;
    private String moduleVersion;

    public ResendBuildAction(final FilePath reportPath, final String moduleName, final String moduleVersion) {
        this.reportPath = reportPath;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    // Hide the build action
    public String getIconFileName() {
        return null;
    }

    // Hide the build action
    public String getDisplayName() {
        return null;
    }

    // Hide the build action
    public String getUrlName() {
        return null;
    }

    /**
     * Provides the module file to resend
     *
     * @return Module
     * @throws IOException
     */
    public Module getModule() throws IOException, InterruptedException {
        final String serializedModule = FileUtils.read(new File(reportPath.toURI()));
        return JsonUtils.unserializeModule(serializedModule);
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    /**
     * Create a file .sent into Grapes report folder of the build to warn that the report does not need to be sent anymore
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void discard() throws IOException, InterruptedException {
        final File reportFolder = new File(reportPath.getParent().toURI());
        FileUtils.touch(reportFolder, SENT_INFO_FILE);
    }

    /**
     * Checks if the report has already been sent o not
     *
     * @return Boolean
     */
    public Boolean toSend() throws IOException, InterruptedException {
        final File reportFolder = new File(reportPath.getParent().toURI());
        final File sentInfoFile = new File(reportFolder, SENT_INFO_FILE);

        return !sentInfoFile.exists();
    }
}
