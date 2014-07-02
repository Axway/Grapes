package org.axway.grapes.server.db.datamodel;

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

    private String name;

    private List<String> modules = new ArrayList<String>();

    private Map<String, DbProductVersion> deliveries = new HashMap<String, DbProductVersion>();

    public DbProduct(final String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getModules() {
        return modules;
    }

    public void addModule(final String moduleName) {
        if(!modules.contains(moduleName)){
            modules.add(moduleName);
        }
    }

    public void removeModule(final String moduleName) {
        modules.remove(moduleName);
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public Map<String, DbProductVersion> getDeliveries() {
        return deliveries;
    }

    public void addDelivery(final DbProductVersion delivery) {
        this.deliveries.put(delivery.getVersion(), delivery);
    }

    public void removeDelivery(final DbProductVersion delivery) {
        this.deliveries.remove(delivery.getVersion());
    }

    public void setDeliveries(final Map<String, DbProductVersion> deliveries) {
        this.deliveries = deliveries;
    }

    public class DbProductVersion {

        private String version;

        private Map<String, String> modules = new HashMap<String, String>();

        public DbProductVersion(final String productVersion){
            this.version = productVersion;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Map<String, String> getModules() {
            return modules;
        }

        public void setModules(Map<String, String> modules) {
            this.modules = modules;
        }
    }
}
