package org.axway.grapes.commons.datamodel;


public class ReportMessage {
    private String body = "";
    private Tag tag = Tag.MINOR;

    public ReportMessage() {
    }

    public ReportMessage(final String msg, final Tag tag) {
        this.body = msg;
        this.tag = tag;
    }

    public void setTag(Tag theTag) {
        this.tag = theTag;
    }

    public void setBody(final String msg) {
        this.body = msg;
    }

    public Tag getTag() {
        return tag;
    }

    public String getBody() {
        return this.body;
    }
}
