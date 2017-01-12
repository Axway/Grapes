package org.axway.grapes.commons.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Artifact Model Class
 *
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class Artifact {

    private String artifactId;
    private String groupId 	= "";
    private String version	= "";
    private String classifier = "";
    private String type = "";
    private String extension = "";
    private String origin = "maven";
    private String sha256 = "";
    private String description = "";

    private boolean promoted = false;

    private String size;
    private String downloadUrl;
    private String provider;

    private List<String> licenses = new ArrayList<String>();

    protected Artifact() {
        // Should only be instantiated via the DataModelObjectFactory
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getSha256() {
		return this.sha256;
	}
	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}
    
    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(final boolean promoted) {
        this.promoted = promoted;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(final String classifier) {
        this.classifier = classifier;
    }

    public List<String> getLicenses() {
        return licenses;
    }

    public void setLicenses(final List<String> licenses) {
        this.licenses = licenses;
    }

    public void addLicense(final String licenseName){
        licenses.add(licenseName);
    }

    public String getSize() {
        return size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(final String provider) {
        this.provider = provider;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonIgnore
    public String getGavc(){
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

    /**
     * Checks if the artifact is the same than an other one.
     *
     * @param obj Object
     * @return <tt>true</tt> only if grId/arId/classifier/version are the same in both.
     */
    @Override
    public boolean equals(final Object obj){
        if(obj instanceof Artifact){
            return hashCode() == obj.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();

        sb.append(groupId);
        sb.append(artifactId);
        sb.append(version);
        sb.append(classifier);
        sb.append(type);
        sb.append(extension);
        sb.append(origin);

        return sb.toString().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(groupId);
        sb.append(":");
        sb.append(artifactId);
        sb.append(":");
        sb.append(version);
        if (!(classifier == null || "".equals(classifier.trim()))){
            sb.append(":");
            sb.append(classifier);
        }
        if (!(type == null || "".equals(type.trim()))){
            sb.append(":");
            sb.append(type);
        }
        if (!(extension == null || "".equals(extension.trim()))){
            sb.append(":");
            sb.append(extension);
        }
        sb.append(":");
        sb.append(origin);

        return sb.toString();
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
