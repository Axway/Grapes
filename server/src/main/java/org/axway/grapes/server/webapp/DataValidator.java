package org.axway.grapes.server.webapp;


import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.DataUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
                        .entity("License regexp does not compile! " + e).build());
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
}
