package org.axway.grapes.server.reports.impl;

public class ParameterDefinition {
    private String name;
    private String description;

    public ParameterDefinition() {
    }

    public ParameterDefinition(final String paramName, final String paramDesc) {
        this.name = paramName;
        this.description = paramDesc;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
