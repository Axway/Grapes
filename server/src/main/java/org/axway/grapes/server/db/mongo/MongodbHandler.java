package org.axway.grapes.server.db.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.sun.jersey.api.NotFoundException;
import org.axway.grapes.server.config.DataBaseConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mongodb Handler
 *
 * <p>Repository Handler designed for mongodb<p/>
 *
 * @author jdcoffre
 */
public class MongodbHandler implements RepositoryHandler {
    private final DB db;

    public MongodbHandler(final DataBaseConfig config) throws UnknownHostException {
        final ServerAddress address = new ServerAddress(config.getHost() , config.getPort());
        final MongoClient mongo = new MongoClient(address);
        db = mongo.getDB(config.getDatastore());

        if(config.getUser() != null && config.getPwd() != null){
            db.authenticate(config.getUser(), config.getPwd());
        }
    }

    /**
     * Ensure that the mongo database indexes are built
     */
    public void ensureIndexes() {
		db.getCollection(DbCollections.DB_MODULES).ensureIndex(DbModule.UID_DB_FIELD);
        db.getCollection(DbCollections.DB_ARTIFACTS).ensureIndex(DbArtifact.GAV_DB_FIELD);
        db.getCollection(DbCollections.DB_LICENSES).ensureIndex(DbLicense.NAME_DB_FIELD);
        db.getCollection(DbCollections.DB_CREDENTIALS).ensureIndex(DbCredential.USER_FIELD);
    }
    
    /**
	 * Initialize a connection with the database using Jongo.
	 * 
	 * <p>WARNING: The database connection is closed only when Datastore instance is garbage collected!!!!</p>
	 * 
	 * @return Jongo instance
	 */
	private Jongo getJongoDataStore() {
		return new Jongo(db);
	}

    @Override
	public void store(final DbCredential credential) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection dbCredentials = datastore.getCollection(DbCollections.DB_CREDENTIALS);

		final DbCredential dbCredential = getCredential(credential.getUser());

        if(dbCredential == null){
            dbCredentials.save(credential);
        }
        else{
            dbCredentials.update(dbCredential.getId()).with(credential);
        }
	}

    @Override
	public Iterable<DbCredential> getCredentials() {
		final Jongo datastore = getJongoDataStore();
		
		return datastore.getCollection(DbCollections.DB_CREDENTIALS)
				.find().as(DbCredential.class);
	}

    @Override
    public void addUserRole(final String user, final AvailableRoles role) {
        final DbCredential credential = getCredential(user);

        if(credential == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        final Jongo datastore = getJongoDataStore();
        final MongoCollection credentials = datastore.getCollection(DbCollections.DB_CREDENTIALS);

        if(!credential.getRoles().contains(role)){
            credential.addRole(role);
            credentials.update(JongoUtils.generateQuery(DbCredential.USER_FIELD, user))
                    .with("{ $set: { \""+ DbCredential.ROLES_FIELD + "\": #}} " , credential.getRoles());
        }

    }

    @Override
    public void removeUserRole(final String user, final AvailableRoles role) {
        final DbCredential credential = getCredential(user);

        if(credential == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        final Jongo datastore = getJongoDataStore();
        final MongoCollection credentials = datastore.getCollection(DbCollections.DB_CREDENTIALS);

        if(credential.getRoles().contains(role)){
            credential.removeRole(role);
            credentials.update(JongoUtils.generateQuery(DbCredential.USER_FIELD, user))
                    .with("{ $set: { \""+ DbCredential.ROLES_FIELD + "\": #}} " , credential.getRoles());
        }
    }

    private DbCredential getCredential(final String user) {
		final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_CREDENTIALS)
				.findOne(JongoUtils.generateQuery(DbCredential.USER_FIELD, user))
					.as(DbCredential.class);
	}

    @Override
    public void store(final DbLicense license) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection dbLicenses = datastore.getCollection(DbCollections.DB_LICENSES);
        final DbLicense dbLicense = getLicense(license.getName());

        if(dbLicense == null){
            dbLicenses.save(license);
        }
        else {
            dbLicenses.update(dbLicense.getId()).with(license);
        }

    }

    @Override
    public List<String> getLicenseNames(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        final Iterable<DbLicense> dbLicenses = datastore.getCollection(DbCollections.DB_LICENSES)
                .find().as(DbLicense.class);

        final List<String> licenseNames = new ArrayList<String>();
        for(DbLicense dbLicense: dbLicenses){
            if(filters.shouldBeInReport(dbLicense)){
                licenseNames.add(dbLicense.getName());
            }
        }

        return licenseNames;
    }

    @Override
    public DbLicense getLicense(final String name) {
        final Jongo datastore = getJongoDataStore();
        return  datastore.getCollection(DbCollections.DB_LICENSES)
                .findOne(JongoUtils.generateQuery(DbLicense.NAME_DB_FIELD, name))
                .as(DbLicense.class);
    }

    @Override
    public void deleteLicense(final String name) {
        final DbLicense license = getLicense(name);

        if(license == null){
            throw new NotFoundException("The license does not exist: " + name);
        }
        else{
            final Jongo datastore = getJongoDataStore();
            datastore.getCollection(DbCollections.DB_LICENSES)
                    .remove(JongoUtils.generateQuery(DbLicense.NAME_DB_FIELD, name));
        }
    }

    @Override
    public List<DbArtifact> getArtifacts(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        final List<DbArtifact> artifacts = new ArrayList<DbArtifact>();

        final Iterable<DbArtifact> dbArtifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS)
                .find(JongoUtils.generateQuery(filters.getArtifactFieldsFilters()))
                .as(DbArtifact.class);

        for(DbArtifact dbArtifact: dbArtifacts){
            artifacts.add(dbArtifact);
        }
        return artifacts;
    }

    @Override
    public void addLicenseToArtifact(final DbArtifact artifact, final DbLicense license) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        if(!artifact.getLicenses().contains(license.getName())){
            artifact.addLicense(license);
            artifacts.update(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, artifact.getGavc()))
                    .with("{ $set: { \""+ DbArtifact.LICENCES_DB_FIELD + "\": #}} " , artifact.getLicenses());
        }

    }

    @Override
    public void removeLicenseFromArtifact(final DbArtifact artifact, final String licenseId) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        if(artifact.getLicenses().contains(licenseId)){
            artifact.removeLicense(licenseId);
            artifacts.update(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, artifact.getGavc()))
                    .with("{ $set: { \""+ DbArtifact.LICENCES_DB_FIELD + "\": #}} " , artifact.getLicenses());
        }

    }

    @Override
    public void approveLicense(final DbLicense license, final Boolean approved) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection licenses = datastore.getCollection(DbCollections.DB_LICENSES);

        licenses.update(JongoUtils.generateQuery(DbLicense.NAME_DB_FIELD, license.getName()))
                .with("{ $set: { \""+ DbLicense.APPROVED_DB_FIELD + "\": #}} " , approved);
    }

    @Override
    public void store(final DbArtifact artifact) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection dbArtifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);
        final DbArtifact dbArtifact = getArtifact(artifact.getGavc());

        if(dbArtifact == null){
            dbArtifacts.save(artifact);
        }
        else{

            // Important: all grapes clients do not send licenses information
            // if licenses are not attached to the artifact & the dbArtifact contains some
            // link between dbArtifact and licenses are preserved
            if(artifact.getLicenses().isEmpty()){
                artifact.setLicenses(dbArtifact.getLicenses());
            }

            dbArtifacts.update(dbArtifact.getId()).with(artifact);
        }
    }

    @Override
    public List<String> getGavcs(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ARTIFACTS).distinct(DbArtifact.GAV_DB_FIELD).as(String.class);
    }

    @Override
    public List<String> getGroupIds(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ARTIFACTS).distinct(DbArtifact.GROUPID_DB_FIELD).as(String.class);
    }

    @Override
    public List<String> getArtifactVersions(final DbArtifact artifact) {
        final Jongo datastore = getJongoDataStore();
        final Map<String,Object> params = new HashMap<String, Object>();
        params.put(DbArtifact.GROUPID_DB_FIELD, artifact.getGroupId());
        params.put(DbArtifact.ARTIFACTID_DB_FIELD, artifact.getArtifactId());
        params.put(DbArtifact.CLASSIFIER_DB_FIELD, artifact.getClassifier());
        params.put(DbArtifact.EXTENSION_DB_FIELD, artifact.getExtension());

        return datastore.getCollection(DbCollections.DB_ARTIFACTS).distinct(DbArtifact.VERSION_DB_FIELD).
                query(JongoUtils.generateQuery(params)).as(String.class);
    }

    @Override
    public DbArtifact getArtifact(final String gavc) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ARTIFACTS)
                .findOne(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, gavc))
                .as(DbArtifact.class);
    }

    @Override
    public void deleteArtifact(final String gavc) {
        final DbArtifact artifact = getArtifact(gavc);

        if(artifact == null){
            throw new NotFoundException("The artifact does not exist: " + gavc);
        }
        else{
            final Jongo datastore = getJongoDataStore();
            datastore.getCollection(DbCollections.DB_ARTIFACTS)
                    .remove(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, gavc));
        }
    }

    @Override
    public void updateDoNotUse(final DbArtifact artifact, final Boolean doNotUse) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        artifacts.update(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.DO_NOT_USE + "\": #}} " , doNotUse);
    }

    @Override
    public void updateDownloadUrl(final DbArtifact artifact, final String downLoadUrl) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        artifacts.update(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.DOWNLOAD_URL_DB_FIELD + "\": #}} " , downLoadUrl);
    }

    @Override
    public void updateProvider(final DbArtifact artifact, final String provider) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        artifacts.update(JongoUtils.generateQuery(DbArtifact.GAV_DB_FIELD, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.PROVIDER + "\": #}} " , provider);
    }

    @Override
    public List<DbModule> getAncestors(final String gavc, final FiltersHolder filters) {
        final DbArtifact artifact = getArtifact(gavc);

        if(artifact == null){
            throw new NotFoundException();
        }

        final Jongo datastore = getJongoDataStore();
        final Map<String, Object> queryParams = filters.getModuleFieldsFilters();
        queryParams.put(DbModule.USE_DB_FIELD, gavc);

        final Iterable<DbModule> results = datastore.getCollection(DbCollections.DB_MODULES)
                .find(JongoUtils.generateQuery(queryParams))
                .as(DbModule.class);

        final List<DbModule> ancestors = new ArrayList<DbModule>();
        for(DbModule ancestor: results){
            ancestors.add(ancestor);
        }

        return ancestors;
    }

    @Override
    public void store(final DbModule module) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection dbModules = datastore.getCollection(DbCollections.DB_MODULES);
        final DbModule dbModule = getModule(module.getUid());

        // has to be done due to mongo limitation: https://jira.mongodb.org/browse/SERVER-267
        module.updateHasAndUse();

        if(dbModule == null){
            dbModules.save(module);
        }
        else{
            dbModules.update(dbModule.getId()).with(module);
        }

    }

    @Override
    public List<String> getModuleNames(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_MODULES).distinct(DbModule.NAME_DB_FIELD).as(String.class);
    }

    @Override
    public List<String> getModuleVersions(final String name, final FiltersHolder filters) {
        final Map<String, Object> params = filters.getModuleFieldsFilters();
        params.put(DbModule.NAME_DB_FIELD, name);

        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_MODULES).distinct(DbModule.VERSION_DB_FIELD).
                    query(JongoUtils.generateQuery(params)).as(String.class);
    }

    @Override
    public DbModule getModule(final String moduleId) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_MODULES)
                .findOne(JongoUtils.generateQuery(DbModule.UID_DB_FIELD, moduleId))
                .as(DbModule.class);
    }

    @Override
    public List<DbModule> getModules(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        final List<DbModule> modules = new ArrayList<DbModule>();

        final Iterable<DbModule> dbModules = datastore.getCollection(DbCollections.DB_MODULES)
                .find(JongoUtils.generateQuery(filters.getModuleFieldsFilters()))
                .as(DbModule.class);

        for(DbModule dbModule: dbModules){
            modules.add(dbModule);
        }
        return modules;
    }


    @Override
    public void deleteModule(final String moduleId) {
        final DbModule module = getModule(moduleId);

        if(module == null){
            throw new NotFoundException("The module does not exist: " + moduleId);
        }
        else{
            final Jongo datastore = getJongoDataStore();
            datastore.getCollection(DbCollections.DB_MODULES)
                    .remove(JongoUtils.generateQuery(DbModule.UID_DB_FIELD, moduleId));
        }
    }

    @Override
    public void promoteModule(final DbModule module) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection modules = datastore.getCollection(DbCollections.DB_MODULES);

        modules.update(JongoUtils.generateQuery(DbModule.UID_DB_FIELD, module.getUid()))
                .with("{ $set: { \""+ DbModule.PROMOTION_DB_FIELD + "\": #}} " , Boolean.TRUE);
    }

    @Override
    public DbModule getRootModuleOf(final String gavc){
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_MODULES)
                .findOne(JongoUtils.generateQuery(DbModule.HAS_DB_FIELD, gavc))
                .as(DbModule.class);
    }

    @Override
    public DbModule getModuleOf(final String gavc) {
        final DbModule module = getRootModuleOf(gavc);

        // It may be a submodule...
        if(module != null && !module.getArtifacts().contains(gavc)){
            for(DbModule submodule: DataUtils.getAllSubmodules(module)){
                if(submodule.getArtifacts().contains(gavc)){
                    return submodule;
                }
            }
        }

        return module;
    }

    @Override
    public List<String> getCorporateGroupIds() {
        final Jongo datastore = getJongoDataStore();

        final DbCorporateGroupIds dbCorporateGroupIds = datastore.getCollection(DbCollections.DB_CORPORATE_GROUPIDS)
                .findOne()
                .as(DbCorporateGroupIds.class);

        if(dbCorporateGroupIds == null){
            return new ArrayList<String>();
        }

        return dbCorporateGroupIds.getCorporateGroupIds();
    }

    @Override
    public void addNewCorporateGroupId(final String corporateGroupId) {
        final Jongo datastore = getJongoDataStore();

        DbCorporateGroupIds dbCorporateGroupIds = datastore.getCollection(DbCollections.DB_CORPORATE_GROUPIDS)
                .findOne()
                .as(DbCorporateGroupIds.class);

        if(dbCorporateGroupIds == null){
            dbCorporateGroupIds = new DbCorporateGroupIds();
            dbCorporateGroupIds.addCorporateGroupId(corporateGroupId);
            datastore.getCollection(DbCollections.DB_CORPORATE_GROUPIDS).save(dbCorporateGroupIds);
        }
        else {
            dbCorporateGroupIds.addCorporateGroupId(corporateGroupId);
            datastore.getCollection(DbCollections.DB_CORPORATE_GROUPIDS).update(dbCorporateGroupIds.getId()).with(dbCorporateGroupIds);
        }
    }

    @Override
    public void removeCorporateGroupId(final String corporateGroupId) {
        final Jongo datastore = getJongoDataStore();

        DbCorporateGroupIds dbCorporateGroupIds = datastore.getCollection(DbCollections.DB_CORPORATE_GROUPIDS)
                .findOne()
                .as(DbCorporateGroupIds.class);

        if(dbCorporateGroupIds != null){
            dbCorporateGroupIds.removeCorporateGroupId(corporateGroupId);
            datastore.getCollection(DbCollections.DB_CORPORATE_GROUPIDS).update(dbCorporateGroupIds.getId()).with(dbCorporateGroupIds);
        }
    }

}
