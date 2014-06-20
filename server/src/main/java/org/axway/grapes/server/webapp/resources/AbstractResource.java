/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.views.View;
import org.axway.grapes.server.config.CommunityConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.*;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.ModelMapper;
import org.axway.grapes.server.db.RepositoryHandler;

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

    private final RepositoryHandler repositoryHandler;
    private final GrapesServerConfig grapesConfig;

    private final ModelMapper modelMapper;
    
    protected AbstractResource(final RepositoryHandler repoHandler, final String templateName, final GrapesServerConfig dmConfig) {
		super(templateName);
        this.grapesConfig = dmConfig;
        this.repositoryHandler = repoHandler;
        this.modelMapper = new ModelMapper(repoHandler);
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
     * Return a OrganizationHandler
     *
     * @return ArtifactHandler
     */
    protected OrganizationHandler getOrganizationHandler(){
        return new OrganizationHandler(repositoryHandler);
    }

    /**
     * Return a ModuleHandler
     *
     * @return ArtifactHandler
     */
    protected ModuleHandler getModuleHandler(){
        return new ModuleHandler(repositoryHandler);
    }

    /**
     * Return an ArtifactHandler
     *
     * @return ArtifactHandler
     */
    protected ArtifactHandler getArtifactHandler(){
        return new ArtifactHandler(repositoryHandler);
    }

    /**
     * Return a DependencyHandler
     *
     * @return DependencyHandler
     */
    protected DependencyHandler getDependencyHandler(){
        return new DependencyHandler(repositoryHandler);
    }

    /**
     * Return a LicenseHandler
     *
     * @return LicenseHandler
     */
    protected LicenseHandler getLicenseHandler(){
        return new LicenseHandler(repositoryHandler);
    }

    /**
     * Return a GraphsHandler
     *
     * @return LicenseHandler
     */
    protected GraphsHandler getGraphsHandler(final FiltersHolder filtersHolder){
        return new GraphsHandler(repositoryHandler,filtersHolder);
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
     * Returns model mapper for data-model conversion
     *
     * @return ModelMapper
     */
    protected ModelMapper getModelMapper(){
        return modelMapper;
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
