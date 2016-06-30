package org.axway.grapes.commons.datamodel;

import java.util.List;

public class Delivery {

	private String commercialName;
	private String commercialVersion;
	private String releaseDate;
	private List<String> dependencies;

	public Delivery() {
		// Only for creating an instance
	}
	public String getCommercialName() {
		return commercialName;
	}

	public void setCommercialName(String commercialName) {
		this.commercialName = commercialName;
	}

	public String getCommercialVersion() {
		return commercialVersion;
	}

	public void setCommercialVersion(String commercialVersion) {
		this.commercialVersion = commercialVersion;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}
}
