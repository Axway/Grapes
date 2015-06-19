package org.axway.grapes.jongo.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.axway.grapes.model.datamodel.Organization;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Organization Model Class
 * <p>
 * <p>
 * <P> Model Objects are used in the communication with the Grapes server.
 *
 *
 * @author jdcoffre
 */
public class DbOrganization extends Organization {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;
    @Id
    private String name;


    public DbOrganization(Organization organization) {
        this.name= organization.getName();
        setName(organization.getName());
        setCorporateGroupIdPrefixes(organization.getCorporateGroupIdPrefixes());
    }

    public  DbOrganization(){

    }


    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("name")
    public void setName(final String name) {
        this.name = name;
    }


    public String getDatamodelVersion() {
        return datamodelVersion;
    }

    public void setDatamodelVersion(String datamodelVersion) {
        this.datamodelVersion = datamodelVersion;
    }


}
