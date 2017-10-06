package org.axway.grapes.server.core.interfaces;

import org.axway.grapes.server.db.datamodel.DbLicense;

import java.util.Set;

public interface LicenseMatcher {

    /**
     * Gets the matching licenses by the regular expression of the license entities.
     * The method will search in all the existent <CODE>DbLicense</CODE> documents
     * and use their respective regular expressions to match the argument string.
     * @param licenseString The license string to match
     * @return The list of licenses which are matched by their
     * regular expression.
     */
    Set<DbLicense> getMatchingLicenses(final String licenseString);
}
