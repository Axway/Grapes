package org.axway.grapes.core.service;

import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.model.datamodel.License;

import java.util.List;

/**
 * Created by jennifer on 4/28/15.
 */
public interface LicenseService {

    public void hello();
    void store(License license);

    List<String> getLicensesNames(FiltersHolder filters);

    License getLicense(String name);

    void deleteLicense(String name);

    void approveLicense(String name, Boolean approved);

    License resolve(String licenseId);

    List<License> getLicenses();

}
