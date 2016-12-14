package org.axway.grapes.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axway.grapes.server.core.ErrorMessageHandler;

/**
 * @author jdcoffre
 * Configuration class of Grapes service class.
 * This configuration is loaded from a yaml file at the service start.
 * The absolute path of the configuration file is one of the mandatory parameter of the command line that start the service.
 * <p>
 *  An example of the expected configuration file in in the project resources.
 * <p>
 * INFO:
 * You can override configuration settings by passing special Java system properties when starting your service. Overrides must start with prefix dw., followed by the path to the configuration value being overridden.
 * For example, to override the tmpDirectory to use, you could start your service like this:
 * java -Ddw.tmpDirectory=/new/tmp/directory server my-config.json
 */
public class GrapesServerConfig extends Configuration{

    @Valid
    @JsonProperty
    private final CommunityConfig community = new CommunityConfig();

    @Valid
    @NotNull
    @JsonProperty
    private final DataBaseConfig database = new DataBaseConfig();
    
    @Valid
    @NotNull
    @JsonProperty
    private final GrapesEmailConfig mailing = new GrapesEmailConfig();
    
    @Valid
    @NotNull
    @JsonProperty
    private File messageFile;
    
    @Valid
    @NotNull
    @JsonProperty
    private ArrayList<String> artifactValidationType;


	@Valid
    @JsonProperty
    private final String authenticationCachePolicy = "maximumSize=10000, expireAfterAccess=10m";
	
	private ErrorMessageHandler messageHandler;
	
    private boolean maintenanceModeActif = false;

    public DataBaseConfig getDataBaseConfig() {
		return database;
	}
    
    public GrapesEmailConfig getGrapesEmailConfig() {
        return mailing;
    }
	
	public File getMessageFile(){
		return messageFile;
	}

	public List<String> getArtifactValidationType() {
		return artifactValidationType;
	}	
	/**
	 * Returns the complete Grapes root URL
	 * 
	 * @return String
	 */
	public String getUrl(){
		final StringBuilder sb = new StringBuilder();
		sb.append("http://");
		sb.append(getHttpConfiguration().getBindHost().get());
		sb.append(":");
		sb.append(getHttpConfiguration().getPort());
		
		return sb.toString();
	}

	public String getAuthenticationCachePolicy() {
		return authenticationCachePolicy;
	}

    public boolean isInMaintenance() {
        return maintenanceModeActif;
    }

    public void setMaintenanceMode(final boolean maintenanceMode) {
        this.maintenanceModeActif = maintenanceMode;
    }

    public CommunityConfig getCommunityConfiguration() {
        return community;
    }
}
