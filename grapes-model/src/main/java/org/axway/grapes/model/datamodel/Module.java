package org.axway.grapes.model.datamodel;
//todo look at how the id is formed and make sure we get the right thing kinda like gavac for artifact.
//todo this is used by the plugin must stay compatible made a layer on top moduleComplete.
//todo should we automatically update has an use when we add new artifacts or dependencies?
import java.util.*;

/**
 * Module Model Class
 *
 *
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class Module {

    private String id ;
    private String name="";
    private String version="";
    private String organization = "";

    private boolean promoted = false;
    private boolean isSubmodule = false;



    private List<String> has = new ArrayList<>();
    private List<String> uses = new ArrayList<>();

    private Set<String> artifacts = new HashSet<>();
    private Set<Dependency> dependencies = new HashSet<Dependency>();
    private Set<Module> submodules = new HashSet<Module>();
    private Map<String, String> buildInfo = new HashMap<String, String>();

    public Module() {

    }

    public String getId() {
        return name + ":" + version;
    }

    protected void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
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

    /**
     * Sets the promotion state.
     *
     * <P>INFO: This method updates automatically all the contained artifacts.
     *
     * @param promoted boolean
     */
    public void setPromoted(final boolean promoted) {
        this.promoted = promoted;

        // for (Artifact artifact : artifacts) {
        //   artifact.setPromoted(promoted);
//        }

        for (Module suModule : submodules) {
            suModule.setPromoted(promoted);
        }
    }
    public boolean isSubmodule() {
        return isSubmodule;
    }

    public void setSubmodule(final boolean isSubmodule) {
        this.isSubmodule = isSubmodule;
    }

    public Set<String> getArtifacts() {
        return artifacts;
    }
    public void setArtifacts(final Set<String> artifactGavcs) {
        this.artifacts = artifactGavcs;
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

    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(final Set<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    public Set<Module> getSubmodules() {
        return submodules;
    }


    public void setSubmodules(final Set<Module> submodules) {
        this.submodules = submodules;
    }
    public Map<String, String> getBuildInfo() {
        return buildInfo;
    }
    public void setBuildInfo(final Map<String, String> buildInfo) {
        this.buildInfo = buildInfo;
    }

    public void addArtifacts(final List<Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            addArtifact(artifact);
        }
    }
    /**
     * Adds a submodule to the module.
     *
     * <P>
     * INFO: If the module is promoted, all added submodule will be promoted.
     *
     * @param submodule Module
     */
    public void addSubmodule(final Module submodule) {
        if (!submodules.contains(submodule)) {
            submodule.setSubmodule(true);

            if (promoted) {
                submodule.setPromoted(promoted);
            }

            submodules.add(submodule);
        }
    }


    /**
     * Adds an artifact to the module.
     *
     * <P>
     * INFO: If the module is promoted, all added artifacts will be promoted.
     *
     * @param artifact Artifact
     */
    public void addArtifact(final Artifact artifact) {
        if (!artifacts.contains(artifact.getGavc())) {
            if (promoted) {
                //todo this needs to be saved to the database somehow
                //or throw an error that you cant add artifacts after a module is promoted
                artifact.setPromoted(promoted);
            }

            artifacts.add(artifact.getGavc());
        }
    }

    /**
     * Adds a set of artifact to the module.
     *
     * <P>
     * INFO: If the module is promoted, all added artifacts will be promoted.
     *
     * @param artifacts Listof Artifact
     */
    public void addAllArtifacts(final List<Artifact> artifacts) {
        //todo check promoted then if model is promoted and artifact is promoted add it otherwise reject it
        for (Artifact artifact : artifacts) {
            addArtifact(artifact);
        }
    }
    public void addDependency(final Artifact artifact, final Scope scope) {
        final Dependency dependency = new Dependency(this.getId(), artifact.getGavc(), scope);
        getDependencies().add(dependency);
    }

    /**
     * Add a dependency to the module.
     *
     * @param dependency Dependency
     */
    public void addDependency(final Dependency dependency) {
        if(dependency != null && !dependencies.contains(dependency)){
            this.dependencies.add(dependency);
        }
    }

    public void addDependencies(final List<Dependency> dependencies) {
        this.dependencies.addAll(dependencies);
    }
    public void flushDependencies() {
        dependencies.clear();
    }

    public void flushSubmodules() {
        submodules.clear();
    }
    public void flushArtifacts() {
        artifacts.clear();
    }


    public void updateHasAndUse() {
        has.clear();
        uses.clear();

        for (Module submodule : getSubmodules()) {
            if (submodule instanceof Module) {
                ((Module) submodule).updateHasAndUse();
                has.addAll(((Module) submodule).has);
                uses.addAll(((Module) submodule).uses);
            }
        }

//        has.addAll(getArtifacts());
        for (String artifact: getArtifacts()){
            has.add(artifact);
        }

        for (Dependency dependency : getDependencies()) {

            uses.add(dependency.getTarget());
        }

        //Remove all the artifacts that the module produces from "use" field
        uses.removeAll(has);
    }

    /**
     * Checks if the module is the same than an other one.
     *
     * @param obj Object
     * @return <tt>true</tt> only if name/version are the same in both.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Module) {
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(name);
        sb.append(version);

        return sb.toString().hashCode();
    }
    public static String generateID(final String moduleName, final String moduleVersion) {
        return moduleName + ":" + moduleVersion;
    }

}
