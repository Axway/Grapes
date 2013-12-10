package org.axway.grapes.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.exceptions.UnsupportedScopeException;

/**
 * Data Mapper Implementation
 *
 * <p>Ensures the transformation from Maven data model to Grapes data model.</p>
 *
 * @author jdcoffre
 */
public class DataMapper {


    private final Log log;

    public DataMapper(final Log log) {
        this.log = log;
    }

    /**
     * Turn a maven project (Maven data model) into a module (Grapes data model)
     *
     * @param project
     * @return
     */
    public Module getModule(final MavenProject project) {
        // manage artifacts
        final String moduleName = generateModuleName(project);
        final org.axway.grapes.commons.datamodel.Module module = DataModelFactory.createModule(moduleName, project.getVersion());
        final org.axway.grapes.commons.datamodel.Artifact mainArtifact = getGrapesArtifact(project.getArtifact());
        module.addArtifact(mainArtifact);

        /* Trick to add project pom file as a module artifact*/
        final org.axway.grapes.commons.datamodel.Artifact pomArtifact = getGrapesArtifact(project.getArtifact());
        pomArtifact.setType("pom");
        pomArtifact.setExtension("xml");
        module.addArtifact(pomArtifact);

        for(Artifact artifact: project.getAttachedArtifacts()){
            module.addArtifact(getGrapesArtifact(artifact));
        }

        // manage dependencies
        for(Artifact dependency: project.getDependencyArtifacts()){
            module.addDependency(getGrapesDependency(dependency));
        }

        return module;
    }

    /**
     * Generate module's name from maven project
     *
     * @param project
     * @return String
     */
    private String generateModuleName(final MavenProject project) {
        final StringBuilder sb = new StringBuilder();
        sb.append(project.getArtifact().getGroupId());
        sb.append(":");
        sb.append(project.getArtifact().getArtifactId());
        return sb.toString();
    }

    /**
     * Generate a Grapes artifact from a Maven artifact
     *
     * @param artifact
     * @return
     */
    private org.axway.grapes.commons.datamodel.Artifact getGrapesArtifact(org.apache.maven.artifact.Artifact artifact) {
        final ArtifactHandler artifactHandler = artifact.getArtifactHandler();
        String extension = artifact.getType();

        if(artifactHandler != null){
            extension = artifactHandler.getExtension();
        }

        return DataModelFactory.createArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getClassifier(), artifact.getType(),extension);
    }

    /**
     * Generate a Grapes dependency from a Maven artifact
     *
     * @param dependency
     * @return
     */
    private org.axway.grapes.commons.datamodel.Dependency getGrapesDependency(org.apache.maven.artifact.Artifact dependency) {
        try {
            final org.axway.grapes.commons.datamodel.Artifact artifact = getGrapesArtifact(dependency);
            return DataModelFactory.createDependency(artifact, dependency.getScope());
        }catch (UnsupportedScopeException e){
            log.warn("The dependency " + dependency.getScope() + " has a scope that is not supported by Grapes, this dependency has been skipped.");
        }
        return null;
    }

}
