package service;


import org.axway.grapes.model.datamodel.Artifact;

/**
 * Created by jennifer on 4/24/15.
 */
public interface VersionsService {
    boolean isUpToDate(Artifact artifact);

//    String getLastRelease(Collection<String> versions) throws NotHandledVersionException, IncomparableException;

//    String getLastVersion(Collection<String> versions) throws NotHandledVersionException, IncomparableException;
}
