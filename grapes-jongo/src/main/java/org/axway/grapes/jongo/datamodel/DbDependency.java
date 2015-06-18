package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Scope;

/**TODO sisnce org.axway.grapes.jongo.it is not stored in the db maybe we dont need this one at all only in commons
 * DbDependency
 * <p>
 * <p>Representation for a dependence between a module (source) and an artifact (target).</p>
 *
 * @author jdcoffre
 */
public class DbDependency extends Dependency {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;


    public DbDependency() {
    }

    public DbDependency(String source, String target, Scope scope) {
        super(source, target, scope);
    }



    public void setDataModelVersion(final String newVersion) {
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion() {
        return datamodelVersion;
    }

    /**
     * Checks if the dependency is the same than an other one.
     *
     * @param obj
     * @return <tt>true</tt> only if artifact/scope are the same in both.
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DbDependency && hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(super.generateSourceID());
        sb.append(":");
        sb.append(super.getTarget());
        sb.append(":");
        if (super.getScope() != null) {
            sb.append(super.getScope().toString());
        }

        return sb.toString().hashCode();
    }


}
