package org.axway.grapes.tests.acceptance.materials.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Database Artifact 
 * 
 * <p>Class that holds the representation of the artifacts stored in the database.
 * The gavc is used to identify the DbArtifacts Object. A database index is created on it.
 * </p>
 * 
 * @author jdcoffre
 */
public class DbArtifact {
	
	@Id
    @JsonProperty("_id")
	private String id;
	
	public static final String GAV_DB_FIELD = "gavc"; 
	private String gavc;

	public static final String GROUPID_DB_FIELD = "groupId"; 
	private String groupId;

	public static final String ARTIFACTID_DB_FIELD = "artifactId"; 
	private String artifactId;

	public static final String VERSION_DB_FIELD = "version"; 
	private String version;

	public static final String CLASSIFIER_DB_FIELD = "classifier"; 
	private String classifier = "";

	public static final String TYPE_DB_FIELD = "type"; 
	private String type = "";

	public static final String EXTENSION_DB_FIELD = "extension"; 
	private String extension = "";

	public static final String PROMOTION_DB_FIELD = "promoted";
	private boolean promoted;

	public static final String LICENCES_DB_FIELD = "licenses"; 
	private List<String> licenses = new ArrayList<String>();

	public static final String DOWNLOAD_URL_DB_FIELD = "downloadUrl"; 
	private String downloadUrl = "";

	public static final String SIZE_DB_FIELD = "size"; 
	private String size = "";
	

	public String getGroupId() {
		return groupId;
	}

	public final void setGroupId(final String groupId) {
		this.groupId = groupId;
		updateGavc();
	}

	public String getArtifactId() {
		return artifactId;
	}

	public final void setArtifactId(final String artifactId) {
		this.artifactId = artifactId;
		updateGavc();
	}

	public String getClassifier() {
		return classifier;
	}

	public final void setClassifier(final String classifier) {
		this.classifier = classifier;
		updateGavc();
	}

	public String getVersion() {
		return version;
	}

	public final void setVersion(final String version) {
		this.version = version;
		updateGavc();
	}

	public boolean isPromoted() {
		return promoted;
	}

	public final void setPromoted(final boolean promoted) {
		this.promoted = promoted;
	}

	public String getId() {
		return id;
	}

	public final void setId(final String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public final void setType(final String type) {
		this.type = type;
	}

	public String getExtension() {
		return extension;
	}

	public final void setExtension(final String extension) {
		this.extension = extension;
        updateGavc();
	}
	
	public final void updateGavc(){		
		gavc = generateGAVC(groupId, artifactId, version, classifier, extension);
	}
	
	public String getGavc(){
		return gavc;
	}

	public List<String> getLicenses(){
		return licenses;
	}

	public void setLicenses(final List<String> licenses) {
		this.licenses = licenses;
	}

	public void addLicense(final DbLicense license) {
		final String licenseId = license.getName();
		
		if(!licenses.contains(licenseId)){
			this.licenses.add(licenseId);
		}
	}

	public void removeLicense(final String licenseId) {
		licenses.remove(licenseId);
	}
	

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(final String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getSize() {
		return size;
	}

	public void setSize(final String size) {
		this.size = size;
	}

	public static String generateGAVC(final String groupId, final String artifactId, final String version, final String classifier, final String extension) {
		final StringBuilder sb = new StringBuilder();
		sb.append(groupId);
		sb.append(":");
		sb.append(artifactId);
		sb.append(":");
		sb.append(version);
        sb.append(":");
        sb.append(classifier);
        sb.append(":");
        sb.append(extension);
		
		return sb.toString();
	}
	
}
