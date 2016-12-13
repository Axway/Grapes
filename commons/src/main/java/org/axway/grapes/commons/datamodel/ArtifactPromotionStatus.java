package org.axway.grapes.commons.datamodel;

public class ArtifactPromotionStatus {
	
	private boolean error;
	private String message;
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
