package org.axway.grapes.server.db.datamodel;

import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database Product
 *
 * <p>Class that holds the representation of a product stored in the database.</p>
 *
 * @author jdcoffre
 */
public class DbProduct {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String name;

    public static final String ORGANIZATION_DB_FIELD = "organization";
    private String organization = "";

    public static final String MODULE_NAMES_DB_FIELD = "modules";
    private List<String> modules = new ArrayList<String>();

    public static final String DELIVERIES_DB_FIELD = "deliveries";
    private Map<String, List<String>> deliveries = new HashMap<String, List<String>>();

    public String getDatamodelVersion() {
        return datamodelVersion;
    }

    public void setDatamodelVersion(String datamodelVersion) {
        this.datamodelVersion = datamodelVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public Map<String, List<String>> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Map<String, List<String>> deliveries) {
        this.deliveries = deliveries;
    }
}
