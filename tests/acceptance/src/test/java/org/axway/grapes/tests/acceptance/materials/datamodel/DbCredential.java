package org.axway.grapes.tests.acceptance.materials.datamodel;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Database Credential
 * 
 * <p>Class that represent the dependency manager credentials that are stored in the database. 
 * Passwords have to be encrypted.</p>
 * 
 * @author jdcoffre
 */
public class DbCredential {
	
	@Id
    @JsonProperty("_id")
	private String id;
	
	public static final String USER_FIELD = "user"; 
	private String user;
	
	public static final String PASSWORD_FIELD = "password"; 
	private String password;
	
	public static final String ROLE_FIELD = "role"; 
	private String role;
	
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}
	
	public void setUser(final String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public boolean isHealthy() {
        return user != null && password != null;
    }
}
