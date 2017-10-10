package org.axway.grapes.server.webapp;


import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.reports.ReportsHandler;
import org.axway.grapes.server.reports.ReportsRegistry;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Data Validator
 *
 * <p>Validates the posted mime to make sure that no corrupted data is sent to Grapes.</p>
 *
 * @author jdcoffre
 */
public final class DataValidator {

    private DataValidator(){
        // Hide utility class constructor
    }

    private static final Logger LOG = LoggerFactory.getLogger(DataValidator.class);

    /**
     * Checks if the provided artifact is valid and could be stored into the database
     *
     * @param artifact the artifact to test
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validate(final Artifact artifact) {
        if((artifact.getOrigin()== null || "maven".equals(artifact.getOrigin()))
                && (artifact.getGroupId() == null || artifact.getGroupId().isEmpty())){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artifact groupId should not be null or empty")
                    .build());
        }
        if(artifact.getArtifactId() == null ||
                artifact.getArtifactId().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artifact artifactId should not be null or empty")
                    .build());
        }
        if(artifact.getVersion() == null ||
                artifact.getVersion().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artifact version should not be null or empty")
                    .build());
        }
    }

    /**
     * Checks if the provided artifact is valid and could be stored into the database
     *
     * @param artifact the artifact to test
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validatePostArtifact(final Artifact artifact) {
    	validate(artifact);
    	
        if(artifact.getExtension() == null ||
                artifact.getExtension().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artifact extension should not be null or empty")
                    .build());
        }
        if(artifact.getSha256() == null ||
                artifact.getSha256().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artifact SHA256 checksum should not be null or empty")
                    .build());
        }
        if(artifact.getSha256().length() != 64){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artifact SHA256 checksum length should be 64")
                    .build());
        }
    }


    /**
     * Checks if the provided license is valid and could be stored into the database
     *
     * @param license the license to test
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validate(final License license) {
        // A license should have a name
        if(license.getName() == null ||
                license.getName().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("License name should not be empty!")
                    .build());
        }

        // A license should have a long name
        if(license.getLongName() == null ||
                license.getLongName().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("License long name should not be empty!")
                    .build());
        }

        // If there is a regexp, it should compile
        if(license.getRegexp() != null &&
                !license.getRegexp().isEmpty()){
            try{
                Pattern.compile(license.getRegexp());
            }
            catch (PatternSyntaxException e){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("License regexp does not compile!").build());
            }

            Pattern regex = Pattern.compile("[&%//]");
            if(regex.matcher(license.getRegexp()).find()){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("License regexp does not compile!").build());
            }

        }
    }

    /**
     * Checks if the provided module is valid and could be stored into the database
     *
     * @param module the module to test
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validate(final Module module) {
        if (null == module) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                .entity("Module cannot be null!")
                .build());
        }
        if(module.getName() == null ||
                module.getName().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Module name cannot be null or empty!")
                    .build());
        }
        if(module.getVersion()== null ||
                module.getVersion().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Module version cannot be null or empty!")
                    .build());
        }

        // Check artifacts
        for(final Artifact artifact: DataUtils.getAllArtifacts(module)){
            validate(artifact);
        }

        // Check dependencies
        for(final Dependency dependency: DataUtils.getAllDependencies(module)){
            validate(dependency.getTarget());
        }
    }

    /**
     * Checks if the provided organization is valid and could be stored into the database
     *
     * @param organization Organization
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validate(final Organization organization) {
        if(organization.getName() == null ||
                organization.getName().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Organization name cannot be null or empty!")
                    .build());
        }
    }

    /**
     * Checks if the provided artifactQuery is valid
     *
     * @param artifactQuery ArtifactQuery
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validate(final ArtifactQuery artifactQuery) {
        final Pattern invalidChars = Pattern.compile("[^A-Fa-f0-9]");
        if(artifactQuery.getUser() == null ||
        		artifactQuery.getUser().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mandatory field [user] missing")
                    .build());
        }
        if( artifactQuery.getStage() != 0 && artifactQuery.getStage() !=1 ){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid [stage] value (supported 0 | 1)")
                    .build());
        }
        if(artifactQuery.getName() == null ||
        		artifactQuery.getName().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mandatory field [name] missing, it should be the file name")
                    .build());
        }
        if(artifactQuery.getSha256() == null ||
                artifactQuery.getSha256().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mandatory field [sha256] missing")
                    .build());
        }
        if(artifactQuery.getSha256().length() < 64 || invalidChars.matcher(artifactQuery.getSha256()).find()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid file checksum value")
                    .build());
        }
        if(artifactQuery.getType() == null ||
        		artifactQuery.getType().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Mandatory field [type] missing")
                    .build());
        }
    }

    /**
     * Check if new license pattern is valid and doesn't match any existing one
     * @param license
     * @param licenseHandler
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validateLicensePattern(License license, LicenseHandler licenseHandler){

        if(license.getRegexp() == null || license.getRegexp().isEmpty()) return;
        DbLicense dbLicense = null;

        try{
           dbLicense = licenseHandler.getLicense(license.getName());
        }catch (Exception e){
            //No license found
        }

        if(dbLicense == null || !license.getRegexp().equals(dbLicense.getRegexp())) {
            int reportId = ReportId.MULTIPLE_LICENSE_MATCHING_STRINGS.getId();
            final Optional<Report> reportOp = ReportsRegistry.findById(reportId);
            if (reportOp.isPresent()) {
                final Report reportDef = reportOp.get();
                ReportRequest reportRequest = new ReportRequest();
                reportRequest.setReportId(reportId);

                DbLicense addedLicense = new DbLicense();
                addedLicense.setRegexp(license.getRegexp());
                addedLicense.setName(license.getName());
                addedLicense.setLongName(license.getLongName());

                List<DbLicense> copy = new ArrayList<>(licenseHandler.allLicenses());
                copy.add(addedLicense);

                ReportExecution reportExecution = new ReportsHandler(new RepositoryHandler() {
                    @Override
                    public void store(DbCredential credential) {

                    }

                    @Override
                    public DbCredential getCredential(String userId) {
                        return null;
                    }

                    @Override
                    public void addUserRole(String user, DbCredential.AvailableRoles role) {

                    }

                    @Override
                    public void removeUserRole(String user, DbCredential.AvailableRoles role) {

                    }

                    @Override
                    public void store(DbLicense license) {

                    }

                    @Override
                    public List<String> getLicenseNames(FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public DbLicense getLicense(String name) {
                        return null;
                    }

                    @Override
                    public List<DbLicense> getAllLicenses() {
                        return copy;
                    }

                    @Override
                    public void deleteLicense(String name) {

                    }

                    @Override
                    public List<DbArtifact> getArtifacts(FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public void addLicenseToArtifact(DbArtifact artifact, String licenseId) {

                    }

                    @Override
                    public void removeLicenseFromArtifact(DbArtifact artifact, String name, LicenseMatcher licenseMatcher) {

                    }

                    @Override
                    public void approveLicense(DbLicense license, Boolean approved) {

                    }

                    @Override
                    public void store(DbArtifact dbArtifact) {

                    }

                    @Override
                    public List<String> getGavcs(FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public List<String> getGroupIds(FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public List<String> getArtifactVersions(DbArtifact artifact) {
                        return null;
                    }

                    @Override
                    public DbArtifact getArtifact(String gavc) {
                        return null;
                    }

                    @Override
                    public DbArtifact getArtifactUsingSHA256(String sha256) {
                        return null;
                    }

                    @Override
                    public void deleteArtifact(String gavc) {

                    }

                    @Override
                    public void updateDoNotUse(DbArtifact artifact, Boolean doNotUse) {

                    }

                    @Override
                    public void updateDownloadUrl(DbArtifact artifact, String downLoadUrl) {

                    }

                    @Override
                    public void updateProvider(DbArtifact artifact, String provider) {

                    }

                    @Override
                    public List<DbModule> getAncestors(DbArtifact artifact, FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public void store(DbModule dbModule) {

                    }

                    @Override
                    public List<String> getModuleNames(FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public List<String> getModuleVersions(String name, FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public DbModule getModule(String moduleId) {
                        return null;
                    }

                    @Override
                    public List<DbModule> getModules(FiltersHolder filters) {
                        return null;
                    }

                    @Override
                    public void deleteModule(String moduleId) {

                    }

                    @Override
                    public void promoteModule(DbModule module) {

                    }

                    @Override
                    public DbModule getRootModuleOf(String gavc) {
                        return null;
                    }

                    @Override
                    public DbModule getModuleOf(String gavc) {
                        return null;
                    }

                    @Override
                    public List<String> getOrganizationNames() {
                        return null;
                    }

                    @Override
                    public DbOrganization getOrganization(String name) {
                        return null;
                    }

                    @Override
                    public void deleteOrganization(String organizationId) {

                    }

                    @Override
                    public void store(DbOrganization organization) {

                    }

                    @Override
                    public void addModulesOrganization(String corporateGroupId, DbOrganization dbOrganization) {

                    }

                    @Override
                    public void removeModulesOrganization(String corporateGroupId, DbOrganization dbOrganization) {

                    }

                    @Override
                    public void removeModulesOrganization(DbOrganization dbOrganization) {

                    }

                    @Override
                    public List<DbOrganization> getAllOrganizations() {
                        return null;
                    }

                    @Override
                    public void store(DbProduct dbProduct) {

                    }

                    @Override
                    public DbProduct getProduct(String name) {
                        return null;
                    }

                    @Override
                    public List<String> getProductNames() {
                        return null;
                    }

                    @Override
                    public void deleteProduct(String name) {

                    }

                    @Override
                    public <T> Optional<T> getOneByQuery(String collectionName, String query, Class<T> c) {
                        return null;
                    }

                    @Override
                    public <T> List<T> getListByQuery(String collectionName, String query, Class<T> c) {
                        return new ArrayList<>();
                    }

                    @Override
                    public <T> void consumeByQuery(String collectionName, String query, Class<T> c, Consumer<T> consumer) {
                        licenseHandler.consumeByQuery(collectionName, query, c, consumer);
                    }

                    @Override
                    public long getResultCount(String collectionName, String query) {
                        return 0;
                    }

                    @Override
                    public void store(DbComment dbComment) {

                    }

                    @Override
                    public List<DbComment> getComments(String entityId, String entityType) {
                        return null;
                    }

                    @Override
                    public DbComment getLatestComment(String entityId, String entityType) {
                        return null;
                    }

                    @Override
                    public DbSearch getSearchResult(String search, FiltersHolder filter) {
                        return null;
                    }
                }).execute(reportDef, reportRequest);
                List<String[]> data = reportExecution.getData();
                data.forEach(strings -> {
                    boolean match = strings[2].contains(addedLicense.getName());
                    if (match) {
                        LOG.info("Pattern conflict: " + strings[1] + " matching " + strings[2]);
                        String message = strings[2].replace(addedLicense.getName(), "");
                        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                .entity("Pattern conflict matching: " +message)
                                .build());
                    }
                });
            }
        }
    }

}
