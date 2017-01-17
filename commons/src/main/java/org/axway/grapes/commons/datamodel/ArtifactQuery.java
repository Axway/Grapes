package org.axway.grapes.commons.datamodel;

public class ArtifactQuery {
	
	private String user;
	private int stage = -1;
	private String name;
	private String sha256;
	private String type;
	private String location;
	
	public ArtifactQuery(String user, int stage, String name, String sha256, String type, String location) {
		this.user = user;
		this.stage = stage;
		this.name = name;
		this.sha256 = sha256;
		this.type = type;
		this.location = location;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSha256() {
		return sha256;
	}
	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
