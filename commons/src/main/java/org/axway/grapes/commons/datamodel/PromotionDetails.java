package org.axway.grapes.commons.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Promotion Model Class
 * <p>
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class PromotionDetails {

    private Boolean canBePromoted;
    private Boolean isSnapshot;
    private List<String> unPromotedDependencies = new ArrayList<String>();
    private List<Artifact> doNotUseArtifacts = new ArrayList<Artifact>();

    public List<String> getUnPromotedDependencies() {
        return unPromotedDependencies;
    }

    public Boolean isPromotable() {
        return canBePromoted;
    }

    public void setPromotable(Boolean v) {
        this.canBePromoted = v;
    }

    public void setSnapshot(Boolean v) {
        this.isSnapshot = v;
    }

    public Boolean isSnapshot() {
        return isSnapshot;
    }

    public void setUnPromotedDependencies(List<String> unPromotedDependencies) {
        this.unPromotedDependencies = unPromotedDependencies;
    }

    public void setDoNotUseArtifacts(List<Artifact> doNotUseArtifacts) {
        this.doNotUseArtifacts = doNotUseArtifacts;
    }

    public List<Artifact> getDoNotUseArtifacts() {
        return doNotUseArtifacts;
    }
}
