package org.axway.grapes.core.service;


import org.axway.grapes.core.version.IncomparableException;
import org.axway.grapes.core.version.NotHandledVersionException;
import org.axway.grapes.model.datamodel.Artifact;

import java.util.Collection;

/**
 * Created by jennifer on 4/24/15.
 */
public interface VersionsService {
    boolean isUpToDate(Artifact artifact);

   String getLastRelease(Collection<String> versions) throws NotHandledVersionException, IncomparableException;

   String getLastVersion(Collection<String> versions) throws NotHandledVersionException, IncomparableException;
    String getLastVersion(Artifact artifact,boolean release);
}
