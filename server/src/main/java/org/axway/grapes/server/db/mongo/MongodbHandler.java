package org.axway.grapes.server.db.mongo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
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
import java.util.*;
import java.util.regex.Pattern;

/**
 * Mongodb Handler
 *
 * <p>Repository Handler designed for mongodb<p/>
 *
 * @author jdcoffre
 */
public class MongodbHandler implements RepositoryHandler {
    // cache for credentials
    private LoadingCache<String, DbCredential> credentialCache;
    // DB connection
    private final DB db;

    public MongodbHandler(final DataBaseConfig config) throws UnknownHostException {
        final ServerAddress address = new ServerAddress(config.getHost() , config.getPort());
        final MongoClient mongo = new MongoClient(address);
        db = mongo.getDB(config.getDatastore());

        if(config.getUser() != null && config.getPwd() != null){
            db.authenticate(config.getUser(), config.getPwd());
        }

        // Init credentials' cache
        credentialCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<String, DbCredential>() {
                            public DbCredential load(String user) {
                                return getCredential(user);
                            }
                        });
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
            dbCredentials.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, dbCredential.getUser())).with(credential);
        }

        credentialCache.invalidate(credential.getUser());
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
            credentials.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, user))
                    .with("{ $set: { \""+ DbCredential.ROLES_FIELD + "\": #}} " , credential.getRoles());
        }

        credentialCache.invalidate(credential.getUser());

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
            credentials.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, user))
                    .with("{ $set: { \""+ DbCredential.ROLES_FIELD + "\": #}} " , credential.getRoles());
        }
        credentialCache.invalidate(credential.getUser());
    }

    @Override
    public DbCredential getCredential(final String user) {
		final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_CREDENTIALS)
				.findOne(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, user))
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
            dbLicenses.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, dbLicense.getName())).with(license);
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
    public List<DbLicense> getAllLicenses() {
        final Jongo datastore = getJongoDataStore();
        final Iterator<DbLicense> licenses = datastore.getCollection(DbCollections.DB_LICENSES)
                .find().as(DbLicense.class).iterator();

        return Lists.newArrayList(licenses);
    }

    @Override
    public DbLicense getLicense(final String name) {
        final Jongo datastore = getJongoDataStore();
        return  datastore.getCollection(DbCollections.DB_LICENSES)
                .findOne(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, name))
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
                    .remove(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, name));
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
    public void addLicenseToArtifact(final DbArtifact artifact, final String licenseId) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);
        artifact.addLicense(licenseId);
        artifacts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.LICENCES_DB_FIELD + "\": #}} " , artifact.getLicenses());

    }

    @Override
    public void removeLicenseFromArtifact(final DbArtifact artifact, final String licenseId) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        if(artifact.getLicenses().contains(licenseId)){
            artifact.removeLicense(licenseId);
            artifacts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, artifact.getGavc()))
                    .with("{ $set: { \""+ DbArtifact.LICENCES_DB_FIELD + "\": #}} " , artifact.getLicenses());
        }

    }

    @Override
    public void approveLicense(final DbLicense license, final Boolean approved) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection licenses = datastore.getCollection(DbCollections.DB_LICENSES);

        licenses.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, license.getName()))
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

            // Important: merge existing license and new ones :
            //    * because an existing license could have been manually enforce by a user
            //    * because all Grapes clients are not to send license information
            for(String license: dbArtifact.getLicenses()){
                artifact.addLicense(license);
            }

            dbArtifacts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, dbArtifact.getGavc())).with(artifact);
        }
    }

    @Override
    public List<String> getGavcs(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ARTIFACTS).distinct(DbCollections.DEFAULT_ID)
                .query(JongoUtils.generateQuery(filters.getArtifactFieldsFilters())).as(String.class);
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
                .findOne(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, gavc))
                .as(DbArtifact.class);
    }

    @Override
    public DbArtifact getArtifactUsingSHA256(final String sha256) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ARTIFACTS)
                .findOne(JongoUtils.generateQuery(DbArtifact.SHA_256, sha256))
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
                    .remove(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, gavc));
        }
    }

    @Override
    public void updateDoNotUse(final DbArtifact artifact, final Boolean doNotUse) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        artifacts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.DO_NOT_USE + "\": #}} " , doNotUse);
    }

    @Override
    public void updateDownloadUrl(final DbArtifact artifact, final String downLoadUrl) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        artifacts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.DOWNLOAD_URL_DB_FIELD + "\": #}} " , downLoadUrl);
    }

    @Override
    public void updateProvider(final DbArtifact artifact, final String provider) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection artifacts = datastore.getCollection(DbCollections.DB_ARTIFACTS);

        artifacts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, artifact.getGavc()))
                .with("{ $set: { \""+ DbArtifact.PROVIDER + "\": #}} " , provider);
    }

    @Override
    public List<DbModule> getAncestors(final DbArtifact artifact, final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        final Map<String, Object> queryParams = filters.getModuleFieldsFilters();
        queryParams.put(DbModule.USE_DB_FIELD, artifact.getGavc());

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
        final DbModule dbModule = getModule(module.getId());

        // has to be done due to mongo limitation: https://jira.mongodb.org/browse/SERVER-267
        module.updateHasAndUse();

        if(dbModule == null){
            dbModules.save(module);
        }
        else{
            // let's keep the old build info and override with new values if any
            final Map<String,String> consolidatedBuildInfo = dbModule.getBuildInfo();
            consolidatedBuildInfo.putAll(module.getBuildInfo());
            module.setBuildInfo(consolidatedBuildInfo);

            dbModules.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, dbModule.getId())).with(module);
        }

    }

    @Override
    public List<String> getModuleNames(final FiltersHolder filters) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_MODULES)
                .distinct(DbModule.NAME_DB_FIELD)
                .query(JongoUtils.generateQuery(filters.getModuleFieldsFilters()))
                .as(String.class);
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
                .findOne(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, moduleId))
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
                    .remove(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, moduleId));
        }
    }

    @Override
    public void promoteModule(final DbModule module) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection modules = datastore.getCollection(DbCollections.DB_MODULES);

        modules.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, module.getId()))
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
    public List<String> getOrganizationNames() {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ORGANIZATION).distinct(DbCollections.DEFAULT_ID).as(String.class);
    }

    @Override
    public DbOrganization getOrganization(String name) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_ORGANIZATION)
                .findOne(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, name))
                .as(DbOrganization.class);
    }

    @Override
    public void deleteOrganization(String organizationId) {
        final Jongo datastore = getJongoDataStore();
        datastore.getCollection(DbCollections.DB_ORGANIZATION)
                .remove(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, organizationId));
    }

    @Override
    public void store(DbOrganization organization) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection dbOrganizations = datastore.getCollection(DbCollections.DB_ORGANIZATION);

        if(getOrganization(organization.getName()) == null){
            dbOrganizations.save(organization);
        }
        else{
            dbOrganizations.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, organization.getName())).with(organization);
        }
    }

    @Override
    public void addModulesOrganization(final String corporateGidPrefix, final DbOrganization organization){
        final Jongo datastore = getJongoDataStore();

        datastore.getCollection(DbCollections.DB_MODULES)
                .update("{ "+DbModule.HAS_DB_FIELD+" :#}", Pattern.compile(corporateGidPrefix + "*"))
                .multi()
                .with("{$set: " + JongoUtils.generateQuery(DbModule.ORGANIZATION_DB_FIELD, organization.getName()) + "}");
    }

    @Override
    public void removeModulesOrganization(final String corporateGidPrefix, final DbOrganization organization){
        final Jongo datastore = getJongoDataStore();

        datastore.getCollection(DbCollections.DB_MODULES)
                .update("{ $and: [" +
                        "{ " + DbModule.HAS_DB_FIELD + " :#} ," +
                        JongoUtils.generateQuery(DbModule.ORGANIZATION_DB_FIELD, organization.getName()) + "]}"
                        , Pattern.compile(corporateGidPrefix + "*"))
                .multi()
                .with("{$set: { " + DbModule.ORGANIZATION_DB_FIELD + " : \"\"}}");
    }

    @Override
    public void removeModulesOrganization(final DbOrganization organization){
        final Jongo datastore = getJongoDataStore();
        datastore.getCollection(DbCollections.DB_MODULES)
                .update(JongoUtils.generateQuery(DbModule.ORGANIZATION_DB_FIELD, organization.getName()))
                .with("{$set: { "+DbModule.ORGANIZATION_DB_FIELD+" : \"\"}}");
    }

    @Override
    public List<DbOrganization> getAllOrganizations() {
        final Jongo datastore = getJongoDataStore();
        final Iterable<DbOrganization> organizations = datastore
                .getCollection(DbCollections.DB_ORGANIZATION).find().as(DbOrganization.class);

        return Lists.newArrayList(organizations);
    }

    @Override
    public void store(final DbProduct dbProduct) {
        final Jongo datastore = getJongoDataStore();
        final MongoCollection dbProducts = datastore.getCollection(DbCollections.DB_PRODUCT);

        if(getOrganization(dbProduct.getName()) == null){
            dbProducts.save(dbProduct);
        }
        else {
            dbProducts.update(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, dbProduct.getName())).with(dbProduct);
        }
    }

    @Override
    public DbProduct getProduct(final String name) {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_PRODUCT)
                .findOne(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, name))
                .as(DbProduct.class);
    }

    @Override
    public List<String> getProductNames() {
        final Jongo datastore = getJongoDataStore();
        return datastore.getCollection(DbCollections.DB_PRODUCT).distinct(DbCollections.DEFAULT_ID).as(String.class);
    }

    @Override
    public void deleteProduct(String name) {
        final Jongo datastore = getJongoDataStore();
        datastore.getCollection(DbCollections.DB_PRODUCT)
                .remove(JongoUtils.generateQuery(DbCollections.DEFAULT_ID, name));
    }
}
