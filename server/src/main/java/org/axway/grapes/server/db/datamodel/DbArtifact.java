package org.axway.grapes.server.db.datamodel;

import org.apache.commons.lang3.StringUtils;
import org.axway.grapes.commons.datamodel.Artifact;
import org.jongo.marshall.jackson.oid.Id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Database Artifact
 * <p>
 * <p>Class that holds the representation of the artifacts stored in the database.
 * The gavc is used to identify the DbArtifacts Object. A database index is created on it.
 * </p>
 *
 * @author jdcoffre
 */
public class DbArtifact {

	public static final String DATA_MODEL_VERSION = "datamodelVersion";
	private String datamodelVersion = DbCollections.datamodelVersion;

	@Id
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

	public static final String ORIGIN_DB_FIELD = "origin";
	private String origin = "maven";

	public static final String PROMOTION_DB_FIELD = "promoted";
	private boolean promoted;

	public static final String LICENCES_DB_FIELD = "licenses";
	private List<String> licenses = new ArrayList<String>();

	public static final String DOWNLOAD_URL_DB_FIELD = "downloadUrl";
	private String downloadUrl = "";

	public static final String SIZE_DB_FIELD = "size";
	private String size = "";

	public static final String DO_NOT_USE = "doNotUse";
	private Boolean doNotUse = false;

    public static final String SHA_256 = "sha256";
    private String sha256 = "";
    
	public static final String DESCRIPTION_FIELD = "description";
	private String description = "";

    public static final String PROVIDER = "provider";
    private String provider = "";

    public static final String VALIDATION_TYPE_NOT_SUPPORTED_KEY = "VALIDATION_TYPE_NOT_SUPPORTED";
	public static final String QUERYING_NON_PUBLISHED_ARTIFACTS_ERROR_STAGE_UPLOAD_KEY = "QUERYING_NON_PUBLISHED_ARTIFACTS_ERROR_STAGE_UPLOAD";
	public static final String QUERYING_NON_PUBLISHED_ARTIFACTS_ERROR_STAGE_PUBLISH_KEY = "QUERYING_NON_PUBLISHED_ARTIFACTS_ERROR_STAGE_PUBLISH";
	public static final String ARTIFACT_NOT_PROMOTED_ERROR_MESSAGE_KEY = "ARTIFACT_NOT_PROMOTED_ERROR_MESSAGE";
	public static final String ARTIFACT_IS_PROMOTED_MESSAGE_KEY = "ARTIFACT_IS_PROMOTED_MESSAGE";
	public static final String ARTIFACT_NOTIFICATION_EMAIL_SUBJECT_KEY = "ARTIFACT_NOTIFICATION_EMAIL_SUBJECT";
	public static final String ARTIFACT_NOT_KNOWN_NOTIFICATION_EMAIL_BODY_KEY = "ARTIFACT_NOT_KNOWN_NOTIFICATION_EMAIL_BODY";
	public static final String ARTIFACT_NOT_PROMOTED_NOTIFICATION_EMAIL_BODY_KEY = "ARTIFACT_NOT_PROMOTED_NOTIFICATION_EMAIL_BODY";
	public static final String DEFAULT_ARTIFACT_IS_PROMOTED_MESSAGE = "Artifact is Promoted";
	
	public static final String DEFAULT_ARTIFACT_NOTIFICATION_EMAIL_SUBJECT = "Webliv publish attempt for %s";
	public static final String DEFAULT_ARTIFACT_NOT_KNOWN_NOTIFICATION_EMAIL_BODY = "Hello,<br><br>User %s is trying to publish <b>%s</b>.<br> Checksum is <b>%s</b>.<br> The artifact is not known.<br><br>Regards,<br>RD DevOps";
	public static final String DEFAULT_ARTIFACT_NOT_PROMOTED_NOTIFICATION_EMAIL_BODY = "Hello,<br><br>User %s is trying to publish <b>%s</b>.<br> Checksum is <b>%s</b>.<br> The artifact is not promoted.<br><br>Regards,<br>RD DevOps";

	public void setDataModelVersion(final String newVersion){
        this.datamodelVersion = newVersion;
    }

	public String getDataModelVersion() {
		return datamodelVersion;
	}

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

	public String getOrigin() {
		return origin;
	}

	public final void setOrigin(final String origin) {
		if (!(origin == null || "".equals(origin.trim()))) {
			this.origin = origin;
		}
		updateGavc();
	}

	public final void updateGavc() {
		gavc = generateGAVC(groupId, artifactId, version, classifier, extension);
	}

	public String getGavc() {
		return gavc;
	}

	public List<String> getLicenses() {
		return licenses;
	}

	public void setLicenses(final List<String> licenses) {
		this.licenses = licenses;
	}

	public void addLicense(final String licenseId) {
		if (!licenses.contains(licenseId)) {
			this.licenses.add(licenseId);
		}
	}

	public void addLicense(final DbLicense license) {
		addLicense(license.getName());
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

	public Boolean getDoNotUse() {
		return doNotUse;
	}

    public void setDoNotUse(final Boolean doNotUse) {
        this.doNotUse = doNotUse;
    }

    public String getSha256() {
		return this.sha256;
	}
	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(final String provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("GroupId: ");
		sb.append(groupId);
		sb.append(", ArtifactId: ");
		sb.append(artifactId);
		sb.append(", Version: ");
		sb.append(version);

		return sb.toString();
	}

	public static String generateGAVC(final String groupId, final String artifactId, final String version, final String classifier, final String extension) {
		return new StringBuilder()
				.append(groupId)
				.append(":")
				.append(artifactId)
				.append(":")
				.append(version)
				.append(":")
				.append(classifier)
				.append(":")
				.append(extension)
				.toString();
	}

	public static String generateGAVC(final Artifact artifact) {
		return generateGAVC(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getClassifier(), artifact.getExtension());
	}

	/**
	 *
	 * Verifies if the db artifact can be updated
	 *
	 * @param dbArtifact
	 * @return
	 */
	public boolean takeUpdatesFrom(final DbArtifact dbArtifact) {
		return this.equals(dbArtifact) &&
				// verify if they have the same type
				!(StringUtils.equals(
						StringUtils.trimToEmpty(this.getType()),
						StringUtils.trimToEmpty(dbArtifact.getType()))
						// verify if they have the same extension
						&& StringUtils.equals(
						StringUtils.trimToEmpty(this.getExtension()),
						StringUtils.trimToEmpty(dbArtifact.getExtension()))
						// verify if they have the same origin
						&& StringUtils.equals(
						StringUtils.trimToEmpty(this.getOrigin()),
						StringUtils.trimToEmpty(dbArtifact.getOrigin()))
						// verify if they have the same Download Url
						&& StringUtils.equals(
						StringUtils.trimToEmpty(this.getDownloadUrl()),
						StringUtils.trimToEmpty(dbArtifact.getDownloadUrl()))
						// verify if they have the same size
						&& StringUtils.equals(
						StringUtils.trimToEmpty(this.getSize()),
						StringUtils.trimToEmpty(dbArtifact.getSize()))
						// verify if they have the same provider
						&& StringUtils.equals(
						StringUtils.trimToEmpty(this.getProvider()),
						StringUtils.trimToEmpty(dbArtifact.getProvider()))
						// verify if they have the same Licenses
						&& (this.getLicenses() != null && Arrays.equals(
						this.getLicenses().toArray(),
						dbArtifact.getLicenses().toArray())));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DbArtifact)) {
			return false;
		}
		final DbArtifact dbArtifact = (DbArtifact) obj;
		return StringUtils.equals(
				StringUtils.trimToEmpty(this.getGroupId()),
				StringUtils.trimToEmpty(dbArtifact.getGroupId()))
				&& StringUtils.equals(
				StringUtils.trimToEmpty(this.getArtifactId()),
				StringUtils.trimToEmpty(dbArtifact.getArtifactId()))
				&& StringUtils.equals(
				StringUtils.trimToEmpty(this.getVersion()),
				StringUtils.trimToEmpty(dbArtifact.getVersion()))
				&& StringUtils.equals(
				StringUtils.trimToEmpty(this.getClassifier()),
				StringUtils.trimToEmpty(dbArtifact.getClassifier()));
	}

	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		hashCode = 31 * hashCode + (this.getGroupId() == null ? 0 : this.getGroupId().hashCode());
		hashCode = 31 * hashCode + (this.getArtifactId() == null ? 0 : this.getArtifactId().hashCode());
		hashCode = 31 * hashCode + (this.getVersion() == null ? 0 : this.getVersion().hashCode());
		hashCode = 31 * hashCode + (this.getClassifier() == null ? 0 : this.getClassifier().hashCode());
		return hashCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
