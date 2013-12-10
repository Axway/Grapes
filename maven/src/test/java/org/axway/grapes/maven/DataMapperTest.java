package org.axway.grapes.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.Scope;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataMapperTest {

    private MavenProject project;
    private Artifact mainArtifact;
    private Artifact dependency;

    @Before
    public void init(){
        final ArtifactHandler artifactHandler = mock(ArtifactHandler.class);
        when(artifactHandler.getExtension()).thenReturn("jar");
        mainArtifact = new DefaultArtifact("groupId","artifactId","version","COMPILE","jar","classifier",artifactHandler);


        final ArtifactHandler artifactHandler2 = mock(ArtifactHandler.class);
        when(artifactHandler2.getExtension()).thenReturn("xml");
        dependency = new DefaultArtifact("groupId2","artifactId2","version2","TEST","pom","classifier2",artifactHandler2);

        final Set<Artifact> dependencies = new HashSet<Artifact>();
        dependencies.add(dependency);
        project = mock(MavenProject.class);
        when(project.getVersion()).thenReturn(mainArtifact.getVersion());
        when(project.getArtifact()).thenReturn(mainArtifact);
        when(project.getDependencyArtifacts()).thenReturn(dependencies);
    }

    @Test
    public void checkModule(){
        final DataMapper dataMapper = new DataMapper(mock(Log.class));
        final Module module = dataMapper.getModule(project);

        assertEquals(mainArtifact.getGroupId()+":"+mainArtifact.getArtifactId(), module.getName());
        assertEquals(mainArtifact.getVersion(), module.getVersion());


        assertEquals(2, module.getArtifacts().size());
        assertEquals(mainArtifact.getGroupId(), module.getArtifacts().iterator().next().getGroupId());
        assertEquals(mainArtifact.getArtifactId(), module.getArtifacts().iterator().next().getArtifactId());
        assertEquals(mainArtifact.getVersion(), module.getArtifacts().iterator().next().getVersion());
        assertEquals(mainArtifact.getClassifier(), module.getArtifacts().iterator().next().getClassifier());


        assertEquals(1, module.getDependencies().size());
        assertEquals(Scope.TEST, module.getDependencies().iterator().next().getScope());
        assertEquals(dependency.getGroupId(), module.getDependencies().iterator().next().getTarget().getGroupId());
        assertEquals(dependency.getArtifactId(), module.getDependencies().iterator().next().getTarget().getArtifactId());
        assertEquals(dependency.getVersion(), module.getDependencies().iterator().next().getTarget().getVersion());
        assertEquals(dependency.getType(), module.getDependencies().iterator().next().getTarget().getType());
        assertEquals(dependency.getClassifier(), module.getDependencies().iterator().next().getTarget().getClassifier());
        assertEquals("xml", module.getDependencies().iterator().next().getTarget().getExtension());
    }
}
