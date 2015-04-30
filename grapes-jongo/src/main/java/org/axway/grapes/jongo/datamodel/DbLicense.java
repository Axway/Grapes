package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.License;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Database License
 * <p>
 * <p>Class that define the representation of licenses stored in the database.
 * The name (short name) is use as an ID. A database index is created on org.axway.grapes.jongo.it.</p>
 *
 * @author jdcoffre
 */
public class DbLicense extends License {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String name = "";


    public void setDataModelVersion(final String newVersion) {
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion() {
        return datamodelVersion;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

}
