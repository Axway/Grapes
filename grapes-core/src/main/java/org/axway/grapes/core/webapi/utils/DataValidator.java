package org.axway.grapes.core.webapi.utils;

import org.axway.grapes.core.exceptions.DataValidationException;
import org.axway.grapes.core.handler.DataUtils;
import org.axway.grapes.core.webapi.resources.DependencyComplete;
import org.axway.grapes.core.webapi.resources.ModuleComplete;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private  static final Logger LOGGER = LoggerFactory.getLogger(DataValidator.class);

    private DataValidator(){
        // Hide utility class constructor
    }

    /**
     * Checks if the provided artifact is valid and could be stored into the database
     *
     * @param artifact the artifact to test
     * @throws  if the data is corrupted
     */
    public static void validate(final Artifact artifact) throws DataValidationException {
        if(artifact.getGroupId() == null ||
                artifact.getGroupId().isEmpty()){
          LOGGER.error("ERROR Artifact Groupid is missing");
            throw new DataValidationException("ERROR Artifact groupId should not be null or empty");
        }
        if(artifact.getArtifactId() == null ||
                artifact.getArtifactId().isEmpty()){
          LOGGER.error("ERROR Artifact artifactid is missing");
            throw new DataValidationException("ERROR Artifact artifactId should not be null or empty");
        }
        if(artifact.getVersion() == null ||
                artifact.getVersion().isEmpty()){
           LOGGER.error("ERROR Artifact version is missing");
            throw new DataValidationException("ERROR Artifact version should not be null or empty");
        }
    }

    /**
     * Checks if the provided license is valid and could be stored into the database
     *
     * @param license the license to test
     * @throws  if the data is corrupted
     */
    public static void validate(final License license) throws DataValidationException {
        // A license should have a name
        if(license.getName() == null ||
                license.getName().isEmpty()){
            LOGGER.error("ERROR License name is missing");
            throw new DataValidationException("ERROR License name is missing");
        }

        // A license should have a long name
        if(license.getLongName() == null ||
                license.getLongName().isEmpty()){
            LOGGER.error("ERROR License long name is missing");
            throw new DataValidationException("ERROR License long name is missing");

        }

        // If there is a regexp, it should compile
        if(license.getRegexp() != null &&
                !license.getRegexp().isEmpty()){
            try{
                Pattern.compile(license.getRegexp());
            }
            catch (PatternSyntaxException e){
            LOGGER.error("ERROR License Regexp does not compile");
                throw new DataValidationException("ERROR License regexp does not compile!");
            }
        }
    }

    /**
     * Checks if the provided module is valid and could be stored into the database
     *
     * @param module the module to test
     * @throws  if the data is corrupted
     */
    public static void validate(final ModuleComplete module) throws DataValidationException {
        if(module.getName() == null ||
                module.getName().isEmpty()){
          LOGGER.error("ERROR Module Name missing");
            throw new DataValidationException("ERROR Module name cannot be null or empty!");
        }
        if(module.getVersion()== null ||
                module.getVersion().isEmpty()){

            LOGGER.error("ERROR Module version missing");
            throw new DataValidationException("ERROR Module version cannot be null or empty!");
        }
        // Check artifacts
        //todo should be static?
        DataUtils dataUtils = new DataUtils();//added this line
       for(Artifact artifact: dataUtils.getAllArtifacts(module)){
           validate(artifact);

}


        // Check dependencies
        for(DependencyComplete dependency: dataUtils.getAllDependencies(module)){
            validate(dependency.getTarget());
        }
    }

    /**
     * Checks if the provided organization is valid and could be stored into the database
     *
     * @param organization Organization
     * @throws  if the data is corrupted
     */
    public static void validate(final Organization organization) throws DataValidationException {
        if(organization.getName() == null ||
                organization.getName().isEmpty()){

            LOGGER.error("ERROR organization name is missing");
            throw new DataValidationException("ERROR Organization name cannot be null or empty!");
        }
    }
}
