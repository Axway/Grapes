package org.axway.grapes.maven.resolver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

/**
 * License Resolver
 *
 * <p>Handle artifact resolution</p>
 *
 * @author jdcoffre
 */
public class ArtifactResolver {

    /**
     * Pom parser
     */
    private final MavenXpp3Reader reader = new MavenXpp3Reader();

    private final RepositorySystem repositorySystem;

    private final ArtifactRepository localRepository;

    private final Log logger;

    public ArtifactResolver(final RepositorySystem repositorySystem, final ArtifactRepository localRepository, final Log log){
        this.repositorySystem = repositorySystem;
        this.localRepository = localRepository;
        this.logger = log;
    }

    /**
     * Resolve an artifact from repository
     *
     * @param project MavenProject
     * @param artifact Artifact
     * @throws org.apache.maven.plugin.MojoExecutionException
     */
    public void resolveArtifact(final MavenProject project, Artifact artifact) throws MojoExecutionException {
        logger.debug("Resolving artifact " + artifact.toString());

        final ArtifactResolutionRequest artifactRequest = new ArtifactResolutionRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setLocalRepository(localRepository);
        artifactRequest.setRemoteRepositories(project.getRemoteArtifactRepositories());

        final ArtifactResolutionResult resolutionResult = repositorySystem.resolve(artifactRequest);

        if (!resolutionResult.isSuccess()) {
            logger.debug("Failed to resolved " + artifact.toString() +" artifact.");
        }
    }

    /**
     * Resolve a dependency artifact
     *
     * @param project MavenProject
     * @param dependency dependency
     * @return Artifact
     */
    public Artifact resolveArtifact(final MavenProject project, final Dependency dependency) throws MojoExecutionException {
        // Manage version ranges
        String version = dependency.getVersion();
        try{
            if(!version.matches("[0-9.]*")){
                final VersionRange range = VersionRange.createFromVersionSpec(version);
                version = getArtifactVersion(range);
            }
        }
        catch (InvalidVersionSpecificationException e){
            throw new MojoExecutionException("Failed to handle version range of " + dependency.toString(), e);
        }

        final DefaultArtifactHandler handler = new DefaultArtifactHandler();
        handler.setExtension(dependency.getType());

        final Artifact artifact = new DefaultArtifact(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                version,
                null,
                dependency.getType(),
                dependency.getClassifier() ,
                handler);

        resolveArtifact(project, artifact);

        return artifact;
    }

    /**
     * Finds a version out of a range
     *
     * @param range VersionRange
     * @return String
     */
    public static String getArtifactVersion(final VersionRange range){
        if(range.getRecommendedVersion() != null){
            return range.getRecommendedVersion().toString();
        }

        if(range.hasRestrictions()){
            for(Restriction restriction : range.getRestrictions()){
                if(restriction.getLowerBound() != null){
                    return restriction.getLowerBound().toString();
                }
                if(restriction.getUpperBound() != null){
                    return restriction.getLowerBound().toString();
                }
            }
        }

        return range.toString();
    }
}
