package org.axway.grapes.maven.converter;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.maven.converter.GrapesTranslator;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GrapesTranslatorTest {

    @Test
    public void generateModule(){
        final String mainArtifactGid = "org.axway.grapes.test";
        final String mainArtifactId = "mainArtifactId";
        final String moduleVersion = "1.1.2-3";

        final org.apache.maven.artifact.Artifact artifact = mock(org.apache.maven.artifact.Artifact.class);
        when(artifact.getGroupId()).thenReturn(mainArtifactGid);
        when(artifact.getArtifactId()).thenReturn(mainArtifactId);

        final MavenProject project = mock(MavenProject.class);
        when(project.getArtifact()).thenReturn(artifact);
        when(project.getVersion()).thenReturn(moduleVersion);

        final Module module = GrapesTranslator.getGrapesModule(project);
        assertEquals(mainArtifactGid + ":" + mainArtifactId, module.getName());
        assertEquals(moduleVersion, module.getVersion());
    }

    @Test
    public void generateArtifact(){
        final String groupId = "org.axway.grapes.test" ;
        final String artifactId = "artifactId" ;
        final String version = "1.0.0" ;
        final String classifier = "linux" ;
        final String type = "test-jar" ;
        final String extension = "jar" ;

        final DefaultArtifactHandler handler = new DefaultArtifactHandler();
        handler.setExtension(extension);

        final org.apache.maven.artifact.Artifact mavenArtifact = new DefaultArtifact(
                groupId,
                artifactId,
                version,
                "COMPILE",
                type,
                classifier ,
                handler);

        final Artifact artifact = GrapesTranslator.getGrapesArtifact(mavenArtifact);
        assertEquals(groupId, artifact.getGroupId());
        assertEquals(artifactId, artifact.getArtifactId());
        assertEquals(classifier, artifact.getClassifier());
        assertEquals(version, artifact.getVersion());
        assertEquals(type, artifact.getType());
        assertEquals(extension, artifact.getExtension());

    }

    @Test
    public void generateArtifactWithVersionRange() throws InvalidVersionSpecificationException {
        final String groupId = "org.axway.grapes.test" ;
        final String artifactId = "artifactId" ;
        final VersionRange versionRange = VersionRange.createFromVersionSpec("[1.0,)");
        final String classifier = "linux" ;
        final String type = "test-jar" ;
        final String extension = "jar" ;

        final DefaultArtifactHandler handler = new DefaultArtifactHandler();
        handler.setExtension(extension);

        final org.apache.maven.artifact.Artifact mavenArtifact = new DefaultArtifact(
                groupId,
                artifactId,
                versionRange,
                "COMPILE",
                type,
                classifier ,
                handler);

        final Artifact artifact = GrapesTranslator.getGrapesArtifact(mavenArtifact);
        assertEquals(groupId, artifact.getGroupId());
        assertEquals(artifactId, artifact.getArtifactId());
        assertEquals(classifier, artifact.getClassifier());
        assertEquals("1.0", artifact.getVersion());
        assertEquals(type, artifact.getType());
        assertEquals(extension, artifact.getExtension());
    }

    @Test
    public void generateDependency() throws MojoExecutionException {
        final String groupId = "org.axway.grapes.test" ;
        final String artifactId = "artifactId" ;
        final String version = "1.0.0" ;
        final String classifier = "linux" ;
        final String type = "test-jar" ;
        final String extension = "jar" ;
        final String scope = "TEST" ;

        final DefaultArtifactHandler handler = new DefaultArtifactHandler();
        handler.setExtension(extension);

        final org.apache.maven.artifact.Artifact mavenArtifact = new DefaultArtifact(
                groupId,
                artifactId,
                version,
                "COMPILE",
                type,
                classifier ,
                handler);

        final Dependency dependency = GrapesTranslator.getGrapesDependency(mavenArtifact, scope);
        assertEquals(groupId, dependency.getTarget().getGroupId());
        assertEquals(artifactId, dependency.getTarget().getArtifactId());
        assertEquals(classifier, dependency.getTarget().getClassifier());
        assertEquals(version, dependency.getTarget().getVersion());
        assertEquals(type, dependency.getTarget().getType());
        assertEquals(extension, dependency.getTarget().getExtension());
        assertEquals(scope, dependency.getScope().toString());

    }
}
