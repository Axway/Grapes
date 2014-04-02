package org.axway.grapes.jenkins;

import hudson.FilePath;
import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.FileUtils;
import org.axway.grapes.commons.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Placeholder for plugin entry point.
 * This class also acts as a container for methods used throughout the plugin.
 *
 * @author jdcoffre
 *
 */
public class GrapesPlugin extends Plugin {

    public static final String REPORT_FILE = "grapesReports/module.json";

    public static final String GRAPES_WORKING_FOLDER = "grapes";

    public static final String GRAPES_MODULE_FILE = "module.json";

    /**
     * Returns the path or URL to access web resources from this plugin.
     *
     * @return resource path
     */
    public static String getPluginResourcePath() {
        PluginWrapper wrapper = Hudson.getInstance().getPluginManager()
                .getPlugin(GrapesPlugin.class);

        return "/plugin/" + wrapper.getShortName() + "/";
    }

    /**
     * Provides build report folder for Grapes archives
     *
     * @param build AbstractBuild
     * @return FilePath
     */
    public static FilePath getReportFolder(AbstractBuild<?, ?> build) {
        assert build != null;
        File reportFolder = new File(build.getRootDir(), REPORT_FILE);
        return  new FilePath(reportFolder);
    }

    /**
     * Returns Grapes module of a build
     *
     * @param moduleFile
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Module getModule(final File moduleFile) throws IOException, InterruptedException {
        if (moduleFile.exists()) {
            final String serializedModule= FileUtils.read(moduleFile);
            return JsonUtils.unserializeModule(serializedModule);
        }

        return null;
    }

    /**
     * Returns Grapes Jenkins plugin logger
     *
     * @return Logger
     */
    public static Logger getLogger(){
        return LogManager.getLogManager().getLogger("org.axway.grapes.jenkins.GrapesPlugin");
    }
}
