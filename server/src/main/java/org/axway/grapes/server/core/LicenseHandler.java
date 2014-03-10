package org.axway.grapes.server.core;

import org.axway.grapes.server.db.datamodel.DbLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Update the licenses references
     *
     * @param licenses
     */
    public void update(final List<DbLicense> licenses){
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

        LOG.error("No matching pattern for license " + licenseId);
        return null;

    }

}
