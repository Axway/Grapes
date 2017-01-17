package org.axway.grapes.commons.datamodel;

public class ArtifactPromotionStatus {
	
	private boolean promoted;
	private String message;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isPromoted() {
		return promoted;
	}
	public void setPromoted(boolean promoted) {
		this.promoted = promoted;
	}
	
}
