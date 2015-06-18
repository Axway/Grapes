package org.axway.grapes.model.datamodel;

/**
 * License Model Class
 * <p>
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * @author jdcoffre
 */
public class License {

    private String name = "";
    private String longName = "";
    private String url = "";
    private String comments = "";
    private String regexp = "";
    private Boolean approved = false;
    //this used to not be stored in the database, however maybe it can be used to validate licenses?
    private Boolean unknown = false;

    public License() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(final String longName) {
        this.longName = longName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(final String regexp) {
        this.regexp = regexp;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(final Boolean approved) {
        this.approved = approved;
    }

    public Boolean isUnknown() {
        return unknown;
    }

    public void setUnknown(Boolean unknown) {
        this.unknown = unknown;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof License) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(longName);
        sb.append(url);
        sb.append(comments);
        sb.append(regexp);
        return sb.toString().hashCode();
    }
}
