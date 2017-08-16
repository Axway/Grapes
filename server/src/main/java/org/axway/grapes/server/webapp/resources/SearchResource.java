package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbSearch;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Search resource
 * <p>This server resource handles all the request about searching modules and.or artifacts.</p>
 */

@Path(ServerAPI.SEARCH_RESOURCE)
public class SearchResource extends AbstractResource {

    public SearchResource(RepositoryHandler repoHandler, GrapesServerConfig dmConfig) {
        super(repoHandler, "", dmConfig);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{searchWord}")
    public Response getSearchResult(@PathParam("searchWord") final String searchWord, @Context final UriInfo uriInfo) {

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());
        DbSearch result = getSearchHandler().getSearchResult(searchWord, filters);
        return Response.ok(result).build();
    }
}
