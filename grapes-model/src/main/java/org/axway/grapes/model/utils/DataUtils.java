package org.axway.grapes.model.utils;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.DataModelFactory;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Module;


import java.util.*;

/**
 * Data Utility
 * 
 * <p>Utility class that performs data transformation.</p>
 * 
 * @author jdcoffre
 */
public final class DataUtils {

    private DataUtils(){
        // Hide utility class constructor
    }

    /**
     * Return a gavc list of all the artifact contained into the module (an its submodules)
     *
     * @param module
     * @return List<String>
     */
//    public static List<String> getAllArtifacts2(final Module module) {
//        final Set<Artifact> gavcs = module.getArtifacts();
//
//        for(Module submodule: module.getSubmodules()){
//            gavcs.addAll(getAllArtifacts(submodule));
//        }
//
//        return gavcs;
//    }

    /**
     * Return a list of all the artifact contained into the module (an its submodules)
     *
     * @param module
     * @return Set<Artifact>
     */
    public static Set<Artifact> getAllArtifacts(final Module module) {
        final Set<Artifact> artifacts = new HashSet<Artifact>();
        artifacts.addAll(module.getArtifacts());

        for(Module submodule: module.getSubmodules()){
            artifacts.addAll(getAllArtifacts(submodule));
        }

        return artifacts;
    }

    /**
     * Return a list of all the dependencies contained into the module (an its submodules)
     *
     * @param module
     * @return Set<Dependency>
     */
    public static Set<Dependency> getAllDependencies(final Module module) {
        final Set<Dependency> dependencies = new HashSet<Dependency>();
        dependencies.addAll(module.getDependencies());

        for(Module submodule: module.getSubmodules()){
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
    public static List<Dependency> getAllDbDependencies(final Module module) {
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.addAll(module.getDependencies());

        for(Module submodule: module.getSubmodules()){
            dependencies.addAll(getAllDbDependencies(submodule));
        }

        return dependencies;
    }

    /**
     * Split a module Id to get the module name
     * @param moduleId
     * @return String
     */
    public static String getModuleName(final String moduleId) {
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
    public static String getModuleVersion(final String moduleId) {
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
    public static String getGroupId(final String gavc) {
        final int splitter = gavc.indexOf(':');
        if(splitter == -1){
            return gavc;
        }
        return gavc.substring(0, splitter);
    }

    /**
     * Generates an artifact starting from gavc
     *
     * WARNING: use this method only if you have a missing reference in the database!!!
     *
     * @param gavc
     * @return DbArtifact
     */
    public static Artifact createDbArtifact(final String gavc) {
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
    public static Artifact createArtifact(final String gavc) {
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
    public static List<Module> getAllSubmodules(final Module module) {
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
    public static void sort(final List<Artifact> targets) {
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
