package org.axway.grapes.maven.resolver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * License Resolver
 *
 * <p>Handle license resolution</p>
 *
 * @author jdcoffre
 */
public class LicenseResolver {

    /**
     * Pom parser
     */
    private final MavenXpp3Reader reader = new MavenXpp3Reader();

    private final ArtifactResolver artifactResolver;

    private final Log logger;

    public LicenseResolver(final RepositorySystem repositorySystem, final ArtifactRepository localRepository, final Log log){
        this.artifactResolver = new ArtifactResolver(repositorySystem, localRepository, log );
        this.logger = log;
    }



    public List<License> resolve(final MavenProject project) throws MojoExecutionException {
        final List<License> licenses = new ArrayList<License>();
        licenses.addAll(project.getLicenses());

        if(licenses.isEmpty() && project.getParent() != null){
            final MavenProject parent = project.getParent();
            licenses.addAll(resolve(project, parent.getGroupId(), parent.getArtifactId(), parent.getVersion()));
        }

        return licenses;
    }

    /**
     * Resolve the licenses attached to an artifact (there is no transitive resolution here)
     *
     * @param project MavenProject
     * @param groupId String
     * @param artifactId String
     * @param version String
     * @return List<License>
     * @throws MojoExecutionException
     */
    public List<License> resolve(final MavenProject project, final String groupId, final String artifactId, final String version) throws MojoExecutionException {
        final Artifact modelArtifact = getModelArtifact(groupId, artifactId, version);
        return getLicenses(project, modelArtifact);
    }

    /**
     * Retrieve all the license (included parent's ones)
     *
     * @param project MavenProject
     * @param modelArtifact Artifact
     * @return List<License>
     * @throws MojoExecutionException
     */
    private List<License> getLicenses(final MavenProject project, final Artifact modelArtifact) throws MojoExecutionException{
        try{
            final List<License> licenses = new ArrayList<License>();
            artifactResolver.resolveArtifact(project, modelArtifact);

            final Model model = reader.read(new FileReader(modelArtifact.getFile()));
            licenses.addAll(model.getLicenses());

            if(model.getParent() != null){
                final Parent parent = model.getParent();
                final Artifact parentModel = getModelArtifact(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
                licenses.addAll(getLicenses(project, parentModel));
            }

            return licenses;
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Failed to read "  + modelArtifact.getFile() , e);
        } catch (XmlPullParserException e) {
            throw new MojoExecutionException("Failed to read "  + modelArtifact.getFile() , e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read "  + modelArtifact.getFile() , e);
        }
    }

    /**
     * Generate an model Artifact
     *
     * @param groupId String
     * @param artifactId String
     * @param version String
     * @return Artifact
     */
    private Artifact getModelArtifact(final String groupId, final String artifactId, final String version ){
        final DefaultArtifactHandler handler = new DefaultArtifactHandler();
        handler.setExtension("pom");

        final Artifact model = new DefaultArtifact(
                groupId,
                artifactId,
                version,
                null,
                "pom",
                null ,
                handler);

        return model;

    }

}
