package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.Product;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Database Product
 * <p>
 * <p>Class that holds the representation of a product stored in the database.</p>
 *
 * @author jdcoffre
 */
public class DbProduct extends Product {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDatamodelVersion() {
        return datamodelVersion;
    }

    public void setDatamodelVersion(String datamodelVersion) {
        this.datamodelVersion = datamodelVersion;
    }


}
