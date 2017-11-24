package org.axway.grapes.server.core;

import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.Version;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Versions Handler
 *
 * <p>This class handles the versions retrieval, sort & the rules regarding version the update.</p>
 *
 * @author jdcoffre
 */
public class VersionsHandler {

    private final RepositoryHandler repoHandler;
    private static final Logger LOG = LoggerFactory.getLogger(VersionsHandler.class);

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

        final String lastDevVersion = getLastVersion(versions);
        final String lastReleaseVersion = getLastRelease(versions);

        if(lastDevVersion == null || lastReleaseVersion == null) {
            // Plain Text comparison against version "strings"
            for(final String version: versions){
                if(version.compareTo(currentVersion) > 0){
                    return false;
                }
            }
            return true;
        } else {
            return currentVersion.equals(lastDevVersion) || currentVersion.equals(lastReleaseVersion);
        }
    }


    /**
     * Find-out the last release version in a list of version (regarding Axway Conventions)
     *
     * @param versions
     * @return String
     * @throws IncomparableException
     */
    public String getLastRelease(final Collection<String> versions) {
        final List<Version> sorted = versions.stream()
                .filter(Version::isValid)               // filter invalid input values
                .map(Version::make)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Version::isRelease)
                .sorted((v1, v2) -> {
                    try {
                        return v1.compare(v2);
                    } catch (IncomparableException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());

        if(sorted.isEmpty()) {
            if(LOG.isWarnEnabled()) {
                LOG.warn(String.format("Cannot obtain last release from collection %s", versions.toString()));
            }
            return null;
        }

        return sorted.get(sorted.size() - 1).toString();
    }


    /**
     * Find-out the last version in a list of version
     *
     * @param versions
     * @return String
     * @throws IncomparableException
     */
    public String getLastVersion(final Collection<String> versions) {
        final List<Version> sorted = versions.stream()
                .filter(Version::isValid) // filter invalid input values
                .map(Version::make)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted((v1, v2) -> {
                    try {
                        return v1.compare(v2);
                    } catch (IncomparableException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());

        if(sorted.isEmpty()) {
            if(LOG.isWarnEnabled()) {
                LOG.warn(String.format("Cannot obtain last version from collection %s", versions.toString()));
            }
            return null;
        }

        return sorted.get(sorted.size() - 1).toString();
    }
}