package org.axway.grapes.jongo.datamodel;
//todo tostring method

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Scope;
import org.jongo.marshall.jackson.oid.Id;

import java.util.Collection;

/**
 * Database Module
 * <p>
 * <p>Class that holds the representation of modules stored in the database.
 * id composed of the name and the version of the module is used as an ID. A database index has been created on org.axway.grapes.jongo.it.</p>
 *
 * @author jdcoffre
 */
public class DbModule extends Module {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String id = "";

    public DbModule(Module m) {
       this.id = m.getId();
        setName(m.getName());
        setVersion(getVersion());
        setArtifacts(m.getArtifacts());
        setBuildInfo(m.getBuildInfo());
        setDependencies(m.getDependencies());
        setHas(m.getHas());
        setId(m.getId());
        setOrganization(m.getOrganization());
        setPromoted(m.isPromoted());
        setSubmodules(m.getSubmodules());
        setUses(m.getUses());

    }

    public DbModule() {

    }

    /**
     * Here is a workaround because of this mongodb open issue:
     * https://jira.mongodb.org/browse/SERVER-267
     */



    public void setDataModelVersion(final String newVersion) {
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion() {
        return datamodelVersion;
    }

    public final void updateId() {
        id = generateID(super.getName(), super.getVersion());
    }

    public String getId() {
        return id;
    }
    public void setName(final String name) {
        super.setName(name);
        updateId();
    }




    public void setVersion(final String version) {
        super.setVersion(version);
        updateId();
    }

    private static boolean contains(Collection<Artifact> artifacts, String gavc) {
        if (artifacts == null || gavc == null) {
            return false;
        }

        for (Artifact artifact : artifacts) {
            if (artifact.getGavc().equalsIgnoreCase(gavc)) {
                return true;
            }
        }
        return false;
    }

    public void addArtifact(final DbArtifact artifact) {
        final String artifactGavc = artifact.getGavc();

        if (!contains(getArtifacts(), artifactGavc)) {
            getArtifacts().add(artifact);
        }
    }



    public void addDependency(final String artifactGavc, final Scope scope) {
        final Dependency dependency = new DbDependency(this.getId(), artifactGavc, scope);
        super.getDependencies().add(dependency);
    }




    @Override
    public String toString() {
        return "Name: " + super.getName() + ", Version: " + super.getVersion();
    }

    public static String generateID(final String moduleName, final String moduleVersion) {
        return moduleName + ":" + moduleVersion;
    }




}
