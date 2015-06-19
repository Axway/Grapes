package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.GrapesInfo;
/**
 * Database Grapes Info
 *
 * <p>Holds the administration information of Grapes. Useful for migrations until now.</p>
 *
 * @author jdcoffre
 */
public class DbGrapesInfo extends GrapesInfo{

    public static final String CURRENT_DATAMODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = "";


    public String getDatamodelVersion() {
        return datamodelVersion;
    }

    public void setDatamodelVersion(String datamodelVersion) {
        this.datamodelVersion = datamodelVersion;
    }
}
