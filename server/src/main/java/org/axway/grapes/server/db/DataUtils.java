package org.axway.grapes.server.db;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;

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
    public static List<String> getAllArtifacts(final DbModule module) {
        final List<String> gavcs = module.getArtifacts();

        for(DbModule submodule: module.getSubmodules()){
            gavcs.addAll(getAllArtifacts(submodule));
        }

        return gavcs;
    }

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
    public static List<DbDependency> getAllDbDependencies(final DbModule module) {
        final List<DbDependency> dependencies = new ArrayList<DbDependency>();
        dependencies.addAll(module.getDependencies());

        for(DbModule submodule: module.getSubmodules()){
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
    public static DbArtifact createDbArtifact(final String gavc) {
        final DbArtifact artifact = new DbArtifact();
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

        if(artifactInfo.length > 5){
            artifact.setOrigin(artifactInfo[5]);
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
        String groupId = null, artifactId = null, version = null, classifier = null, extension = null, origin = null;
        final String[] artifactInfo = gavc.split(":");

        if(artifactInfo.length > 0){
            groupId = artifactInfo[0];
        }

        if(artifactInfo.length > 1){
            artifactId = artifactInfo[1];
        }

        if(artifactInfo.length > 2){
            version = artifactInfo[2];
        }

        if(artifactInfo.length > 3){
            classifier = artifactInfo[3];
        }

        if(artifactInfo.length > 4){
            extension = artifactInfo[4];
        }

        if(artifactInfo.length > 5){
            origin = artifactInfo[5];
        }

        return DataModelFactory.createArtifact(groupId, artifactId, version, classifier, null, extension, origin);
    }

    /**
     * Return the list of all the module submodules
     *
     * @param module
     * @return List<DbModule>
     */
    public static List<DbModule> getAllSubmodules(final DbModule module) {
        final List<DbModule> submodules = new ArrayList<DbModule>();
        submodules.addAll(module.getSubmodules());

        for(DbModule submodule: module.getSubmodules()){
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
