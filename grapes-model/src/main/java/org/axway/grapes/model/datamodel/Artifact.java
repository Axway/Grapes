package org.axway.grapes.model.datamodel;

//todo used by the plugin needs to stay the same, however the package name has changed?
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Artifact Model Class
 * <p>
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class Artifact {

    //this is the id in the jongo datamodel
    private String gavc;
    private String artifactId;
    private String groupId = "";
    private String version = "";
    private String classifier = "";
    private String type = "";
    private String extension = "";

    private boolean promoted = false;
    private Boolean doNotUse = false;

    private String size;
    private String downloadUrl;
    private String provider;

    private List<String> licenses = new ArrayList<String>();

    public Artifact() {
        // Should only be instantiated via the DataModelObjectFactory
    }

    //should only be used internally and not manually set
    protected void setGavc(String gavc) {
        this.gavc = gavc;
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

    public void addLicense(final String licenseName) {
        if (!getLicenses().contains(licenseName)) {
            getLicenses().add(licenseName);
        }
    }

    public void removeLicense(final String licenseId) {
        getLicenses().remove(licenseId);
    }

    public Boolean getDoNotUse() {
        return doNotUse;
    }

    public void setDoNotUse(final Boolean doNotUse) {
        this.doNotUse = doNotUse;
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


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonIgnore
    public String getGavc() {
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
    public boolean equals(final Object obj) {
        if (obj instanceof Artifact) {
            return hashCode() == obj.hashCode();
        }

        return false;
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

    public static String generateGAVC(final Artifact artifact) {
        return generateGAVC(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getClassifier(), artifact.getExtension());
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
        sb.append(":");
        sb.append(classifier);
        sb.append(":");
        sb.append(type);
        sb.append(":");
        sb.append(extension);

        return sb.toString();
    }

}
