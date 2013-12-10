package org.axway.grapes.server.core.graphs;


public class GraphElement {

	private String value;
	private String version;
	private boolean root = false;
	
	public String getValue() {
		return value;
	}
	public void setValue(final String value) {
		this.value = value;
	}
	public boolean isRoot() {
		return root;
	}
	public void setRoot(final boolean root) {
		this.root = root;
	}
	public void setVersion(final String version) {
		this.version = version;
	}
	public String getVersion() {
		return version;
	}
	
	@Override
	public int hashCode(){
		return value.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof GraphElement && value.equals(((GraphElement) obj).value);
	}
}
