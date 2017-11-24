package org.axway.grapes.server.db.datamodel;

import org.jongo.marshall.jackson.oid.Id;

/**
 * Database License
 * 
 * <p>Class that define the representation of licenses stored in the database.
 * The name (short name) is use as an ID. A database index is created on it.</p>
 * @author jdcoffre
 */
public class DbLicense {

    public static final String DATA_MODEL_VERSION = "DATAMODEL_VERSION";
    private String datamodelVersion = DbCollections.DATAMODEL_VERSION;

    @Id
	private String name = "";

	public static final String LONG_NAME_DB_FIELD = "longName"; 
	private String longName = "";

	public static final String URL_DB_FIELD = "url"; 
	private String url = "";

	public static final String COMMENTS_DB_FIELD = "comments"; 
	private String comments = "";
	
	public static final String REGEXP_DB_FIELD = "regexp"; 
	private String regexp = "";
	
	public static final String APPROVED_DB_FIELD = "approved"; 
	private Boolean approved = null;

    public void setDataModelVersion(final String newVersion){
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion(){
        return datamodelVersion;
    }
	
	public final String getName() {
		return name;
	}
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	public final String getLongName() {
		return longName;
	}
	
	public final void setLongName(final String longName) {
		this.longName = longName;
	}
	
	public final String getUrl() {
		return url;
	}
	
	public final void setUrl(final String url) {
		this.url = url;
	}
	
	public final String getComments() {
		return comments;
	}
	
	public final void setComments(final String comments) {
		this.comments = comments;
	}
	
	public final String getRegexp() {
		return regexp;
	}
	
	public final void setRegexp(final String regexp) {
		this.regexp = regexp;
	}
	
	public final Boolean isApproved() {
		return approved;
	}
	
	public final void setApproved(final Boolean approved) {
		this.approved = approved;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DbLicense dbLicense = (DbLicense) o;

		return name != null ? name.equals(dbLicense.name) : dbLicense.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
