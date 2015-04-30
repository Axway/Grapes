package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.Artifact;


import org.jongo.marshall.jackson.oid.Id;

/**
 * Database Artifact
 * <p>
 * <p>Class that holds the representation of the artifacts stored in the database.
 * The gavc is used to identify the DbArtifacts Object. A database index is created on org.axway.grapes.jongo.it.
 * </p>
 */
public class DbArtifact extends Artifact {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String gavc;


    public void setDataModelVersion(final String newVersion) {
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion() {
        return datamodelVersion;
    }

    public final void setGroupId(final String groupId) {
       super.setGroupId(groupId);
        updateGavc();
    }


    public final void setArtifactId(final String artifactId) {
        super.setArtifactId(artifactId);
        updateGavc();
    }


    public final void setClassifier(final String classifier) {
        super.setClassifier(classifier);
        updateGavc();
    }


    public final void setVersion(final String version) {
        super.setVersion(version);
        updateGavc();
    }

    public final void setExtension(final String extension) {
        super.setExtension(extension);
        updateGavc();
    }

    public final void updateGavc() {
        gavc = generateGAVC(getGroupId(), getArtifactId(), getVersion(), getClassifier(), getExtension());
    }

    public String getGavc() {
        return gavc;
    }





    @Override
    public String toString() {

        return "GroupId: " + getGroupId() + ", ArtifactId: " + getArtifactId() + ", Version: " + getVersion();
    }

    public static String generateGAVC(final String groupId, final String artifactId, final String version, final String classifier, final String extension) {

        return groupId + ":" + artifactId + ":" + version + ":" + classifier + ":" + extension;
    }

    public static String generateGAVC(final Artifact artifact) {
        return generateGAVC(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getClassifier(), artifact.getExtension());
    }

}
