package org.axway.grapes.tests.acceptance.materials.datamodel;

/**
 * DbDependency
 * 
 * <p>Representation for a dependence between a module (source) and an artifact (target).</p>
 * 
 * @author jdcoffre
 */
public class DbDependency {
	
	private String source;
	private String target;
	private String scope;

    public void setSource(final String source) {
        this.source = source;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getScope() {
        return scope;
    }

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
            sb.append(scope);
        }

        return sb.toString().hashCode();
    }
}
