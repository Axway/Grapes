package org.axway.grapes.server.core;

import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.server.core.interfaces.LicenseMatcher;
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
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * License Handler
 * <p>
 * <p>Handles the license resolution. It stores the licenses names and the regexp to avoid db access. It must be updated at license addition / deletion.</p>
 *
 * @author jdcoffre
 */
public class LicenseHandler implements LicenseMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseHandler.class);

    private final Map<String, DbLicense> licensesRegexp = new HashMap<>();
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
    private void init(final List<DbLicense> licenses) {
        licensesRegexp.clear();

        for (final DbLicense license : licenses) {
            if (license.getRegexp() == null ||
                    license.getRegexp().isEmpty()) {
                licensesRegexp.put(license.getName(), license);
            } else {
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

        if (license == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("License " + name + " does not exist.").build());
        }

        return license;
    }

    /**
     * Delete a license from the repository
     *
     * @param licName The name of the license to remove
     */
    public void deleteLicense(final String licName) {
        final DbLicense dbLicense = getLicense(licName);

        repoHandler.deleteLicense(dbLicense.getName());

        final FiltersHolder filters = new FiltersHolder();
        final LicenseIdFilter licenseIdFilter = new LicenseIdFilter(licName);
        filters.addFilter(licenseIdFilter);

        for (final DbArtifact artifact : repoHandler.getArtifacts(filters)) {
            repoHandler.removeLicenseFromArtifact(artifact, licName, this);
        }
    }

    /**
     * Approve or reject a license
     *
     * @param name     String
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
    public DbLicense resolve(final String licenseId) {

        for (final Entry<String, DbLicense> regexp : licensesRegexp.entrySet()) {
            try {
                if (licenseId.matches(regexp.getKey())) {
                    return regexp.getValue();
                }
            } catch (PatternSyntaxException e) {
                LOG.error("Wrong pattern for the following license " + regexp.getValue().getName(), e);
                continue;
            }
        }

        if(LOG.isWarnEnabled()) {
            LOG.warn(String.format("No matching pattern for license %s", licenseId));
        }
        return null;
    }

    /**
     * Returns all the available license in client/server data model
     *
     * @return List<License>
     */
    public List<License> getLicenses() {
        final ModelMapper modelMapper = new ModelMapper(repoHandler);
        final List<License> licenses = new ArrayList<>();
        for (final DbLicense dbLicense : licensesRegexp.values()) {
            licenses.add(modelMapper.getLicense(dbLicense));
        }

        return licenses;
    }

    /**
     * Turns a series of strings into their corresponding license entities
     * by using regular expressions
     *
     * @param licStrings The list of license strings
     * @return A set of license entities
     */
    public Set<DbLicense> resolveLicenses(List<String> licStrings) {
        Set<DbLicense> result = new HashSet<>();

        licStrings
                .stream()
                .map(this::getMatchingLicenses)
                .forEach(result::addAll);

        return result;
    }

    @Override
    public Set<DbLicense> getMatchingLicenses(String licenseString) {
        final List<DbLicense> allLicenses = repoHandler.getAllLicenses();
        return allLicenses
                .stream()
                .filter(license ->
                        licenseString.equalsIgnoreCase(license.getName()) ||
                                (
                                        !license.getRegexp().isEmpty() &&
                                                licenseString.matches(String.format("(?i:%s)", license.getRegexp()))
                                )
                )
                .collect(Collectors.toSet());
    }
}
