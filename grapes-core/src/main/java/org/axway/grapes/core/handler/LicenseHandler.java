package org.axway.grapes.core.handler;
/*todo adding licenses is complicated apprently they can have differnt names for the
*same thing need a way to keep track of them better when adding to artifacts*/

import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.options.filters.LicenseIdFilter;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.LicenseService;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.PatternSyntaxException;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class LicenseHandler implements LicenseService {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseHandler.class);

    private final Map<String, License> licensesRegexp = new HashMap<String, License>();

    @Model(value = License.class)
    private Crud<License, String> licenseStringCrud;
    @Model(value = Artifact.class)
    private Crud<Artifact, String> artifactCrud;

    @Requires
    private ArtifactService artifactService;



    /**
     * Add or update a license to the Database.
     *
     * @param license
     */
    @Override
    public void store(License license) {
        License license1 = licenseStringCrud.findOne(license.getName());
        if (license1 == null
                && license.getLongName().isEmpty()
                && license.getRegexp().isEmpty()
                && license.getUrl().isEmpty()) {

            license.setApproved(false);
            license.setUnknown(true);
        }

        licenseStringCrud.save(license);
    }

    public  void storeUnknown(String name){

        License license = new License();
        license.setName(name);
        license.setLongName("");
        license.setApproved(false);
        license.setComments("");
        license.setRegexp("");
        license.setUnknown(true);
        license.setUrl("");
        store(license);
    }

    @Override
    public License getLicense(String name) {
        final License license = licenseStringCrud.findOne(name);
        if (license == null) {
            throw new NoSuchElementException("License of name: " + name);
        }
        return license;
    }

    /**
     * Return a list of license names.
     *
     * @param filters
     * @return
     */
    @Override
    public List<String> getLicensesNames(final FiltersHolder filters) {
        final List<String> licenseNames = new ArrayList<String>();
        for (License license : getLicenses()) {
            if (filters.shouldBeInReport(license)) {
                licenseNames.add(license.getName());
            }
        }
        return licenseNames;
    }


   /* @Override
    public List<License> getAllLicenses() {
        Iterable<License> list = licenseStringCrud.findAll();
        return Lists.newArrayList(list);
    }*/

    /*@Override
    //todo not needed? then delete?
    public List<License> getAllLicenses(String name) {
        return null;
    }*/

    @Override
       public List<License> getLicenses() {
        Iterable<License> l = licenseStringCrud.findAll();
        return Lists.newArrayList(l);
    }

    /**
     *
     * @param name
     */
    @Override
    public void deleteLicense(String name) {
        License license = getLicense(name);
        licenseStringCrud.delete(license.getName());
        final FiltersHolder filters = new FiltersHolder();
        final LicenseIdFilter licenseIdFilter = new LicenseIdFilter(name);
        filters.addFilter(licenseIdFilter);
        //remove the license from all artifacts.
        for(Artifact artifact:artifactService.getArtifacts(filters)){
           artifactService.removeLicenseFromArtifact(artifact.getGavc(),name);
        }


    }

    /**
     * @param name
     * @param approved
     */
    @Override
    public void approveLicense(String name, Boolean approved) {
        final License license = getLicense(name);
        license.setApproved(approved);
        licenseStringCrud.save(license);
    }

    /**
     * @param licenseName
     * @return
     */
    @Override
    public License resolve(String licenseName) {
        init(getLicenses());//todo see method todo


//this should be more effcient then the other version below.
    /*    for(Map.Entry<String, License> regexp : licensesRegexp.entrySet()){
            try {
                if (licenseName.matches(regexp.getKey())) {

                    return regexp.getValue();
                }
            } catch (PatternSyntaxException e) {
                LOG.error("Wrong pattern for the following license " + licensesRegexp.get(regexp).getName());
                continue;
            }

        }*/
        for (String regexp : licensesRegexp.keySet()) {
            try {
                if (licenseName.matches(regexp)) {

                    return licensesRegexp.get(regexp);
                }
            } catch (PatternSyntaxException e) {
                LOG.error("Wrong pattern for the following license " + licensesRegexp.get(regexp).getName());
                continue;
            }
        }
        LOG.warn("No matching pattern for license " + licenseName);
        return null;
    }

    /**
     * TODO snchro? static? distributed?
     * Todo Should be called inside store and delete?
     * Init the licenses cache.
     *
     * @param licenses
     */
    private void init(final List<License> licenses) {
        licensesRegexp.clear();
        for (License license : licenses) {
            if (license.getRegexp() == null ||
                    license.getRegexp().isEmpty()) {
                licensesRegexp.put(license.getName(), license);
            } else {
                licensesRegexp.put(license.getRegexp(), license);
            }
        }
    }
}
