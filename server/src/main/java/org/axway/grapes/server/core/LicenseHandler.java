package org.axway.grapes.server.core;

import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * License Handler
 *
 * <p>Handles the license resolution. It stores the licenses names and the regexp to avoid db access. It must be updated at license addition / deletion.</p>
 *
 *
 */
public class LicenseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseHandler.class);

    private final Map<String, DbLicense> licensesRegexp = new HashMap<String, DbLicense>();
    private final RepositoryHandler repoHandler;

    public LicenseHandler(final RepositoryHandler repoHandler) {
        this.repoHandler = repoHandler;
        init(repoHandler.getAllLicenses());
    }

    /**
     * Init the licenses cache
     *
     * @param licenses
     */
    private void init(final List<DbLicense> licenses){
        licensesRegexp.clear();

        for(DbLicense license: licenses){
            if(license.getRegexp() == null ||
                    license.getRegexp().isEmpty()){
                licensesRegexp.put(license.getName(), license);
            }
            else{
                licensesRegexp.put(license.getRegexp(), license);
            }
        }
    }


    /**
     * Resolve the targeted license thanks to the license ID
     * Return null if no license is matching the licenseId
     *
     * @param licenseId
     * @return DbLicense
     */
    public DbLicense resolve(final String licenseId){

        for(String regexp : licensesRegexp.keySet()){
            try{
                if(licenseId.matches(regexp)){
                    return licensesRegexp.get(regexp);
                }
            }
            catch (PatternSyntaxException e){
                LOG.error("Wrong pattern for the following license " + licensesRegexp.get(regexp).getName());
                continue;
            }
        }

        LOG.warn("No matching pattern for license " + licenseId);
        return null;

    }


    /**
     * Returns all the available license in client/server data model
     *
     * @return List<License>
     */
    public List<License> getLicenses(){
        final List<License> licenses = new ArrayList<License>();
        for(DbLicense dbLicense: licensesRegexp.values()){
            licenses.add(DataUtils.getLicense(dbLicense));
        }

        return licenses;
    }

    /**
     * Update in database the licenses of an artifact
     *
     * @param gavc String
     * @param newLicenses List<String>
     */
    public void updateArtifact(final String gavc, final List<String> newLicenses) {
        final DbArtifact artifact = repoHandler.getArtifact(gavc);

        if(artifact == null){
            return;
        }

        for(String newLicense: newLicenses){
            // Try to find an existing license that match the new one
            final DbLicense license = resolve(newLicense);

            // If there is no existing license that match this one let's use the provided value but
            // only if the artifact has no license  yet. Otherwise it could mean that users has already
            // identify the license manually.
            if(license == null){
                if(artifact.getLicenses().isEmpty()){
                    LOG.warn("Add reference to a non existing license called " + newLicense + " in  artifact " + gavc);
                    repoHandler.addLicenseToArtifact(artifact, newLicense);
                }
            }
            // Add only if the license is not already referenced
            else if(!artifact.getLicenses().contains(license.getName())){
                    repoHandler.addLicenseToArtifact(artifact, license.getName());
            }
        }
    }
}
