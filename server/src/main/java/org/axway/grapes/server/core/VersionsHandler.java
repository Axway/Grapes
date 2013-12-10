package org.axway.grapes.server.core;

import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.NotHandledVersionException;
import org.axway.grapes.server.core.version.Version;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;

import java.util.Collection;
import java.util.List;

/**
 * Versions Handler
 *
 * <p>This class handles the versions retrieval, sort & the rules regarding version the update.</p>
 *
 * @author jdcoffre
 */
public class VersionsHandler {

    private final RepositoryHandler repoHandler;

    public VersionsHandler(final RepositoryHandler repoHandler) {
        this.repoHandler = repoHandler;
    }


    /**
     * Check if the current version match the last release or the last snapshot one
     *
     * @param artifact
     * @return boolean
     */
    public boolean isUpToDate(final DbArtifact artifact) {
        final List<String> versions = repoHandler.getArtifactVersions(artifact);
        final String currentVersion = artifact.getVersion();

        try{
            final String lastDevVersion = getLastVersion(versions);
            final String lastReleaseVersion = getLastRelease(versions);
            return currentVersion.equals(lastDevVersion) || currentVersion.equals(lastReleaseVersion);
        }
        catch (Exception e){
            for(String version: versions){
                if(version.compareTo(currentVersion) > 0){
                    return false;
                }
            }

            return true;
        }

    }


    /**
     * Find-out the last release version in a list of version (regarding Axway Conventions)
     *
     * @param versions
     * @return String
     * @throws NotHandledVersionException
     * @throws IncomparableException
     */
    public String getLastRelease(final Collection<String> versions) throws NotHandledVersionException, IncomparableException {
        Version lastRelease = null;

        for(String version: versions){
            final Version testedVersion = new Version(version);

            if(testedVersion.isRelease()){
                if(lastRelease == null){
                    lastRelease = testedVersion;
                }
                else if(lastRelease.compare(testedVersion) < 0){
                    lastRelease = testedVersion;
                }
            }

        }

        if(lastRelease == null){
            return null;
        }

        return lastRelease.toString();
    }

    /**
     * Find-out the last version in a list of version
     *
     * @param versions
     * @return String
     * @throws NotHandledVersionException
     * @throws IncomparableException
     */
    public String getLastVersion(final Collection<String> versions) throws NotHandledVersionException, IncomparableException {
        Version lastVersion = null;

        for(String version: versions){
            final Version testedVersion = new Version(version);

            if(lastVersion == null){
                lastVersion = testedVersion;
            }
            else if(lastVersion.compare(testedVersion) < 0){
                lastVersion = testedVersion;
            }

        }

        if(lastVersion == null){
            return null;
        }

        return lastVersion.toString();
    }
}
