package org.axway.grapes.server.core;

import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.core.options.filters.LicenseIdFilter;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
 * @author jdcoffre
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
     * Add or update a license to the database
     *
     * @param dbLicense DbLicense
     */
    public void store(final DbLicense dbLicense) {
        repoHandler.store(dbLicense);
    }

    /**
     * Return a list of license names. This list can either be serialized in HTML or in JSON
     *
     * @param filters FiltersHolder
     * @return List<String>
     */
    public List<String> getLicensesNames(final FiltersHolder filters) {
        return repoHandler.getLicenseNames(filters);
    }

    /**
     * Return a html view that contains the targeted license
     *
     * @param name String
     * @return DbLicense
     */
    public DbLicense getLicense(final String name) {
        final DbLicense license = repoHandler.getLicense(name);

        if(license == null){
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("License " + name + " does not exist.").build());
        }

        return license;
    }

    /**
     * Delete a license from the repository
     *
     * @param name
     */
    public void deleteLicense(final String name) {
        final DbLicense dbLicense = getLicense(name);

        repoHandler.deleteLicense(dbLicense.getName());

        final FiltersHolder filters = new FiltersHolder();
        final LicenseIdFilter licenseIdFilter = new LicenseIdFilter(name);
        filters.addFilter(licenseIdFilter);

        for(DbArtifact artifact: repoHandler.getArtifacts(filters)){
            repoHandler.removeLicenseFromArtifact(artifact, name);
        }

    }

    /**
     * Approve or reject a license
     *
     * @param name String
     * @param approved Boolean
     */
    public void approveLicense(final String name, final Boolean approved) {
        final DbLicense license = getLicense(name);
        repoHandler.approveLicense(license, approved);
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
        final ModelMapper modelMapper = new ModelMapper(repoHandler);
        final List<License> licenses = new ArrayList<License>();
        for(DbLicense dbLicense: licensesRegexp.values()){
            licenses.add(modelMapper.getLicense(dbLicense));
        }

        return licenses;
    }

}
