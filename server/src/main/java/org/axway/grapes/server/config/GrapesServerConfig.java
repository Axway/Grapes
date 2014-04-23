package org.axway.grapes.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    private static final Logger LOG = LoggerFactory.getLogger(GrapesServerConfig.class);

    @Valid
    @JsonProperty
    private final CommunityConfig community = new CommunityConfig();

    @Valid
    @NotNull
    @JsonProperty
    private final DataBaseConfig database = new DataBaseConfig();
	
	@Valid
    @JsonProperty
    private static final String authenticationCachePolicy = "maximumSize=10000, expireAfterAccess=10m";

    private boolean maintenanceModeActif = false;

    public DataBaseConfig getDataBaseConfig() {
		return database;
	}

    /**
     * List of the groupIds that identify the internal production.
     * This information is stored in the database and modified using tasks
     */
    private final List<String> corporateGroupIds = new ArrayList<String>();

    /**
     * List of the credentials that are stored in Grapes database
     */
    private final List<DbCredential> credentials = new ArrayList<DbCredential>();
	
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

    /**
     * Loads the available corporate groupIds
     */
    public void loadGroupIds(final RepositoryHandler repoHandler)  {
        try{
            final List<String> dbCorporateGroupIds = repoHandler.getCorporateGroupIds();

            if(dbCorporateGroupIds != null){
                corporateGroupIds.clear();
                for(String groupId: dbCorporateGroupIds){
                    corporateGroupIds.add(groupId);
                }
            }

        }catch (Exception e){
            LOG.error("Failed to update the corporate groupid list.", e);
        }
    }

    public List<String> getCorporateGroupIds() {
        final List<String> groupIds = new ArrayList<String>();
        groupIds.addAll(corporateGroupIds);

        return groupIds;
    }

    /**
     * Loads the available credentials
     */
    public void loadCredentials(final RepositoryHandler repoHandler)  {
        try{
            final Iterable<DbCredential> dbCredentials = repoHandler.getCredentials();

            if(dbCredentials != null){
                credentials.clear();
                for(DbCredential credential: dbCredentials){
                    credentials.add(credential);
                }
            }


        }catch (Exception e){
            LOG.error("Failed to update the credentials.", e);
        }
    }

    public List<DbCredential> getCredentials() {
        return credentials;
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
