package org.axway.grapes.model.datamodel;
//todo used by the plugin needs to stay the same

/**
 * Dependency Model Class
 * <p>
 * <p> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.</p>
 * <p>
 * <p>This class is used to represent the dependency between two modules</p>
 */
public class Dependency {

    private String datamodelVersion = DbCollections.datamodelVersion;
    //module name and version
    private String source;
    //artifact gavc
    private String target;
    private Scope scope;

    public Dependency() {
        // Should only be instantiated via the DataModelObjectFactory
    }

    public Dependency(final String sourceId, final String target, final Scope scope) {
        this.source = sourceId;
        this.setScope(scope);
        this.target = target;
    }

    public void setSourceNameAndSourceVersionFromSourceID(String sourceId) {
        if (sourceId == null || sourceId.split(":").length == 0) {
            setSource("");
            setTarget("");
            return;
        }
        String[] s = sourceId.split(":");
        if (s.length == 1) {
            setSource(s[0]);
            setTarget("");
        } else {
            setSource(s[0]);
            setTarget(s[1]);
        }
    }

    public String generateSourceID() {
        return this.getSource() + ":" + this.getTarget();
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public Scope getScope() {
        return scope;
    }

    protected void setScope(final Scope scope) {
        this.scope = scope;
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
     * @param obj Object
     * @return <tt>true</tt> only if artifact/scope are the same in both.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Dependency) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(source);
        sb.append(":");
        sb.append(target);
        sb.append(":");
        if (scope != null) {
            sb.append(scope.toString());
        }

        return sb.toString().hashCode();
    }
}
