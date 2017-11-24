package org.axway.grapes.server.db.datamodel;

import java.util.List;

/**
 * DbSearch
 * <p>class that holds the gathered search results for modules and artefacts</p>
 *
 */
public class DbSearch {

    public static final String MODULES_DB_FIELD = "modules";
    private List<String> modules;

    public static final String ARTIFACTS_DB_FIELD = "artifacts";
    private List<String> artifacts;

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public List<String> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<String> artifacts) {
        this.artifacts = artifacts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("{ modules: ");
        sb.append(modules);
        sb.append(", artifacts: ");
        sb.append(artifacts);
        sb.append("}");
        return sb.toString();
    }
}
