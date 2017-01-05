/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.axway.grapes.server.webapp.resources;

import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.commons.utils.JsonUtils;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract resource
 * 
 * <p>Provide documentation and repository management support. This class should be extended by all the server resources.</p>
 * 
 * @author jdcoffre
 */
public abstract class AbstractResource extends View {

    private final RepositoryHandler repositoryHandler;
    private final ServiceHandler serviceHandler;
    private final GrapesServerConfig grapesConfig;

    private final ModelMapper modelMapper;
    
    protected AbstractResource(final RepositoryHandler repoHandler, final ServiceHandler serviceHandler, final String templateName, final GrapesServerConfig dmConfig) {
		super(templateName);
        this.grapesConfig = dmConfig;
        this.repositoryHandler = repoHandler;
        this.serviceHandler = serviceHandler;
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
     * @return OrganizationHandler
     */
    protected OrganizationHandler getOrganizationHandler(){
        return new OrganizationHandler(repositoryHandler);
    }

    /**
     * Return a ProductHandler
     *
     * @return ProductHandler
     */
    protected ProductHandler getProductHandler(){
        return new ProductHandler(repositoryHandler);
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

    /**
     * Returns an empty model of a Organization in Json
     *
     * @return String
     * @throws IOException
     */
    public String getOrganizationJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createOrganization(""));
    }

    /**
     * Returns an empty model of a Module in Json
     *
     * @return String
     * @throws IOException
     */
    public String getModuleJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createModule("", ""));
    }

    /**
     * Returns an empty model of an Artifact in Json
     *
     * @return String
     * @throws IOException
     */
    public String getArtifactJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createArtifact("", "", "", "", "", ""));
    }

    /**
     * Returns an empty model of a Message to check Promotion status 
     *
     * @return String
     * @throws IOException
     */
    public String getArtifactPromtotionInputMessage() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createArtifactQuery("", 0, "", "", ""));
    }
    
    /**
     * Returns an empty model of a Dependency in Json
     *
     * @return String
     * @throws IOException
     */
    public String getDependencyJsonModel() throws IOException {
        final Artifact artifact = DataModelFactory.createArtifact("","","","","","","");
        return JsonUtils.serialize(DataModelFactory.createDependency(artifact, Scope.COMPILE));
    }

    /**
     * Returns an empty model of a License in Json
     *
     * @return String
     * @throws IOException
     */
    public String getLicenseJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createLicense("","","","",""));
    }
    /**
     * Returns an empty Promotion details in Json
     *
     * @return String
     * @throws IOException
     */
    public String getPromotionDetailsJsonModel() throws IOException {
        return JsonUtils.serialize(DataModelFactory.createPromotionDetails(false,false,new ArrayList<String>(),new ArrayList<Artifact>()));
    }

    /**
     * Returns the comma separated list of available scopes
     *
     * @return String
     */
     public String getScopes() {
        final StringBuilder sb = new StringBuilder();
        for (Scope scope : Scope.values()) {
            sb.append(scope);
            sb.append(", ");
        }
        String scopes = sb.toString().trim();
        return scopes.substring(0, scopes.length() - 1);
    }
     /**
      * Returns an empty Delivery details in Json
      *
      * @return String
      * @throws IOException
      */
     public String getDeliveryJsonModel() throws IOException {
         return JsonUtils.serialize(DataModelFactory.createDelivery("", "", "", new ArrayList<String>()));
     }

	protected ServiceHandler getServiceHandler() {
		return serviceHandler;
	}
}
