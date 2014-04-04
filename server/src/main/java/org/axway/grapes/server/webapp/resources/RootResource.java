package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("")
public class RootResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactResource.class);

    public RootResource(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
        super(repoHandler, "RootResource.ftl", config);
    }


    /**
     * Return the configured Corporate filters
     *
     * @return Response A JSON list of corporate filter
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(ServerAPI.GET_CORPORATE_FILTERS)
    public Response getCorporateFilters(){
        LOG.info("Got a get corporate filters request.");
        List<String> corporateFilters;

        try {
            corporateFilters = getConfig().getCorporateGroupIds();

        } catch (Exception e) {
            LOG.error("Failed retrieve the corporate filters.", e);
            return Response.serverError().status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        return Response.ok(corporateFilters).build();
    }

}