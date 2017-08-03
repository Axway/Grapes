package org.axway.grapes.commons.datamodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple model for passing promotion errors between server and client.
 */
public class PromotionEvaluationReport {

    //
    //  By passing a simple set of error messages, further validation don't
    // require updating the client modules as well.
    //
    private Set<String> errors = new HashSet<>();

    private boolean promotable = false;

    public void addError(String error) {
        this.errors.add(error);
        promotable = false;
    }

    public boolean isPromotable() {
        return promotable;
    }

    public Set<String> getErrors() {
        return Collections.unmodifiableSet(errors);
    }
}
