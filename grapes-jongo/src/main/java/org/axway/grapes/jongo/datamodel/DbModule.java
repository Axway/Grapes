package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;
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
    private String id;

    public DbModule(Module module) {
        this.id = module.getId();
        setName(module.getName());
        setVersion(module.getVersion());
        setArtifacts(module.getArtifacts());
        setBuildInfo(module.getBuildInfo());
        setDependencies(module.getDependencies());
        setHas(module.getHas());
        setId(module.getId());
        setOrganization(module.getOrganization());
        setPromoted(module.isPromoted());
        setSubmodules(module.getSubmodules());
        setUses(module.getUses());
    }

    public DbModule() {
    }

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
        updateId();
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Name: ");
        sb.append(super.getName());
        sb.append(", Version: ");
        sb.append(super.getVersion());

        return sb.toString();
    }

    public static String generateID(final String moduleName, final String moduleVersion) {
        final StringBuilder sb = new StringBuilder();

        sb.append(moduleName);
        sb.append(":");
        sb.append(moduleVersion);

        return sb.toString();
    }
}
