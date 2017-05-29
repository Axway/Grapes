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

    public Boolean canBePromoted;
    private List<String> dependencyProblems = new ArrayList<String>();

    public List<String> getDependencyProblems() {
        return dependencyProblems;
    }

    public void setDependencyProblems(List<String> dependencyProblems) {
        this.dependencyProblems = dependencyProblems;
    }
}
