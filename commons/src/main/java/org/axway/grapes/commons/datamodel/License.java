package org.axway.grapes.commons.datamodel;

/**
 * License Model Class
 *
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
	
	private boolean approved = false;

    // This field is never taken into account by the server
    // it provides an extra information to clients to warn them if the licenses exist
    // in the database of the server
    private boolean unknown = false;


    protected License() {
		// Should only be instantiated via the DataModelObjectFactory
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

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(final boolean approved) {
		this.approved = approved;
	}

    public boolean isUnknown() {
        return unknown;
    }

    public void setUnknown(boolean unknown) {
        this.unknown = unknown;
    }
	
	/**
	 * Checks if the dependency is the same than an other one.
	 * 
	 * @param obj Object
	 * @return <tt>true</tt> only if artifact/scope are the same in both.
	 */
	@Override
	public boolean equals(final Object obj){
		if(obj instanceof License){
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

	@Override
	public String toString() {
		return "License{" +
				"name='" + name + '\'' +
				", longName='" + longName + '\'' +
				", url='" + url + '\'' +
				", comments='" + comments + '\'' +
				", regexp='" + regexp + '\'' +
				", approved=" + approved +
				", unknown=" + unknown +
				'}';
	}
}
