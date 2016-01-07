package org.axway.grapes.core.handler;

import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.options.FiltersHolder;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.ModuleService;
import org.axway.grapes.core.service.VersionsService;
import org.axway.grapes.core.webapi.utils.JongoUtils;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;
import org.wisdom.jongo.service.MongoFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class ArtifactHandler implements ArtifactService {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactHandler.class);
    @Requires
    VersionsService versionsService;
    @Requires
    ModuleService moduleService;
    @Requires
    DataUtils dataUtils;

    @Model(value = Artifact.class)
    private Crud<Artifact, String> artifactCrud;
    @Model(value = License.class)
    private Crud<License, String> licenseCrud;
    @Model(value = Organization.class)
    private Crud<Organization, String> organizationCrud;
    @Model(value = Module.class)
    private Crud<Module, String> moduleCrud;

    @Override
    public void store(Artifact artifact) {
        Artifact oldArtifact = artifactCrud.findOne(artifact.getGavc());
        if (oldArtifact == null) {
            artifactCrud.save(artifact);
        } else {
            for (String license : oldArtifact.getLicenses()) {
                artifact.addLicense(license);
            }
            artifactCrud.save(artifact);
        }
        //todo wasnt there in original
        updateLicensesInDB(artifact);
    }

    //Todo need to think about what to do for licenses when they dont exist in the db but only in the artifact
    private void updateLicensesInDB(Artifact artifact) {
        for (String license : artifact.getLicenses()) {
            if (licenseCrud.findOne(license) == null) {
                License newLicense = new License();
                newLicense.setName(license);
                newLicense.setUnknown(true);
                licenseCrud.save(newLicense);
            }
        }
    }

    @Override
    public void storeIfNew(Artifact artifact) {
        if (artifactCrud.findOne(artifact.getGavc()) == null) {
            artifactCrud.save(artifact);
            updateLicensesInDB(artifact);
        }
    }

    @Override
    public Artifact getArtifact(String gavc) {
        final Artifact artifact = artifactCrud.findOne(gavc);
        if (artifact == null) {
            throw new NoSuchElementException("artifact with gavc: " + gavc);
        }
        return artifact;
    }

    @Override
    public List<Artifact> getAllArtifacts() {
        Iterable<Artifact> list = artifactCrud.findAll();
        return Lists.newArrayList(list);
    }

    @Override
    public List<Artifact> getArtifacts(FiltersHolder filters) {
        Iterable<Artifact> list = artifactCrud.findAll(new MongoFilter<Artifact>(JongoUtils.generateQuery(filters.getArtifactFieldsFilters())));
        return Lists.newArrayList(list);
    }

    @Override
    //todo renamed from getGavcs maybe this breaks compatibility to see
    public List<String> getArtifactGavcs(FiltersHolder filters) {
        List<String> gavcList = new ArrayList<>();
        for (Artifact artifact : getArtifacts(filters)) {
            gavcList.add(artifact.getGavc());
        }
        return gavcList;
    }

    @Override
    public List<String> getArtifactGroupIds(FiltersHolder filters) {
        List<String> list = new ArrayList<>();
        for (Artifact artifact : getArtifacts(filters)) {
            if (!list.contains(artifact.getGroupId()))
                list.add(artifact.getGroupId());
        }
        return list;
    }

    @Override
    public List<License> getArtifactLicenses(String gavc, FiltersHolder filters) {
        final Artifact artifact = getArtifact(gavc);
        final List<License> licensesList = new ArrayList<License>();
        for (String name : artifact.getLicenses()) {
            final License license = licenseCrud.findOne(name);
            // Here is a license to identify
            if (license == null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("This artifact ");
                sb.append(gavc);
                sb.append(" has a license ");
                sb.append('"');
                sb.append(name);
                sb.append("'");
                sb.append(" that not found in the database: ");
                LOG.info( sb.toString() );
                final License notIdentifiedLicense = new License();
                notIdentifiedLicense.setName(name);
                licensesList.add(notIdentifiedLicense);
            }
            // The license has to be validated
            else if (filters.shouldBeInReport(license)) {
                licensesList.add(license);
            }
        }
        return licensesList;
    }

    @Override
    public String getArtifactLastVersion(String gavc) {
        final List<String> versions = getArtifactVersions(gavc);
        try {
            return versionsService.getLastVersion(versions);
        } catch (Exception e) {
            // These versions cannot be compared
            // Let's use the Collection.max() method by default
            return Collections.max(versions);
        }
    }

    @Override
    public List<String> getArtifactVersions(String gavc) {
        final Artifact artifact = getArtifact(gavc);
        return getArtifactVersions(artifact);
    }

    @Override
    public List<String> getArtifactVersions(Artifact artifact) {
        final List<String> versions = new ArrayList<>();
        versions.add(artifact.getVersion());
        Iterable<Artifact> list = artifactCrud.findAll(new MongoFilter<Artifact>("{artifactId:#,groupId:#,classifier:#,extension:#}",
                artifact.getArtifactId(), artifact.getGroupId(), artifact.getClassifier(), artifact.getExtension()));
        for (Artifact artifact1 : list) {
            if (!versions.contains(artifact1.getVersion())) {
                versions.add(artifact1.getVersion());
            }
        }
        return versions;
    }

    @Override
    //todo it only searches the uses field which is the dependencies and not the artifacts.
    //todo this method should go in moduleshandler
    public List<Module> getAncestors(String gavc, FiltersHolder filters) {
//        LOG.error("inside get ancestors in artifact handler for gavc: " + gavc);
        Artifact artifact = getArtifact(gavc);
//        LOG.error("I should have an id: " + artifact.getGavc());
        final Map<String, Object> queryParams = filters.getModuleFieldsFilters();
        queryParams.put("'uses'", artifact.getGavc());
//        LOG.error("query paramteres" + JongoUtils.generateQuery(queryParams));
        final Iterable<Module> results = moduleCrud.findAll(new MongoFilter<Module>(JongoUtils.generateQuery(queryParams)));
        final List<Module> ancestors = new ArrayList<>();
        int count = 0;
        for (Module ancestor : results) {
            count ++;
//            LOG.error("count: "+count);
            ancestors.add(ancestor);
//            LOG.error("added first ancestor"+ancestor.getId());
            Set<Dependency> s = ancestor.getDependencies();
            for (Dependency d : s) {
//                LOG.error("list of dependency for this ancestor "+s.size());
                if (gavc.equals(d.getTarget())) {
//                    LOG.error("gavc is  " + gavc + " target is " + d.getTarget() + " source is " + d.getSource());
                    Module module = moduleCrud.findOne(d.getSource());
                    if(module!=null){
                        for (final String artifactId : dataUtils.getAllArtifactsGavcs(module)) {
                            ancestors.addAll(getAncestors(artifactId, filters));
                    }

                }
            }
        }
        }
        return ancestors;
    }

    @Override
    public Module getModule(Artifact artifact) {
        return moduleService.getRootModuleOf(artifact.getGavc());
    }

    @Override
    public Organization getOrganization(Artifact artifact) {
        final Module module = getModule(artifact);
        if (module == null || module.getOrganization().isEmpty() || module.getOrganization() == null) {
            return null;
        }
        Organization organization = organizationCrud.findOne(module.getOrganization());
        if (organization == null) {
            throw new NoSuchElementException("artifact with gavc: " + module.getOrganization());
        }
        return organization;
    }

    @Override
    public void updateDoNotUse(String gavc, Boolean doNotUse) {
        final Artifact artifact = getArtifact(gavc);
        artifact.setDoNotUse(doNotUse);
        artifactCrud.save(artifact);
    }

    @Override
    public void updateDownLoadUrl(String gavc, String downLoadUrl) {
        final Artifact artifact = getArtifact(gavc);
        artifact.setDownloadUrl(downLoadUrl);
        artifactCrud.save(artifact);
    }

    @Override
    public void updateProvider(String gavc, String provider) {
        final Artifact artifact = getArtifact(gavc);
        artifact.setProvider(provider);
        artifactCrud.save(artifact);
    }

    @Override
    public void addLicense(String gavc, String licenseName) {
        //TODO i dont think any one calls this method?
        final Artifact artifact = getArtifact(gavc);
        /*todo if the lisences doesnt exsist and the artifact has no liceses then add the lisence.
        * if the license doesnt exist and the artifact has other lisences do nothing.
        * this is bad because in both cases we have either missing licenses or licenses that are in the db corretly.
        */
        // Try to find an existing license that match the new one
//todo the original used resolve which looks just in the cache and not the db
        // final License license = licenseCrud.resolve(licenseId);
        License license = licenseCrud.findOne(licenseName);
        // If there is no existing license that match this one let's use the provided value but
        // only if the artifact has no license  yet. Otherwise it could mean that users has already
        // identify the license manually.
        if (license == null) {
            if (artifact.getLicenses().isEmpty()) {
                LOG.warn("Add reference to a non existing license called " + licenseName + " in  artifact " + artifact.getGavc());
                addLicenseToArtifact(artifact.getGavc(), licenseName);
            }
        }
        // Add only if the license is not already referenced
        else if (!artifact.getLicenses().contains(license.getName())) {
            addLicenseToArtifact(artifact.getGavc(), license.getName());
        }
    }

    /**
     * from MH
     *
     * @param gavc
     * @param licenseId
     */
    @Override
    public void addLicenseToArtifact(String gavc, String licenseId) {
        final Artifact artifact = getArtifact(gavc);
        // Don't need to access the DB if the job is already done
        if (artifact.getLicenses().contains(licenseId)) {
            return;
        }
        //todo doesnt call the above method? nor does it in the old version
        artifact.addLicense(licenseId);//saves directly to the artifact but not the database for lic.
        artifactCrud.save(artifact);
    }

    /**
     * @param gavc
     * @param licenseId
     */
    @Override
    public void removeLicenseFromArtifact(String gavc, String licenseId) {
        final Artifact artifact = getArtifact(gavc);
        // Don't need to access the DB if the job is already done
        if (!artifact.getLicenses().contains(licenseId)) {
            return;
        }
        artifact.removeLicense(licenseId);
        artifactCrud.save(artifact);
    }

    @Override
    public void deleteArtifact(String gavc) {
        Artifact artifact = getArtifact(gavc);
        artifactCrud.delete(artifact);
    }
}
