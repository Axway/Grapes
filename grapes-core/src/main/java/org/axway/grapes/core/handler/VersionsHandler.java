package org.axway.grapes.core.handler;

import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.service.ArtifactService;
import org.axway.grapes.core.service.VersionsService;
import org.axway.grapes.core.version.IncomparableException;
import org.axway.grapes.core.version.NotHandledVersionException;
import org.axway.grapes.core.version.Version;
import org.axway.grapes.model.datamodel.Artifact;
import org.wisdom.api.annotations.Service;

import java.util.Collection;
import java.util.List;

/**
 * Created by jennifer on 4/28/15.
 */
@Service
public class VersionsHandler implements VersionsService {

    @Requires(optional = true)
    private ArtifactService artifactService;

    public ArtifactService getArtifactService() {
        return artifactService;
    }

    public void setArtifactService(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @Override
    public boolean isUpToDate(Artifact artifact) {
        final List<String> versions = artifactService.getArtifactVersions(artifact);
        final String currentVersion = artifact.getVersion();
        try {
            final String lastDevVersion = getLastVersion(versions);
            final String lastReleaseVersion = getLastRelease(versions);
            return currentVersion.equals(lastDevVersion) || currentVersion.equals(lastReleaseVersion);
        } catch (Exception e) {
            for (String version : versions) {
                if (version.compareTo(currentVersion) > 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public String getLastRelease(final Collection<String> versions)
            throws NotHandledVersionException, IncomparableException {
        Version lastRelease = null;
        for (String version : versions) {
            final Version testedVersion = new Version(version);
            if (testedVersion.isRelease()) {
                if (lastRelease == null) {
                    lastRelease = testedVersion;
                } else if (lastRelease.compare(testedVersion) < 0) {
                    lastRelease = testedVersion;
                }
            }
        }
        if (lastRelease == null) {
            return null;
        }
        return lastRelease.toString();
    }

    public String getLastVersion(Artifact artifact,boolean release) {
        System.out.println("did I make it to here?");
        final List<String> versions = artifactService.getArtifactVersions(artifact);
        final String currentVersion = artifact.getVersion();
        try {
            final String lastDevVersion = getLastVersion(versions);
            final String lastReleaseVersion = getLastRelease(versions);
            if (release){return lastReleaseVersion;}
            else{ return lastDevVersion;}

        } catch (Exception e) {
            return "what happened?";
        }
    }
    public String getLastVersion(final Collection<String> versions)
            throws NotHandledVersionException, IncomparableException {
        Version lastVersion = null;
        for (String version : versions) {
            final Version testedVersion = new Version(version);
            if (lastVersion == null) {
                lastVersion = testedVersion;
            } else if (lastVersion.compare(testedVersion) < 0) {
                lastVersion = testedVersion;
            }
        }
        if (lastVersion == null) {
            return null;
        }
        return lastVersion.toString();
    }
}
