package org.axway.grapes.maven;

import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.utils.JsonUtils;
import org.axway.grapes.maven.materials.stubs.SubSubModule21ProjectStub;
import org.axway.grapes.maven.utils.FileUtils;
import org.sonatype.aether.impl.internal.DefaultRepositorySystem;
import sun.awt.ModalityListener;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GenerateMojoTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();

    }

    /**
     * @throws Exception
     */
    public void testSimpleProjectWithOneModuleOneArtifactAndOneLicense() throws Exception {
        File pom = getTestFile( "src/test/resources/materials/simple-project/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        final GenerateMojo generateMojo = (GenerateMojo) lookupMojo( "generate", pom );
        final MavenProject simpleProject = (MavenProject) getVariableValueFromObject( generateMojo, "project" );

        /* Fake artifact resolution */
        final RepositorySystem repositorySystemMock = mock(RepositorySystem.class);
        final ArtifactResolutionResult result = mock(ArtifactResolutionResult.class);
        when(repositorySystemMock.resolve(any(ArtifactResolutionRequest.class))).thenReturn(result);
        when(result.isSuccess()).thenReturn(true);

        setVariableValueToObject(generateMojo, "repositorySystem", repositorySystemMock);
        /* End */

        // Runs Mojo on the project
        generateMojo.execute();

        // Get generated Grapes module
        final String serializedModule = FileUtils.read(new File(simpleProject.getBasedir(), "target/grapes/"+GrapesMavenPlugin.MODULE_JSON_FILE_NAME));
        final Module simpleProjectModule = JsonUtils.unserializeModule(serializedModule);

        // Checks
        assertEquals(simpleProject.getGroupId() + ":" + simpleProject.getArtifactId() , simpleProjectModule.getName());
        assertEquals(simpleProject.getVersion() , simpleProjectModule.getVersion());
        assertEquals(0 , simpleProjectModule.getSubmodules().size());
        assertEquals(2 , simpleProjectModule.getArtifacts().size());

        final Artifact grapesArtifact = simpleProjectModule.getArtifacts().iterator().next();
        assertEquals(simpleProject.getArtifact().getGroupId() , grapesArtifact.getGroupId());
        assertEquals(simpleProject.getArtifact().getArtifactId() , grapesArtifact.getArtifactId());
        assertEquals(simpleProject.getArtifact().getVersion() , grapesArtifact.getVersion());
        assertEquals(simpleProject.getLicenses().get(0).getName(), grapesArtifact.getLicenses().get(0));

    }

    /**
     * @throws Exception
     */
    public void testMultiModuleProject() throws Exception {
        /* Fake artifact resolution */
        final RepositorySystem repositorySystemMock = mock(RepositorySystem.class);
        final ArtifactResolutionResult result = mock(ArtifactResolutionResult.class);
        when(repositorySystemMock.resolve(any(ArtifactResolutionRequest.class))).thenReturn(result);
        when(result.isSuccess()).thenReturn(true);
        /* End */

        /* Runs Mojo on root project */
        File pom = getTestFile( "src/test/resources/materials/multi-module-project/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        GenerateMojo generateMojo = (GenerateMojo) lookupMojo( "generate", pom );
        final MavenProject multiModuleProject = (MavenProject) getVariableValueFromObject( generateMojo, "project" );
        setVariableValueToObject(generateMojo, "repositorySystem", repositorySystemMock);

        // Runs Mojo on the project
        generateMojo.execute();


        /* Runs Mojo on sub sub project 21 */
        pom = getTestFile( "src/test/resources/materials/multi-module-project/subModule2/subSubModule21/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        generateMojo = (GenerateMojo) lookupMojo( "generate", pom );
        MavenProject subProject = (MavenProject) getVariableValueFromObject( generateMojo, "project" );
        setVariableValueToObject(generateMojo, "repositorySystem", repositorySystemMock);

        // Runs Mojo on the project
        generateMojo.execute();


        /* Runs Mojo on sub project 1 */
        pom = getTestFile( "src/test/resources/materials/multi-module-project/subModule1/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        generateMojo = (GenerateMojo) lookupMojo( "generate", pom );
        subProject = (MavenProject) getVariableValueFromObject( generateMojo, "project" );
        setVariableValueToObject(generateMojo, "repositorySystem", repositorySystemMock);

        // Runs Mojo on the project
        generateMojo.execute();


        /* Runs Mojo on sub project 2 */
        pom = getTestFile( "src/test/resources/materials/multi-module-project/subModule2/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        generateMojo = (GenerateMojo) lookupMojo( "generate", pom );
        subProject = (MavenProject) getVariableValueFromObject(generateMojo, "project");
        setVariableValueToObject(generateMojo, "repositorySystem", repositorySystemMock);

        // Runs Mojo on the project
        generateMojo.execute();


        // Get generated Grapes module
        final String serializedModule = FileUtils.read(new File(multiModuleProject.getBasedir(), "target/grapes/"+GrapesMavenPlugin.MODULE_JSON_FILE_NAME));
        final Module multiModuleProjectModule = JsonUtils.unserializeModule(serializedModule);

        // Checks
        assertEquals(multiModuleProject.getGroupId() + ":" + multiModuleProject.getArtifactId() , multiModuleProjectModule.getName());
        assertEquals(3, multiModuleProjectModule.getArtifacts().size());
        assertEquals(2, multiModuleProjectModule.getSubmodules().size());

        Module subModule2 = null;
        for(Module subModule : multiModuleProjectModule.getSubmodules()){
            if(subModule.getName().equals("org.axway.grapes.maven.materials.multi:sub-module2-project")){
                subModule2 = subModule;
            }
        }

        assertNotNull(subModule2);
        assertEquals(1, subModule2.getSubmodules().size());

    }
}
