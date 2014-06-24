package org.axway.grapes.server.db.datamodel;

import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Organization Model Class
 *
 *
 * <P> Model Objects are used in the communication with the Grapes server.
 * These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class DbOrganization {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String name;

    public static final String CORPORATE_GROUPID_PREFIXES_FIELD = "corporateGroupIdPrefixes";
    private List<String> corporateGroupIdPrefixes = new ArrayList<String>();


    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getCorporateGroupIdPrefixes() {
        return corporateGroupIdPrefixes;
    }

    public void setCorporateGroupIdPrefixes(final List<String> corporateGroupIdPrefixes) {
        this.corporateGroupIdPrefixes = corporateGroupIdPrefixes;
    }

}
