/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.views.View;
import org.axway.grapes.server.config.CommunityConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.webapp.RequestHandler;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Abstract resource
 * 
 * <p>Provide documentation and repository management support. This class should be extended by all the server resources.</p>
 * 
 * @author jdcoffre
 */
public abstract class AbstractResource extends View{

    private final RequestHandler requestHandler;
    private final GrapesServerConfig grapesConfig;
    
    protected AbstractResource(final RepositoryHandler repoHandler, final String templateName, final GrapesServerConfig dmConfig) {
		super(templateName);
        this.grapesConfig = dmConfig;
        this.requestHandler = new RequestHandler(repoHandler, dmConfig);
	}
    
    /**
	 * Provide the documentation of the LicenseResource when the server got a request GET <dm_url>/<resourceName>
	 * 
	 * @return Response The documentation in HTML
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getDocumentation(){
		return Response.ok(this).build();
	}

    /**
     * Return the requestHandler dedicated to the resource
     *
     * @return RequestHandler
     */
    protected RequestHandler getRequestHandler(){
        return requestHandler;
    }

    /**
     * Return Grapes configuration
     *
     * @return GrapesServerConfig
     */
    protected GrapesServerConfig getConfig(){
        return grapesConfig;
    }
    
    /**
     * Return the version of the application
     * 
     * @return String
     */
    public String getProgramVersion(){
		return getClass().getPackage().getImplementationVersion();
	}


    /**
     * Return the issue-tracker url configured in the server configuration file (null if empty)
     *
     * @return String
     */
    public String getIssueTrackerUrl(){
        final CommunityConfig communityConfig = getConfig().getCommunityConfiguration();
        if(communityConfig == null){
            return null;
        }
        return communityConfig.getIssueTracker();
    }

    /**
     * Return the online documentation url configured in the server configuration file (null if empty)
     *
     * @return String
     */
    public String getOnlineDocumentation(){
        final CommunityConfig communityConfig = getConfig().getCommunityConfiguration();
        if(communityConfig == null){
            return null;
        }
        return communityConfig.getOnlineHelp();
    }
}
