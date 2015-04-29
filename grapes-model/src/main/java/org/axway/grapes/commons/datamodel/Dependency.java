package org.axway.grapes.commons.datamodel;

/**
 * Dependency Model Class
 * <p>
 * <p> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.</p>
 * <p>
 * <p>This class is used to represent the dependency between two modules</p>
 */
public class Dependency {

    private String targetGAVC;
    private String source;
    private String sourceName;
    private String sourceVersion;
    private Artifact target;
    private Scope scope;

    protected Dependency() {
        // Should only be instantiated via the DataModelObjectFactory
    }

    public Dependency(final String source, final Artifact target, final Scope scope) {

        this.setSource(source);
        this.setScope(scope);
        this.target = target;
        this.targetGAVC = target.getGavc();

    }

    public Dependency(final String source, final String targetGAVC, final Scope scope) {

        this.setSource(source);
        this.setScope(scope);
        // We don't have the artifacts
        this.target = null;
        this.targetGAVC = targetGAVC;
    }

    public String getTargetGavc() {
        return targetGAVC;
    }

    public void setTargetGavc(String targetGavc) {
        this.targetGAVC = targetGavc;
    }

    public Artifact getTarget() {
        return target;
    }

    protected void setTarget(final Artifact artifact) {
        this.target = artifact;
        this.targetGAVC = artifact.getGavc();
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

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
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

        sb.append(sourceName);
        sb.append(sourceVersion);
        sb.append(target.getGroupId());
        sb.append(target.getArtifactId());
        sb.append(target.getClassifier());
        sb.append(target.getVersion());
        sb.append(target.getType());
        sb.append(scope.toString());

        return sb.toString().hashCode();
    }


}
