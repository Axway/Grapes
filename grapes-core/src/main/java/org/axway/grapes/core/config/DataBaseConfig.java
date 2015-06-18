package org.axway.grapes.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Database configuration
 *
 * <p>Handles the database configuration of Grapes server. The Grapes server will use the targeted
 * database to store the dependencies/licenses information</p>
 *
 * @author jdcoffre
 */
public class DataBaseConfig {


	@Valid
    @NotNull
    @JsonProperty
    private String host;
	
	@Valid
    @NotNull
    @JsonProperty
    private int port;
	
	@Valid
    @JsonProperty
    private String user;
	
	@Valid
    @JsonProperty
    private String pwd;
	
	@Valid
    @NotNull
    @JsonProperty
    private String datastore;
	
	@Valid
    @NotNull
    @JsonProperty
    private String dbsystem;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public char[] getPwd() {
		return pwd.toCharArray();
	}

	public String getDatastore() {
		return datastore;
	}	

    public String getDbsystem() {
        return dbsystem;
    }    
}
