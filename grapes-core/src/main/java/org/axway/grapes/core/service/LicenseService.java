package org.axway.grapes.core.service;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.model.datamodel.License;

import java.util.List;

/**
 * Created by jennifer on 4/28/15.
 */
public interface LicenseService {

    public void store(License license);
    public  void storeUnknown(String name);

    public License getLicense(String name);

    public List<String> getLicensesNames(FiltersHolder filters);

   // public List<License> getAllLicenses();

   // public List<License> getAllLicenses(String name);

    public List<License> getLicenses();

    public void deleteLicense(String name);

    public void approveLicense(String name, Boolean approved);

    public License resolve(String licenseId);
}
