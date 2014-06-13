package org.axway.grapes.server.db;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.datamodel.*;

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
	 * Transform a license from client/server model to database model
	 * 
	 * @param license the license to transform
	 * @return DbLicense return a license in database model
	 */
	public static DbLicense getDbLicense(final License license) {
		final DbLicense dbLicense = new DbLicense();
		dbLicense.setName(license.getName());
		dbLicense.setLongName(license.getLongName());
		dbLicense.setComments(license.getComments());
		dbLicense.setRegexp(license.getRegexp());
		dbLicense.setUrl(license.getUrl());
		
		return dbLicense;
	}


    /**
     * Transform a license from database model to client/server model
     *
     * @param license DbLicense the license to transform
     * @return License return a license in database model
     */
    public static License getLicense(final DbLicense license) {
        return DataModelFactory.createLicense(license.getName(),
                license.getLongName(),
                license.getComments(),
                license.getRegexp(),
                license.getUrl());
    }

    /**
     * Transform an artifact from client/server model to database model
     *
     * WARNING: This transformation does not take licenses into account!!!
     *
     * @param artifact the artifact to transform
     * @return DbArtifact
     */
    public static DbArtifact getDbArtifact(final Artifact artifact) {
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId(artifact.getGroupId());
        dbArtifact.setArtifactId(artifact.getArtifactId());
        dbArtifact.setVersion(artifact.getVersion());
        dbArtifact.setClassifier(artifact.getClassifier());
        dbArtifact.setType(artifact.getType());
        dbArtifact.setExtension(artifact.getExtension());
        dbArtifact.setPromoted(artifact.isPromoted());

        dbArtifact.setSize(artifact.getSize());
        dbArtifact.setDownloadUrl(artifact.getDownloadUrl());
        dbArtifact.setProvider(artifact.getProvider());

        return dbArtifact;
    }

    /**
     * Transform an artifact from database model to client/server model
     *
     * @param dbArtifact the artifact to transform
     * @return Artifact return a license in database model
     */
    public static Artifact getArtifact(final DbArtifact dbArtifact) {
        final Artifact artifact = DataModelFactory.createArtifact(
                dbArtifact.getGroupId(),
                dbArtifact.getArtifactId(),
                dbArtifact.getVersion(),
                dbArtifact.getClassifier(),
                dbArtifact.getType(),
                dbArtifact.getExtension()
        );

        artifact.setPromoted(dbArtifact.isPromoted());
        artifact.setSize(dbArtifact.getSize());
        artifact.setDownloadUrl(dbArtifact.getDownloadUrl());
        artifact.setProvider(dbArtifact.getProvider());

        for(String licenseId: dbArtifact.getLicenses()){
            artifact.addLicense(licenseId);
        }

        return artifact;
    }

    /**
     * Transform a module from client/server model to database model
     *
     * @param module the module to transform
     * @return DbModule
     */
    public static DbModule getDbModule(final Module module) {
        final DbModule dbModule = new DbModule();

        dbModule.setName(module.getName());
        dbModule.setVersion(module.getVersion());
        dbModule.setPromoted(module.isPromoted());
        dbModule.setSubmodule(module.isSubmodule());

        // Artifact
        for(Artifact artifact: module.getArtifacts()){
            final DbArtifact dbArtifact = getDbArtifact(artifact);
            dbModule.addArtifact(dbArtifact);
        }

        // Dependencies
        for(Dependency dependency : module.getDependencies()){
            dbModule.addDependency(dependency.getTarget().getGavc(), dependency.getScope());
        }

        //SubModules
        final StringBuilder sb = new StringBuilder();
        for(Module submodule: module.getSubmodules()){
            final DbModule dbSubmodule = getDbModule(submodule);
            dbModule.addSubmodule(dbSubmodule);
            sb.setLength(0);
        }

        return dbModule;
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
        final List<DbDependency> dependencies = module.getDependencies();

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
    public static DbArtifact createArtifact(final String gavc) {
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

        return artifact;
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

    /**
     * Transform an organization from client/server model to database model
     *
     * @param organization Organization
     * @return DbOrganization
     */
    public static DbOrganization getDbOrganization(final Organization organization) {
        final DbOrganization dbOrganization = new DbOrganization();
        dbOrganization.setName(organization.getName());
        dbOrganization.getCorporateGroupIdPrefixes().addAll(organization.getCorporateGroupIdPrefixes());

        return dbOrganization;
    }

    /**
     * Transform an organization from database model to client/server model
     *
     * @param dbOrganization DbOrganization
     * @return Organization
     */
    public static Organization getOrganization(final DbOrganization dbOrganization) {
        final Organization organization = DataModelFactory.createOrganization(dbOrganization.getName());
        organization.getCorporateGroupIdPrefixes().addAll(dbOrganization.getCorporateGroupIdPrefixes());

        return organization;
    }
}
