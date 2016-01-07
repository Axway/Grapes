package org.axway.grapes.core.handler;
//todo see todo list

import com.google.common.collect.Lists;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.options.filters.CorporateFilter;
import org.axway.grapes.core.webapi.utils.JongoUtils;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;
import org.wisdom.jongo.service.MongoFilter;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class OrganizationHandler implements OrganizationService {

    @Model(value = Organization.class)
    private Crud<Organization, String> organizationCrud;
    @Model(value = Module.class)
    private Crud<Module, String> moduleCrud;

    @Requires
   DataUtils dataUtils;

    @Override
    public void store(Organization organization) {
        organizationCrud.save(organization);
    }

    @Override
    public Organization getOrganization(String organizationName) {
        final Organization organization = organizationCrud.findOne(organizationName);
        if (organization == null) {
            System.out.println("too bad I throw an error");
            throw new NoSuchElementException(organizationName);
        }
      return organization;
    }

    @Override
    public List<String> getOrganizationNames() {
        Set<String> listOfNames = new HashSet<>();
        for (Organization organization : organizationCrud.findAll()) {
            listOfNames.add(organization.getName());
        }
        return Lists.newArrayList(listOfNames);
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return Lists.newArrayList(organizationCrud.findAll());
    }

    @Override
    public void deleteOrganization(String organizationName) {
        final Organization organization = getOrganization(organizationName);
            organizationCrud.delete(organizationName);
        //todo broken regex pattern and maybe we should delete from products too?
       removeModulesOrganization(organization);
    }

    @Override
    public List<String> getCorporateGroupIds(String organizationName) {
        final Organization organization = getOrganization(organizationName);
        return organization.getCorporateGroupIdPrefixes();
    }

    @Override
    public void addCorporateGroupId(String organizationName, String corporateGroupId) {
        final Organization organization = getOrganization(organizationName);
        if (!organization.getCorporateGroupIdPrefixes().contains(corporateGroupId)) {
            organization.getCorporateGroupIdPrefixes().add(corporateGroupId);
            organizationCrud.save(organization);
        }
        addModulesOrganization(corporateGroupId, organization);
    }

    @Override
    public void removeCorporateGroupId(String organizationName, String corporateGroupId) {
        final Organization organization = getOrganization(organizationName);
        if (organization.getCorporateGroupIdPrefixes().contains(corporateGroupId)) {
            organization.getCorporateGroupIdPrefixes().remove(corporateGroupId);
            organizationCrud.save(organization);
        }
        removeModulesOrganization(corporateGroupId, organization);
    }


    @Override
    public void removeModulesOrganization(Organization organization) {
        Iterable<Module> list = moduleCrud.findAll(new MongoFilter<>(
                "{organization:#}", organization.getName()));
        for (Module module : list) {
            module.setOrganization("");
            moduleCrud.save(module);
        }
    }


    public void removeOrganizationFromProduct(Organization organization) {
        Iterable<Module> list = moduleCrud.findAll(new MongoFilter<>(
                "{organization:#}", organization.getName()));
        for (Module module : list) {
            module.setOrganization("");
            moduleCrud.save(module);
        }
    }


    @Override
    public void removeModulesOrganization(String corporateGidPrefix, Organization organization) {
        //todo it finds both org.blahblahb and borg.blahblah need to find something with regx?
         Iterable<Module> list = moduleCrud.findAll(new MongoFilter<>(
                "{$and: [ { has :#} ," + JongoUtils.generateQuery("organization", organization.getName()) + "]}", Pattern.compile(corporateGidPrefix + "*")));

        for (Module module : list) {
            module.setOrganization("");
            moduleCrud.save(module);
        }
    }

    @Override
    //todo what is this changing? the organization of the module? sub modules? artifacts? should be in modules?
    public void addModulesOrganization(String corporateGidPrefix, Organization organization) {
        Iterable<Module> moduleList = moduleCrud.findAll(new MongoFilter<>("{ has :#}", Pattern.compile(corporateGidPrefix + "*")));
        for(Module module : moduleList){
            module.setOrganization(organization.getName());
            moduleCrud.save(module);
        }
//        datastore.getCollection(DbCollections.DB_MODULES)
//                .update("{ "+DbModule.HAS_DB_FIELD+" :#}", Pattern.compile(corporateGidPrefix + "*"))
//                .multi()
//                .with("{$set: " + JongoUtils.generateQuery(DbModule.ORGANIZATION_DB_FIELD, organization.getName()) + "}");


    }

    @Override
    //todo
    public Organization getMatchingOrganization(Module module) {


            if(module.getOrganization() != null
                    && !module.getOrganization().isEmpty()){
                return getOrganization(module.getOrganization());
            }

            for(Organization organization: getAllOrganizations()){


                List<String> gavcs = dataUtils.getAllArtifactsGavcs(module);
                Set<Artifact> artifacts = dataUtils.getAllArtifacts(gavcs);
                final CorporateFilter corporateFilter = new CorporateFilter(organization);

                boolean b;
                if(artifacts.isEmpty()) {
                   b = corporateFilter.evaluate(module.getId());
                } else{
                    b=   corporateFilter.evaluate(artifacts.iterator().next().getGavc());
                    }

                if(b){
                //if(corporateFilter.matches(module)){
                    return organization;
                }
            }

            return null;
        }

}
