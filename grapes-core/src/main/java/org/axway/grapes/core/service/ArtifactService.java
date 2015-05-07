package org.axway.grapes.core.service;


import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;

import java.util.List;

/**
 * Created by jennifer on 4/24/15.
 */
public interface ArtifactService {
    void store(Artifact artifact);

    void storeIfNew(Artifact artifact);

    void addLicense(String gavc, String licenseId);

   List<String> getArtifactGavcs(FiltersHolder filters);

    List<String> getArtifactGroupIds(FiltersHolder filters);

    List<String> getArtifactVersions(String gavc);

    String getArtifactLastVersion(String gavc);

    Artifact getArtifact(String gavc);

    Module getModule(Artifact artifact);

    Organization getOrganization(Artifact artifact);

    void updateDownLoadUrl(String gavc, String downLoadUrl);

    void updateProvider(String gavc, String provider);

    void deleteArtifact(String gavc);

    void updateDoNotUse(String gavc, Boolean doNotUse);

    List<Module> getAncestors(String gavc, FiltersHolder filters);

   List<License> getArtifactLicenses(String gavc, FiltersHolder filters);

    void addLicenseToArtifact(String gavc, String licenseId);

    void removeLicenseFromArtifact(String gavc, String licenseId);

    List<Artifact> getArtifacts(FiltersHolder filters);
    List<String> getArtifactVersions(Artifact artifact);

    List<String> getGavcs(FiltersHolder filters);
}
