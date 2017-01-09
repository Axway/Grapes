package org.axway.grapes.server.db;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.datamodel.*;

/**
 * Model Mapper
 *
 * <p>Model mapper maps the client/server model and the database model.</p>
 *
 * @author jdcoffre
 */
public class ModelMapper {

    private final RepositoryHandler repositoryHandler;

    public ModelMapper(final RepositoryHandler repoHandler) {
        this.repositoryHandler = repoHandler;
    }

    /**
     * Transform an organization from client/server model to database model
     *
     * @param organization Organization
     * @return DbOrganization
     */
    public DbOrganization getDbOrganization(final Organization organization) {
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
    public Organization getOrganization(final DbOrganization dbOrganization) {
        final Organization organization = DataModelFactory.createOrganization(dbOrganization.getName());
        organization.getCorporateGroupIdPrefixes().addAll(dbOrganization.getCorporateGroupIdPrefixes());

        return organization;
    }

    /**
     * Transform a license from client/server model to database model
     *
     * @param license the license to transform
     * @return DbLicense return a license in database model
     */
    public DbLicense getDbLicense(final License license) {
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
     * @param dbLicense DbLicense the license to transform
     * @return License return a license in database model
     */
    public License getLicense(final DbLicense dbLicense) {
        final License license = DataModelFactory.createLicense(dbLicense.getName(),
                dbLicense.getLongName(),
                  dbLicense.getComments(),
                    dbLicense.getRegexp(),
                dbLicense.getUrl());

        if(dbLicense.isApproved() != null){
            license.setApproved(dbLicense.isApproved());
        }

        if(license.getLongName().isEmpty() &&
             license.getComments().isEmpty() &&
               license.getUrl().isEmpty() &&
                 license.getRegexp().isEmpty()){
            license.setUnknown(true);
        }

        return license;
    }

    /**
     * Transform an artifact from client/server model to database model
     *
     * WARNING: This transformation does not take licenses into account!!!
     *
     * @param artifact the artifact to transform
     * @return DbArtifact
     */
    public DbArtifact getDbArtifact(final Artifact artifact) {
        final DbArtifact dbArtifact = new DbArtifact();
        dbArtifact.setGroupId(artifact.getGroupId());
        dbArtifact.setArtifactId(artifact.getArtifactId());
        dbArtifact.setVersion(artifact.getVersion());
        dbArtifact.setClassifier(artifact.getClassifier());
        dbArtifact.setType(artifact.getType());
        dbArtifact.setExtension(artifact.getExtension());
        dbArtifact.setOrigin(artifact.getOrigin());
        dbArtifact.setPromoted(artifact.isPromoted());
        dbArtifact.setLicenses(artifact.getLicenses());

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
    public Artifact getArtifact(final DbArtifact dbArtifact) {
        final Artifact artifact = DataModelFactory.createArtifact(
                dbArtifact.getGroupId(),
                dbArtifact.getArtifactId(),
                dbArtifact.getVersion(),
                dbArtifact.getClassifier(),
                dbArtifact.getType(),
                dbArtifact.getExtension(),
		        dbArtifact.getOrigin()
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
    public DbModule getDbModule(final Module module) {
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
     * Transform a module from database model to client/server model
     *
     * @param dbModule DbModule
     * @return Module
     */
    public Module getModule(final DbModule dbModule) {
        final Module module =DataModelFactory.createModule(dbModule.getName(), dbModule.getVersion());
        module.setPromoted(dbModule.isPromoted());
        module.setSubmodule(dbModule.isSubmodule());

        //Artifacts
        for(String gavc: dbModule.getArtifacts()){
            final DbArtifact dbArtifact = repositoryHandler.getArtifact(gavc);
            final Artifact artifact = getArtifact(dbArtifact);
            module.addArtifact(artifact);
        }

        //Dependencies
        for(DbDependency dbDependency: dbModule.getDependencies()){
            final Dependency dependency = getDependency(dbDependency, module.getName(), module.getVersion());
            dependency.setSourceName(module.getName());
            dependency.setSourceVersion(module.getVersion());
            module.addDependency(dependency);
        }

        //Submodules
        for(DbModule dbSubmodule: dbModule.getSubmodules()){
            module.addSubmodule(getModule(dbSubmodule));
        }

        return module;
    }


    /**
     * Transform a dependency from database model to client/server model
     *
     * @param dbDependency DbDependency
     * @return Dependency
     */
    public Dependency getDependency(final DbDependency dbDependency, final String sourceName, final String sourceVersion) {
        final DbArtifact dbArtifact = repositoryHandler.getArtifact(dbDependency.getTarget());
        final Artifact artifact;

        if(dbArtifact == null){
            artifact = DataUtils.createArtifact(dbDependency.getTarget());
        } else {
            artifact = getArtifact(dbArtifact);
        }

        final Dependency dependency = DataModelFactory.createDependency(artifact, dbDependency.getScope());
        dependency.setSourceName(sourceName);
        dependency.setSourceVersion(sourceVersion);

        return dependency;
    }
}
