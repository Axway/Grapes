package org.axway.grapes.commons.datamodel;

/**
 * Dependency Model Class
 *
 * <p> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.</p>
 *
 * <p>This class is used to represent the dependency between two modules</p>
 */
public class Dependency {

    private String sourceName;
    private String sourceVersion;
    private Artifact target;
    private Scope scope;

    protected Dependency() {
        // Should only be instantiated via the DataModelObjectFactory
    }

    public Artifact getTarget() {
        return target;
    }

    protected void setTarget(final Artifact artifact) {
        this.target = artifact;
    }

    public Scope getScope() {
        return scope;
    }

    protected void setScope(final Scope scope) {
        this.scope = scope;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(final String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(final String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * Checks if the dependency is the same than an other one.
     *
     * @param obj Object
     * @return <tt>true</tt> only if artifact/scope are the same in both.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Dependency){
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(sourceName);
        sb.append(sourceVersion);
        sb.append(target.getGroupId());
        sb.append(target.getArtifactId());
        sb.append(target.getClassifier());
        sb.append(target.getVersion());
        sb.append(target.getType());
        sb.append(target.getOrigin());
        sb.append(scope.toString());

        return sb.toString().hashCode();
    }

}
