package org.axway.grapes.commons.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Delivery {

	private String commercialName;
	private String commercialVersion;
	private String version;
	private String jenkinsBuildUrl;
	private String releaseDate;
	private String moduleName;
	private List<String> dependencies = new ArrayList<String>();
	private List<Artifact> allArtifactDependencies = new ArrayList<Artifact>();

	public Delivery() {
		// Only for creating an instance
	}
	public String getCommercialName() {
		return commercialName;
	}

	public void setCommercialName(String commercialName) {
		this.commercialName = commercialName.trim();
	}

	public String getCommercialVersion() {
		return commercialVersion;
	}

	public void setCommercialVersion(String commercialVersion) {
		this.commercialVersion = commercialVersion.trim();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version.trim();
	}

	public String getJenkinsBuildUrl() {
		return jenkinsBuildUrl;
	}

	public void setJenkinsBuildUrl(String jenkinsBuildUrl) {
		this.jenkinsBuildUrl = jenkinsBuildUrl;
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
	
	public void addDependency(String dependency)
	{
		this.dependencies.add(dependency);
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public String toString() {
		return "Delivery{" +
				"commercialName='" + commercialName + '\'' +
				", commercialVersion='" + commercialVersion + '\'' +
				", version='" + version + '\'' +
				", jenkinsBuildUrl='" + jenkinsBuildUrl + '\'' +
				", releaseDate='" + releaseDate + '\'' +
				", moduleName='" + moduleName + '\'' +
				", dependencies=" + dependencies +
				", all artifacts=" + allArtifactDependencies +
				'}';
	}

	public List<Artifact> getAllArtifactDependencies() {
		return allArtifactDependencies;
	}

	public void setAllArtifactDependencies(List<Artifact> allArtifactDependencies) {
		this.allArtifactDependencies = allArtifactDependencies;
	}
}
