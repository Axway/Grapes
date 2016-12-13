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
        if((artifact.getOrigin()== null || artifact.getOrigin() == "maven")
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
                        .entity("License regexp does not compile!")
                        .build());
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
        for(Artifact artifact: DataUtils.getAllArtifacts(module)){
            validate(artifact);
        }

        // Check dependencies
        for(Dependency dependency: DataUtils.getAllDependencies(module)){
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
        if(artifactQuery.getUser() == null ||
        		artifactQuery.getUser().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("User name cannot be null or empty!")
                    .build());
        }
        if(artifactQuery.getName() == null ||
        		artifactQuery.getName().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("File Name cannot be null or empty!")
                    .build());
        }
        if(artifactQuery.getSha256() == null ||
        		artifactQuery.getSha256().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("SHA256 code cannot be null or empty!")
                    .build());
        }
        if(artifactQuery.getType() == null ||
        		artifactQuery.getType().isEmpty()){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("File Type code cannot be null or empty!")
                    .build());
        }
    }
}
