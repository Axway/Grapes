package org.axway.grapes.commons.datamodel;

import org.axway.grapes.commons.exceptions.UnsupportedScopeException;


/**
 * Data Model Object Factory
 * <p>
 * <p>Factory that handles Data Models Objects creation.</p>
 *
 * @author jdcoffre
 */
public final class DataModelFactory {


    // Utility class, though no constructor
    private DataModelFactory() {
    }


    /**
     * Generates an organization regarding the parameters.
     *
     * @param name String
     * @return Organization
     */
    public static Organization createOrganization(final String name) {
        final Organization organization = new Organization();
        organization.setName(name);

        return organization;

    }


    /**
     * Generates a module regarding the parameters.
     *
     * @param name    String
     * @param version String
     * @return Module
     */
    public static Module createModule(final String name, final String version) {
        final Module module = new Module();

        module.setName(name);
        module.setVersion(version);
        module.setPromoted(false);

        return module;

    }

    /**
     * Generates an artifact regarding the parameters.
     * <p>
     * <P> <b>WARNING:</b> The parameters grId/arId/version should be filled!!! Only classifier and type are not mandatory.
     *
     * @param groupId    String
     * @param artifactId String
     * @param version    String
     * @param classifier String
     * @param type       String
     * @param extension  String
     * @return Artifact
     */
    public static Artifact createArtifact(final String groupId, final String artifactId, final String version, final String classifier, final String type, final String extension) {
        final Artifact artifact = new Artifact();

        artifact.setGroupId(groupId);
        artifact.setArtifactId(artifactId);
        artifact.setVersion(version);

        if (classifier != null) {
            artifact.setClassifier(classifier);
        }

        if (type != null) {
            artifact.setType(type);
        }

        if (extension != null) {
            artifact.setExtension(extension);
        }

        return artifact;
    }

    /**
     * Generates a License regarding the parameters.
     *
     * @param name     String
     * @param longName String
     * @param comments String
     * @param regexp   String
     * @param url      String
     * @return License
     */
    public static License createLicense(final String name, final String longName, final String comments, final String regexp, final String url) {
        final License license = new License();

        license.setName(name);
        license.setLongName(longName);
        license.setComments(comments);
        license.setRegexp(regexp);
        license.setUrl(url);

        return license;
    }

    /**
     * Generates a dependency regarding the parameters.
     *
     * @param artifact Artifact
     * @param scope    Scope
     * @return Dependency
     */
    public static Dependency createDependency(final Artifact artifact, final Scope scope) {
        final Dependency dependency = new Dependency();
        dependency.setTarget(artifact);
        dependency.setScope(scope);

        return dependency;
    }

    /**
     * Generates a dependency regarding the parameters.
     *
     * @param artifact Artifact
     * @param scope    String
     * @return Dependency
     * @throws UnsupportedScopeException
     */
    public static Dependency createDependency(final Artifact artifact, final String scope) throws UnsupportedScopeException {
        try {
            final Scope depScope = Scope.valueOf(scope.toUpperCase());
            return createDependency(artifact, depScope);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedScopeException();
        }
    }

}
