package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.Organization;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Organization Model Class
 * <p>
 * <p>
 * <P> Model Objects are used in the communication with the Grapes server.
 * These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class DbOrganization extends Organization {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String name;

    public String getName() {
        return name;
    }

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
