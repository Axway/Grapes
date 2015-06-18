package org.axway.grapes.core.webapi.resources;

import org.axway.grapes.model.datamodel.Artifact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jennifer on 6/9/15.
 */
public class ModuleComplete {
    private String name="";
    private String version="";
    private String organization = "";

    private boolean promoted = false;
    private boolean isSubmodule = false;



    private List<String> has = new ArrayList<>();
    private List<String> uses = new ArrayList<>();

    private Set<Artifact> artifacts = new HashSet<>();
    private Set<DependencyComplete> dependencies = new HashSet<DependencyComplete>();
    private Set<ModuleComplete> submodules = new HashSet<ModuleComplete>();
    private Map<String, String> buildInfo = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public boolean getIsSubmodule() {
        return isSubmodule;
    }

    public void setIsSubmodule(boolean isSubmodule) {
        this.isSubmodule = isSubmodule;
    }

    public List<String> getHas() {
        return has;
    }

    public void setHas(List<String> has) {
        this.has = has;
    }

    public List<String> getUses() {
        return uses;
    }

    public void setUses(List<String> uses) {
        this.uses = uses;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public Set<DependencyComplete> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<DependencyComplete> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<ModuleComplete> getSubmodules() {
        return submodules;
    }

    public void setSubmodules(Set<ModuleComplete> submodules) {
        this.submodules = submodules;
    }

    public Map<String, String> getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(Map<String, String> buildInfo) {
        this.buildInfo = buildInfo;
    }
}
