package org.axway.grapes.core.handler;

import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.webapi.resources.DependencyComplete;
import org.axway.grapes.core.webapi.resources.ModuleComplete;
import org.axway.grapes.core.service.ArtifactService;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.DataModelFactory;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Data Utility
 * 
 * <p>Utility class that performs data transformation.</p>
 * 
 * @author jdcoffre
 */
@Service
public  class DataUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DataUtils.class);
    @Requires(optional = true)
    ArtifactService artifactService;

    public DataUtils(){
        // Hide utility class constructor
    }

    /**
     * Return a gavc list of all the artifact contained into the module (an its submodules)
     *
     * @param module
     * @return List<String>
     */
    public   List<String> getAllArtifactsGavcs(final Module module) {
        List<String> gavcs = new ArrayList<>();
        for (String artifact: module.getArtifacts()){
            gavcs.add(artifact);
        }

        for (Module submodule : module.getSubmodules()) {
            gavcs.addAll(getAllArtifactsGavcs(submodule));
        }

        return gavcs;
    }

    /**
     * Return a list of all the artifact contained into the module (an its submodules)
     *
     * @param module
     * @return Set<Artifact>
     */
    public  Set<Artifact> getAllArtifacts(final ModuleComplete module) {
         Set<Artifact> artifacts = new HashSet<Artifact>();
        artifacts.addAll(module.getArtifacts());

        for(ModuleComplete submodule: module.getSubmodules()){
            artifacts.addAll(getAllArtifacts(submodule));
        }

        return artifacts;
    }

    public Set<Artifact> getAllArtifacts(List<String> gavcs){
        Set<Artifact> artifacts = new HashSet<Artifact>();
       for(String gavc : gavcs){
           try {
               artifacts.add(artifactService.getArtifact(gavc));
           }catch (NoSuchElementException e){
               final StringBuilder sb = new StringBuilder();
               sb.append("Artifact ");
               sb.append(gavc);
               sb.append(" does not exist in database");
               LOG.error(sb.toString());
               artifacts.add(createArtifact(gavc));
           }
       }
        return artifacts;
    }
    /**
     * Return a list of all the dependencies contained into the module (an its submodules)
     *
     * @param module
     * @return Set<Dependency>
     */
    public  Set<DependencyComplete> getAllDependencies(final ModuleComplete module) {
        final Set<DependencyComplete> dependencies = new HashSet<DependencyComplete>();
        dependencies.addAll(module.getDependencies());

        for(ModuleComplete submodule: module.getSubmodules()){
            dependencies.addAll(getAllDependencies(submodule));
        }

        return dependencies;
    }

    /**
     * Return a list of all the dependencies contained into the module (an its submodules)
     *
     * @param module
     * @return List<DbDependency>
     */
    public  List<Dependency> getAllDbDependencies(final Module module) {
//        LOG.info("whoo in data utiles getting dependecies");
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.addAll(module.getDependencies());
//        LOG.info("here dependency list: " + Lists.newArrayList(dependencies));
//        LOG.info("subs list" + Lists.newArrayList(module.getSubmodules()));
        for(Module submodule: module.getSubmodules()){
//            LOG.error("for sub : " + submodule + " " + submodule.toString() + "depends: " + submodule.getDependencies() + " subs " + submodule.getSubmodules());

            dependencies.addAll(getAllDbDependencies(submodule));
        }
//    LOG.error("returning now: "+dependencies.toString());
        return dependencies;
    }

    /**
     * Split a module Id to get the module name
     * @param moduleId
     * @return String
     */
    public  String getModuleName(final String moduleId) {
        final int splitter = moduleId.indexOf(':');
        if(splitter == -1){
            return moduleId;
        }
        return moduleId.substring(0, splitter);
    }

    /**
     * Split a module Id to get the module version
     * @param moduleId
     * @return String
     */
    public  String getModuleVersion(final String moduleId) {
        final int splitter = moduleId.lastIndexOf(':');
        if(splitter == -1){
            return moduleId;
        }
        return moduleId.substring(splitter+1);
    }

    /**
     * Split an artifact gavc to get the groupId
     * @param gavc
     * @return String
     */
    public  String getGroupId(final String gavc) {
        final int splitter = gavc.indexOf(':');
        if(splitter == -1){
            return gavc;
        }
        return gavc.substring(0, splitter);
    }
    /**
     * Transform a module from client/server model to database model
     *
     * @param moduleComplete the module to transform
     * @return DbModule
     */
    public Module translateIntoModule(final ModuleComplete moduleComplete) {
        final Module module = new Module();

        module.setName(moduleComplete.getName());
        module.setVersion(moduleComplete.getVersion());
        module.setPromoted(moduleComplete.isPromoted());
        module.setSubmodule(moduleComplete.getIsSubmodule());
        module.setOrganization(moduleComplete.getOrganization());
        module.setBuildInfo(moduleComplete.getBuildInfo());


        // Artifact
        for(Artifact artifact: moduleComplete.getArtifacts()){

            module.addArtifact(artifact);
        }

        // Dependencies
        for(DependencyComplete dependency : moduleComplete.getDependencies()){
            module.addDependency(dependency.getTarget(), dependency.getScope());
        }

        //SubModules

        for(ModuleComplete submoduleComplete: moduleComplete.getSubmodules()){
            final Module submodule = translateIntoModule(submoduleComplete);
            module.addSubmodule(submodule);

        }

        return module;
    }


    /**
     * Generates an artifact starting from gavc
     *
     * WARNING: use this method only if you have a missing reference in the database!!!
     *
     * @param gavc
     * @return DbArtifact
     */
    public  Artifact createDbArtifact(final String gavc) {
        final Artifact artifact = new Artifact();
        final String[] artifactInfo = gavc.split(":");

        if(artifactInfo.length > 0){
            artifact.setGroupId(artifactInfo[0]);
        }

        if(artifactInfo.length > 1){
            artifact.setArtifactId(artifactInfo[1]);
        }

        if(artifactInfo.length > 2){
            artifact.setVersion(artifactInfo[2]);
        }

        if(artifactInfo.length > 3){
            artifact.setClassifier(artifactInfo[3]);
        }

        if(artifactInfo.length > 4){
            artifact.setExtension(artifactInfo[4]);
        }

        return artifact;
    }

    /**
     * Generates an artifact starting from gavc
     *
     * WARNING: use this method only if you have a missing reference in the database!!!
     *
     * @param gavc
     * @return DbArtifact
     */
    public  Artifact createArtifact(final String gavc) {
        String groupId = null, artifactId = null, version = null, classifier = null, extension = null;
        final String[] artifactInfo = gavc.split(":");

        if(artifactInfo.length > 0){
            groupId = artifactInfo[0];
        }

        if(artifactInfo.length > 1){
            artifactId = artifactInfo[1];
        }

        if(artifactInfo.length > 2){
            version= artifactInfo[2];
        }

        if(artifactInfo.length > 3){
            classifier= artifactInfo[3];
        }

        if(artifactInfo.length > 4){
            extension= artifactInfo[4];
        }

        return DataModelFactory.createArtifact(groupId, artifactId, version, classifier, null, extension);
    }

    /**
     * Return the list of all the module submodules
     *
     * @param module
     * @return List<DbModule>
     */
    public  List<Module> getAllSubmodules(final Module module) {
        final List<Module> submodules = new ArrayList<Module>();
        submodules.addAll(module.getSubmodules());

        for(Module submodule: module.getSubmodules()){
            submodules.addAll(getAllSubmodules(submodule));
        }

        return submodules;
    }

    /**
     * Bubble sort
     *
     * @param targets
     */
    public  void sort(final List<Artifact> targets) {
        int n = targets.size();
        while(n != 0){
            int newn = 0;

            for(int i = 1 ; i <= n-1 ; i++){
                if (targets.get(i-1).toString().compareTo(targets.get(i).toString()) > 0){
                    Collections.swap(targets, i - 1, i);
                    newn = i;
                }
            }

            n = newn;
        }
    }
}
