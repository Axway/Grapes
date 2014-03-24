package org.axway.grapes.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Module Aggregator
 *
 * <p>Aggregates the modules of the sub-maven projects to create a single root module file.</p>
 *
 * @author jdcoffre
 */
public class ModuleAggregator {


    private final List<MavenProject> reactorProjects;

    private final Hashtable<String, List<MavenProject>> subModuleDictionary = new Hashtable<String, List<MavenProject>>();

    private final File rootWorkingFolder;

    public ModuleAggregator(final List<MavenProject> reactorProjects) {
        this.reactorProjects = reactorProjects;

        for(MavenProject project: reactorProjects){
            final List<MavenProject> subProjects = getSubProjects(project);
            subModuleDictionary.put(project.getName(), subProjects);
        }

        rootWorkingFolder = GrapesMavenPlugin.getGrapesPluginWorkingFolder(reactorProjects.get(0));
    }

    /**
     * Identify the direct sub-module projects of a project
     *
     * @param project MavenProject
     * @return List<MavenProject>
     */
    private List<MavenProject> getSubProjects(final MavenProject project) {
        final List<MavenProject> subProjects = new ArrayList<MavenProject>();
        final List<String> subModules = project.getModules();

        for(MavenProject subProject: reactorProjects){
            final String subModuleName = subProject.getBasedir().getName();
            if(subModules.contains(subModuleName)){
                subProjects.add(subProject);
            }
        }

        return subProjects;
    }

    /**
     * Aggregates all sub-module reports to generate one single report at the root Grapes working folder
     *
     * @return Module
     * @throws IOException
     * @throws MojoExecutionException
     */
    public Module aggregate() throws IOException, MojoExecutionException {
        final Module rootModule = aggregate(reactorProjects.get(0));
        final String serializedRootModule = JsonUtils.serialize(rootModule);
        FileUtils.serialize(rootWorkingFolder, serializedRootModule, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);

        return rootModule;
    }

    /**
     * Aggregate the module information of the project with its sub modules
     *
     * @param project MavenProject
     * @return Module
     * @throws IOException
     * @throws MojoExecutionException
     */
    private Module aggregate(final MavenProject project) throws IOException, MojoExecutionException {
        final File moduleFile = new File(GrapesMavenPlugin.getGrapesPluginWorkingFolder(project), GrapesMavenPlugin.TMP_MODULE_JSON_FILE_NAME);
        final String serializedModule = FileUtils.read(moduleFile);
        final Module module = JsonUtils.unserializeModule(serializedModule);

        for(MavenProject subProject: subModuleDictionary.get(project.getName())){
            final Module subModule = aggregate(subProject);
            module.addSubmodule(subModule);
        }

        return module;
    }

    /**
     * Removes temporary ans partial sub-module information files
     */
    public void cleanTmpFiles() {
        for(MavenProject project : reactorProjects){
            final File grapesWorkingFolder = GrapesMavenPlugin.getGrapesPluginWorkingFolder(project);
            final File tmpFile = new File(grapesWorkingFolder, GrapesMavenPlugin.TMP_MODULE_JSON_FILE_NAME);

            if(tmpFile.exists()){
                tmpFile.delete();
            }
        }
    }
}
