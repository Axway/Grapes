package org.axway.grapes.server.db.datamodel;

import org.axway.grapes.commons.datamodel.Scope;

/**
 * DbDependency
 * 
 * <p>Representation for a dependence between a module (source) and an artifact (target).</p>
 * 
 * @author jdcoffre
 */
public class DbDependency {

    public static final String DATA_MODEL_VERSION = "DATAMODEL_VERSION";
    private String datamodelVersion = DbCollections.DATAMODEL_VERSION;
	
	private String source;
	private String target;
	private Scope scope;

    public DbDependency(){
        //
    }
	
    /**
     * Generate a dependency
     * @param source
     * @param target
     * @param scope 
     */
	public DbDependency(final String source, final String target, final Scope scope) {
		this.source = source;
		this.target = target;
		this.scope = scope;
	}

    public void setDataModelVersion(final String newVersion){
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion(){
        return datamodelVersion;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public Scope getScope() {
        return scope;
    }

    /**
     * Checks if the dependency is the same than an other one.
     *
     * @param obj
     * @return <tt>true</tt> only if artifact/scope are the same in both.
     */
    @Override
    public boolean equals(final Object obj){
        return obj instanceof DbDependency && hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(source);
        sb.append(":");
        sb.append(target);
        sb.append(":");
        if(scope != null){
            sb.append(scope.toString());
        }

        return sb.toString().hashCode();
    }
}
