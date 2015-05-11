package org.axway.grapes.core.handler;
//todo add exceptions

import com.google.common.collect.Lists;
import org.axway.grapes.core.service.OrganizationService;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;
import org.wisdom.jongo.service.MongoFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class OrganizationHandler implements OrganizationService {

    @Model(value = Organization.class)
    private Crud<Organization, String> organizationCrud;
    @Model(value = Module.class)
    private Crud<Module, String> moduleCrud;

    @Override
    public void store(Organization organization) {
       organizationCrud.save(organization);
    }

    @Override
    public List<String> getOrganizationNames() {
        Set<String> listOfNames = new HashSet<>();
        for(Organization organization:organizationCrud.findAll()){
            listOfNames.add(organization.getName());
        }
        return Lists.newArrayList(listOfNames);
    }

    @Override
    public Organization getOrganization(String organizationId) {
        System.out.println("find one: id-"+organizationId);
        final Organization organization = organizationCrud.findOne(organizationId);
        System.out.println("returns: "+organization);

        if(organization == null){
           //todo exception not found
        }

        return organization;
    }

    @Override
    public List<Organization> getAllOrganizations(){
        return Lists.newArrayList(organizationCrud.findAll());
    }

    @Override
    public void deleteOrganization(String organizationId) {
        final Organization organization = getOrganization(organizationId);
        if(organization==null){
            //todo throw doesnt exsit
            System.out.println("error");
            return;
        }
       organizationCrud.delete(organizationId);
        //todo broken
       removeModulesOrganization(organization);

    }

    @Override
    public List<String> getCorporateGroupIds(String organizationId) {
        final Organization organization = getOrganization(organizationId);
        return organization.getCorporateGroupIdPrefixes();
    }

    @Override
    public void addCorporateGroupId(String organizationId, String corporateGroupId) {
        final Organization organization = getOrganization(organizationId);//needs to throw an error inside this or call the crud directly


        if(!organization.getCorporateGroupIdPrefixes().contains(corporateGroupId)){
            organization.getCorporateGroupIdPrefixes().add(corporateGroupId);
            organizationCrud.save(organization);
        }

        addModulesOrganization(corporateGroupId, organization);

    }

    @Override
    public void removeCorporateGroupId(String organizationId, String corporateGroupId) {
        final Organization organization = getOrganization(organizationId);

        if(organization.getCorporateGroupIdPrefixes().contains(corporateGroupId)){
            organization.getCorporateGroupIdPrefixes().remove(corporateGroupId);
            organizationCrud.save(organization);
        }

        removeModulesOrganization(corporateGroupId, organization);

    }


    @Override
    public void removeModulesOrganization(Organization organization) {
        System.out.println("why null?");
        Module m = new Module();
        //m.setId("test1");
        m.setName("rat1");
        m.setOrganization("kittycorp");
        moduleCrud.save(m);
        m.setId("rat2");
        m.setName("rat2");
        m.setOrganization("kittycorp");
        moduleCrud.save(m);
        m.setId("rat3");
        m.setName("rat2name");
        m.setOrganization("kittycorp");
        moduleCrud.save(m);

       Iterable<Module> list= moduleCrud.findAll(new MongoFilter<Module>(
                "{organization:#}", organization.getName()));
        for(Module module:list){
            module.setOrganization("");
            moduleCrud.save(module);
        }

    }

    @Override
    public void removeModulesOrganization(String corporateGidPrefix, Organization organization) {
        //todo needs regexfor name
        Iterable<Module> list= moduleCrud.findAll(new MongoFilter<Module>(
                "{organization:#}", organization.getName()));
        for(Module module:list){
            module.setOrganization("");
            moduleCrud.save(module);
        }
//        final Jongo datastore = getJongoDataStore();
//
//        datastore.getCollection(DbCollections.DB_MODULES)
//                .update("{ $and: [" +
//                        "{ " + DbModule.HAS_DB_FIELD + " :#} ," +
//                        JongoUtils.generateQuery(DbModule.ORGANIZATION_DB_FIELD, organization.getName()) + "]}"
//                        , Pattern.compile(corporateGidPrefix + "*"))
//                .multi()
//                .with("{$set: { " + DbModule.ORGANIZATION_DB_FIELD + " : \"\"}}");
    }
    @Override
    public void addModulesOrganization(String corporateGidPrefix, Organization organization) {
//        final Jongo datastore = getJongoDataStore();
//
//        datastore.getCollection(DbCollections.DB_MODULES)
//                .update("{ "+DbModule.HAS_DB_FIELD+" :#}", Pattern.compile(corporateGidPrefix + "*"))
//                .multi()
//                .with("{$set: " + JongoUtils.generateQuery(DbModule.ORGANIZATION_DB_FIELD, organization.getName()) + "}");
//

    }
}
