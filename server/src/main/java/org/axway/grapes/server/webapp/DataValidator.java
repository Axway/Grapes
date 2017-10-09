package org.axway.grapes.server.webapp;


import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.reports.ReportsHandler;
import org.axway.grapes.server.reports.ReportsRegistry;
import org.axway.grapes.server.reports.impl.MultipleMatchingReport;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.webapp.resources.LicenseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;
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
     * Check if license pattern is ok
     * @param license
     * @param licenseHandler
     * @throws WebApplicationException if the data is corrupted
     */
    public static void validateLicensePattern(License license, LicenseHandler licenseHandler, ReportsHandler reportsHandler){

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
                Map<String, String> jsonParams = new HashMap<>();
                jsonParams.put("organization", "Axway");
                jsonParams.put("new_value", license.getRegexp());
                reportRequest.setParamValues(jsonParams);
                ReportExecution reportExecution = reportsHandler.execute(reportDef, reportRequest);
                List<String[]> data = reportExecution.getData();
                data.forEach(strings -> LOG.info(strings[0] + " " + strings[1]));
                if(!data.isEmpty() && !data.get(0)[0].contains("All OK")) {
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                            .entity("Pattern conflict with other licenses")
                            .build());
                }
            }
        }
    }
}
