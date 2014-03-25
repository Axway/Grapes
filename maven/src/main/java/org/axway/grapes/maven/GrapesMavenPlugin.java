package org.axway.grapes.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

import static org.axway.grapes.maven.utils.FileUtils.read;

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

    public final static String TMP_MODULE_FILE_SUFFIX = "_tmp.json";

    /**
     * Returns Grapes working folder of a project
     *
     * @param rootProject
     * @return
     */
    public static File getGrapesPluginWorkingFolder(final MavenProject rootProject){
        return new File(rootProject.getBuild().getDirectory() + "/grapes");
    }

    /**
     * Returns the temp file names where the information of the sub-module is stored
     *
     * @param subModuleName String
     * @return String
     */
    public static String getSubModuleFileName(final String subModuleName) {
        return subModuleName + TMP_MODULE_FILE_SUFFIX;
    }

    /**
     * Return a module from Json file in a targeted folder
     *
     * @param folder File
     * @param moduleJsonFileName String
     * @return Module
     * @throws MojoExecutionException
     * @throws IOException
     */
    public static Module getModule(final File folder, final String moduleJsonFileName) throws MojoExecutionException, IOException {
        final File moduleFile = new File(folder, moduleJsonFileName);

        if(moduleFile.exists()){
            final String serializedModule = read(moduleFile);
            return JsonUtils.unserializeModule(serializedModule);
        }

        return null;
    }
}
