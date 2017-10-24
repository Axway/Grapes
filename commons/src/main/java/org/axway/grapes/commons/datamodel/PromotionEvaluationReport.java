package org.axway.grapes.commons.datamodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple model for passing promotion errors between server and client.
 */
public class PromotionEvaluationReport {

    //
    //  By passing a simple set of error messages, further validations don't
    // require updating the client modules as well.
    //
    private Set<ReportMessage> messages = new HashSet<>();
    private boolean promotable = true;

    public void addMessage(final String message, final Tag tag) {
        messages.add(new ReportMessage(message, tag));
    }


    public boolean isPromotable() {
        return promotable;
    }

    public Set<ReportMessage> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    public void setPromotable(boolean promotable) {
        this.promotable = promotable;
    }
}
