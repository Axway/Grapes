package org.axway.grapes.maven.converter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.GrapesMavenPlugin;
import org.axway.grapes.maven.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module Aggregator
 *
 * <p>Aggregates the modules of the sub-maven projects to create a single root module file.</p>
 *
 * @author jdcoffre
 */
public class ModuleAggregator {


    private final List<MavenProject> reactorProjects;
    
    private final File workingFolder;

    public ModuleAggregator(final List<MavenProject> reactorProjects) {
        this.workingFolder = GrapesMavenPlugin.getGrapesPluginWorkingFolder(reactorProjects.get(0));
        this.reactorProjects = reactorProjects;
    }

    /**
     * Checks all the available reports and aggregates the existing ones
     *
     * @throws IOException
     * @throws MojoExecutionException
     */
    public void aggregate() throws IOException, MojoExecutionException {
        final Module rootModule = GrapesMavenPlugin.getModule(workingFolder, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);
        final Map<String, Module> subModules = getSubModuleReports();

        for(Map.Entry<String,Module> submodule : subModules.entrySet()){
            final MavenProject parentProject = getParentProject(submodule.getKey());

            if(parentProject != null){
                final Boolean updated = updateParent(parentProject, submodule.getValue());

                // removes the children that are taken into accounts into parent reports
                if(updated){
                    final File subModuleFile = new File(workingFolder, GrapesMavenPlugin.getSubModuleFileName(submodule.getKey()));
                    subModuleFile.delete();
                }
            }
        }
    }

    /**
     * Finds the parent report and update it with sub-modules information
     *
     * @param parentProject Module
     * @param subModule MavenProject
     * @return Boolean true if the update has been performed successfully
     * @throws MojoExecutionException
     * @throws IOException
     */
    private Boolean updateParent(final MavenProject parentProject, final Module subModule) throws MojoExecutionException, IOException {
        final File parentModuleFile = getModuleReportFile(parentProject);
        final Module rootModule = GrapesMavenPlugin.getModule(workingFolder, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);

        // Parent is serialized into a temp file
        if(parentModuleFile.exists()){
            final String serializedParent = FileUtils.read(parentModuleFile);
            parentModuleFile.delete();

            final Module parentModule = JsonUtils.unserializeModule(serializedParent);
            parentModule.addSubmodule(subModule);
            FileUtils.serialize(workingFolder, JsonUtils.serialize(parentModule), parentModuleFile.getName());

            return true;
        }
        // parent is serialized into module.son file
        else if(contains(rootModule, parentProject)){
            final Module parentModule = getSubModule(rootModule, parentProject);
            parentModule.addSubmodule(subModule);

            final File rootModuleFile = new File(workingFolder, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);
            rootModuleFile.delete();
            FileUtils.serialize(workingFolder, JsonUtils.serialize(rootModule), GrapesMavenPlugin.MODULE_JSON_FILE_NAME);

            return true;
        }

        return false;
    }

    /**
     * Loads the available reports and returns the corresponding Modules
     *
     * @return Map<String,Module>
     * @throws IOException
     * @throws MojoExecutionException
     */
    private Map<String, Module> getSubModuleReports() throws IOException, MojoExecutionException {
        final Map<String, Module> subModules = new HashMap<String, Module>();

        for(MavenProject project: reactorProjects){
            final String fileName = GrapesMavenPlugin.getSubModuleFileName(project.getBasedir().getName());
            final Module subModule = GrapesMavenPlugin.getModule(workingFolder, fileName);

            if(subModule != null){
                subModules.put(project.getBasedir().getName(), subModule);
            }
        }

        return subModules;
    }

    /**
     * Return the parent project matching the sub-module key
     *
     * @param subModuleKey String
     * @return MavenProject
     */
    private MavenProject getParentProject(final String subModuleKey) {
        for(MavenProject project: reactorProjects){
            final List<String> subModules = project.getModules();
            if(subModules != null &&
                    subModules.contains(subModuleKey)){
                return project;
            }
        }

        return null;
    }

    /**
     * Returns the report file of the corresponding project
     *
     * @param project MavenProject
     * @return File
     */
    private File getModuleReportFile(final MavenProject project) {
        if(project.equals(reactorProjects.get(0))){
            return new File(workingFolder, GrapesMavenPlugin.MODULE_JSON_FILE_NAME);
        }

        return new File(workingFolder, GrapesMavenPlugin.getSubModuleFileName(project.getBasedir().getName()));
    }

    private Module getSubModule(final Module rootModule, final MavenProject parentProject) {
        final String parentSubModuleName = GrapesTranslator.generateModuleName(parentProject);

        for(Module subModule: rootModule.getSubmodules()){
            if(subModule.getName().equals(parentSubModuleName)){
                return subModule;
            }

            final Module result = getSubModule(subModule,parentProject);
            if(result != null){
                return result;
            }
        }

        return null;
    }

    /**
     * Checks if a module has a project as sub module
     *
     * @param rootModule Module
     * @param parentProject MavenProject
     * @return boolean
     */
    private boolean contains(final Module rootModule, final MavenProject parentProject) {
        return getSubModule(rootModule, parentProject) != null;
    }

}
