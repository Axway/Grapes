package org.axway.grapes.maven;

import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Grapes Maven plugin
 *
 *
 * <p>This class acts as a container for methods used throughout the plugin.</p>
 *
 * @author jdcoffre
 */
public class GrapesMavenPlugin {

    public final static String MODULE_JSON_FILE_NAME = "module.json";

    public final static String TMP_MODULE_JSON_FILE_NAME = "module_tmp.json";

    /**
     * Returns Grapes working folder of a project
     *
     * @param project
     * @return
     */
    public static File getGrapesPluginWorkingFolder(final MavenProject project){
        return new File(project.getBuild().getDirectory() + "/grapes");
    }

}
