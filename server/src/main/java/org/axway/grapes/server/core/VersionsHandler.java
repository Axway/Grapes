package org.axway.grapes.server.core;

import org.axway.grapes.server.core.version.IncomparableException;
import org.axway.grapes.server.core.version.NotHandledVersionException;
import org.axway.grapes.server.core.version.Version;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

//        try{
//            final String lastDevVersion = getLastVersion(versions);
//            final String lastReleaseVersion = getLastRelease(versions);
//            return currentVersion.equals(lastDevVersion) || currentVersion.equals(lastReleaseVersion);
//        }
//        catch (Exception e){
//            LOG.info("Some problem occured while fetching the Last version or Last release version" , e);
//            for(final String version: versions){
//                if(version.compareTo(currentVersion) > 0){
//                    return false;
//                }
//            }
//            return true;
//        }


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


//        catch (Exception e){
//            LOG.info("Some problem occurred while fetching the Last version or Last release version" , e);
//            for(final String version: versions){
//                if(version.compareTo(currentVersion) > 0){
//                    return false;
//                }
//            }
//            return true;
//        }


    }


    /**
     * Find-out the last release version in a list of version (regarding Axway Conventions)
     *
     * @param versions
     * @return String
     * @throws NotHandledVersionException
     * @throws IncomparableException
     */
    public String getLastRelease(final Collection<String> versions) {
        Version lastRelease = null;

        for(final String version: versions){
            if(versionIsAcceptable(version)) {
                try {
                    final Version testedVersion = new Version(version);

                    if (testedVersion.isRelease()) {
                        if (lastRelease == null) {
                            lastRelease = testedVersion;
                        } else if (lastRelease.compare(testedVersion) < 0) {
                            lastRelease = testedVersion;
                        }
                    }
                } catch(final NotHandledVersionException e) {
                    LOG.error("Protection should have been in place", e);
                } catch(final IncomparableException ie) {
                    LOG.error(String.format("Cannot compare latest release [%s]. Details %s", lastRelease, ie));
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
    public String getLastVersion(final Collection<String> versions) {
        Version lastVersion = null;

        for(final String version: versions){
            if(versionIsAcceptable(version)) {

                try {
                    final Version testedVersion = new Version(version);

                    if (lastVersion == null) {
                        lastVersion = testedVersion;
                    } else if (lastVersion.compare(testedVersion) < 0) {
                        lastVersion = testedVersion;
                    }
                } catch(final NotHandledVersionException e) {
                    LOG.error("Protection should have been in place", e);
                } catch(final IncomparableException ie) {
                    LOG.error(String.format("Cannot compare latest version [%s]. Details %s", lastVersion, ie));
                }
            }
        }

        if(lastVersion == null){
            return null;
        }

        return lastVersion.toString();
    }

    private boolean versionIsAcceptable(String version) {
        if(version == null) {
            return false;
        }

        try {
            new Version(version);
        } catch (NotHandledVersionException e) {
            LOG.warn(String.format("Unsupported version [%s] %s", version, e.getMessage() == null ? "" : e.getMessage() ));
            LOG.debug(e);
            return false;
        }

        return true;
    }
}
