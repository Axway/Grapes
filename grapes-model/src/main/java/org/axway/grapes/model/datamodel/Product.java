package org.axway.grapes.model.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Product {
    private String name;

    private String organization = "";

    private List<String> modules = new ArrayList<String>();

    private Map<String, List<String>> deliveries = new HashMap<String, List<String>>();

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
