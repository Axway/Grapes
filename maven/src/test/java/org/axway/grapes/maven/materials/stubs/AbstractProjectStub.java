package org.axway.grapes.maven.materials.stubs;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Build;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.axway.grapes.commons.datamodel.Scope;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;


public abstract class AbstractProjectStub extends MavenProjectStub {

    private final Build build;

    private final Artifact artifact;

    private final License license;

    public AbstractProjectStub(){
        File testDir = new File( PlexusTestCase.getBasedir() + "/src/test/resources/materials/" + getProjectPath() + "/" );
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model;

        try
        {
            File pomFile = new File( testDir, "pom.xml" );
            // TODO: Once plexus-utils has been bumped to 1.4.4, use ReaderFactory.newXmlReader()
            model = pomReader.read( new InputStreamReader( new FileInputStream( pomFile ), "UTF-8" ) );
            setModel( model );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        /* Setup project */
        setGroupId( model.getGroupId() );
        setArtifactId( model.getArtifactId() );
        setVersion( model.getVersion() );
        setName( model.getName() );
        setUrl( model.getUrl() );
        setPackaging( model.getPackaging() );

        /* Setup artifact */
        artifact = new DefaultArtifact(
                model.getGroupId(),
                model.getArtifactId(),
                model.getVersion(),
                Scope.COMPILE.toString(),
                "jar",
                null,
                new DefaultArtifactHandler()
        );

        /* Setup License */
        license = new License();
        license.setName("License One");
        license.setDistribution("Grapes Corp.");
        license.setComments("Best license ever");
        license.setUrl("htt://somewhere.com");

        /* Setup build */
        build = new Build();
        Resource resource = new Resource();

        build.setFinalName( model.getArtifactId() );
        build.setDirectory( getBasedir().getAbsolutePath() + "/target" );

        build.setSourceDirectory( testDir + "/src/main/java" );
        resource.setDirectory(testDir + "/src/main/resources");
        build.setResources( Collections.singletonList(resource) );
        build.setOutputDirectory( getBasedir().getAbsolutePath() + "/target/classes" );

        build.setTestSourceDirectory( testDir + "/src/test/java" );
        resource = new Resource();
        resource.setDirectory( testDir + "/src/test/resources" );
        build.setTestResources( Collections.singletonList( resource ) );
        build.setTestOutputDirectory( getBasedir().getAbsolutePath() + "/target/test-classes" );

    }

    /**
     * @see org.apache.maven.project.MavenProject#getBasedir()
     */
    public File getBasedir() {
        File basedir = new File( PlexusTestCase.getBasedir(), "/target/test/unit/" + getProjectPath() + "/"  );

        if ( !basedir.exists() )
        {
            //noinspection ResultOfMethodCallIgnored
            basedir.mkdirs();
        }

        return basedir;
    }

    /**
     * @see org.apache.maven.project.MavenProject#getBuild()
     */
    public Build getBuild()
    {
        return build;
    }

    /**
     * @see org.apache.maven.project.MavenProject#getArtifact()
     */
    public Artifact getArtifact()
    {
        return artifact;
    }

    /**
     * @see org.apache.maven.project.MavenProject#getLicenses()
     */
    public List<License> getLicenses()
    {
        return Collections.singletonList(license);
    }

    /**
     * @return the project path from <code>src/test/resources/unit</code> directory
     */
    public abstract String getProjectPath();

    /**
     * @see org.apache.maven.project.MavenProject#getAttachedArtifacts()
     */
    public abstract List<Artifact> getAttachedArtifacts();

    /**
     * @see org.apache.maven.project.MavenProject#getModules()
     */
    public abstract List<String> getModules();


}
