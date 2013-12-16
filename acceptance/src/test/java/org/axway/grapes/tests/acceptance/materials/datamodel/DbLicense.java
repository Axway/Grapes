package org.axway.grapes.tests.acceptance.materials.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Database License
 * 
 * <p>Class that define the representation of licenses stored in the database.
 * The name (short name) is use as an ID. A database index is created on it.</p>
 * @author jdcoffre
 */
public class DbLicense {
	
	@Id
    @JsonProperty("_id")
	private String id;

	public static final String NAME_DB_FIELD = "name"; 
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
		
	public final String getId() {
		return id;
	}
	
	public final void setId(final String id) {
		this.id = id;
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
}
