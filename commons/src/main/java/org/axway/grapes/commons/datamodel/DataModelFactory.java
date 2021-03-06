package org.axway.grapes.commons.datamodel;

import org.axway.grapes.commons.exceptions.UnsupportedScopeException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Data Model Object Factory
 *
 * <p>Factory that handles Data Models Objects creation.</p>
 *
 * @author jdcoffre
 */
public final class DataModelFactory {

	private static final Logger LOG = Logger.getLogger(DataModelFactory.class.getName());

	// Utility class, though no constructor
	private DataModelFactory() {}


    /**
     * Generates an organization regarding the parameters.
     *
     * @param name String
     * @return Organization
     */
    public static Organization createOrganization(final String name){
        final Organization organization = new Organization();
        organization.setName(name);

        return organization;

    }


    /**
     * Generates a module regarding the parameters.
     *
     * @param name String
     * @param version String
     * @return Module
     */
    public static Module createModule(final String name,final String version){
        final Module module = new Module();

        module.setName(name);
        module.setVersion(version);
        module.setPromoted(false);

        return module;

    }

	/**
	 * Generates an artifact regarding the parameters.
	 *
	 * <P> <b>WARNING:</b> The parameters grId/arId/version should be filled!!! Only classifier and type are not mandatory.
	 *
	 * @param groupId String
	 * @param artifactId String
	 * @param version String
	 * @param classifier String
	 * @param type String
	 * @param extension String
	 * @return Artifact
	 */
	public static Artifact createArtifact(final String groupId, final String artifactId, final String version, final String classifier, final String type, final String extension){
		return createArtifact(groupId, artifactId, version, classifier, type, extension, null);
	}

	/**
	 * Generates an artifact regarding the parameters.
	 *
	 * <P> <b>WARNING:</b> The parameters grId/arId/version should be filled!!! Only classifier and type are not mandatory.
	 *
	 * @param groupId String
	 * @param artifactId String
	 * @param version String
	 * @param classifier String
	 * @param type String
	 * @param extension String
	 * @return Artifact
	 */
	public static Artifact createArtifact(final String groupId, final String artifactId, final String version, final String classifier, final String type, final String extension, final String origin){
		final Artifact artifact = new Artifact();

		artifact.setGroupId(groupId);
		artifact.setArtifactId(artifactId);
		artifact.setVersion(version);

		if(classifier != null){
			artifact.setClassifier(classifier);
		}

		if(type != null){
			artifact.setType(type);
		}

		if(extension != null){
			artifact.setExtension(extension);
		}

		artifact.setOrigin(origin == null ? "maven" : origin);

		return artifact;
	}

	/**
	 * Generates a License regarding the parameters.
	 *
	 * @param name String
	 * @param longName String
	 * @param comments String
	 * @param regexp String
	 * @param url String
	 * @return License
	 */
	public static License createLicense(final String name, final String longName, final String comments, final String regexp, final String url){
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
	 * @param scope Scope
	 * @return Dependency
	 */
	public static Dependency createDependency(final Artifact artifact, final Scope scope){
		final Dependency dependency = new Dependency();
		dependency.setTarget(artifact);
		dependency.setScope(scope);

		return dependency;
	}

	/**
	 * Generates a dependency regarding the parameters.
	 *
	 * @param artifact Artifact
	 * @param scope String
	 * @return Dependency
	 * @throws UnsupportedScopeException
	 */
	public static Dependency createDependency(final Artifact artifact, final String scope) throws UnsupportedScopeException{
        try{
            final Scope depScope = Scope.valueOf(scope.toUpperCase());
            return createDependency(artifact, depScope);
        }
        catch(IllegalArgumentException e){
			LOG.log(Level.SEVERE, String.format("Cannot identify scope for string %s. Details: %s", scope, e.getMessage()), e);
            throw new UnsupportedScopeException();
        }
	}

	/**
	 * Generates a PromotionDetails regarding the parameters.
	 *
	 * @param canBePromoted Boolean
	 * @param isSnapshot Boolean
	 * @param unPromotedDependencies List<String>
	 * @param doNotUseArtifacts List<Artifact>
	 * @return PromotionDetails
	 * @throws IOException
	 */
	public static PromotionDetails createPromotionDetails(final Boolean canBePromoted, final Boolean isSnapshot, final List<String> unPromotedDependencies, final List<Artifact> doNotUseArtifacts) throws IOException{
		try{
			final PromotionDetails promotionDetails = new PromotionDetails();

			promotionDetails.setPromotable(canBePromoted);
			promotionDetails.setSnapshot(isSnapshot);
			promotionDetails.setUnPromotedDependencies(unPromotedDependencies);
			promotionDetails.setDoNotUseArtifacts(doNotUseArtifacts);

			return promotionDetails;
		}
		catch(Exception e){
			throw new IOException(e);
		}
	}
	/**
	 * Generates a PromotionDetails regarding the parameters.
	 *
	 * @param commercialName String
	 * @param commercialVersion String
	 * @param releaseDate String
	 * @param dependencies List<String>
	 * @return Delivery
	 */
	public static Delivery createDelivery(final String commercialName, final String commercialVersion, final String releaseDate, final List<String> dependencies) {
		final Delivery delivery = new Delivery();

		delivery.setCommercialName(commercialName);
		delivery.setCommercialVersion(commercialVersion);
		delivery.setReleaseDate(releaseDate);
		delivery.setDependencies(dependencies);

		return delivery;
	}

	public static ArtifactPromotionStatus createArtifactPromotionStatus(final Boolean promoted, final String message) throws IOException{
		try{
			final ArtifactPromotionStatus promotionStatus = new ArtifactPromotionStatus();
			promotionStatus.setPromoted(promoted);
			promotionStatus.setMessage(message);
			return promotionStatus;
		}
		catch(Exception e){
			throw new IOException(e);
        }
	}
	
    public static ArtifactQuery createArtifactQuery(final String user, final int stage , final String name, final String sha256, final String type, final String location) throws IOException{
        try{
        	return new ArtifactQuery(user, stage, name, sha256, type, location);
        }
        catch(Exception e){
            throw new IOException(e);
        }
    }

	/**
	 * Generates a comment regarding the parameters.
	 *
	 * @param entityId - id of the commented entity
	 * @param entityType - type of the entity
	 * @param action - the action performed by the user
	 * @param commentedText - comment text
	 * @param user - comment left by
	 * @param date - date comment was created
	 * @return - comment entity
	 */
	public static Comment createComment(final String entityId,
										final String entityType,
										final String action,
										final String commentedText,
										final String user,
										final Date date) {

		final Comment comment = new Comment();
		comment.setEntityId(entityId);
		comment.setEntityType(entityType);
		comment.setAction(action);
		comment.setCommentText(commentedText);
		comment.setCommentedBy(user);
		comment.setCreatedDateTime(date);
		return comment;
	}
}
