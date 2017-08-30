package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbSearch;
import org.axway.grapes.server.webapp.views.SearchView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * WebSearch resource
 * <p>This server resource handles all the request about searching modules and/or artifacts.</p>
 */
@Path(ServerAPI.SEARCH_RESOURCE)
public class WebSearchResource extends AbstractResource {

    public WebSearchResource(RepositoryHandler repoHandler, GrapesServerConfig dmConfig) {
        super(repoHandler, "Search.ftl", dmConfig);
    }

    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    @Path("/{searchWord}")
    public Response getSearchResult(@PathParam("searchWord") final String searchWord, @Context final UriInfo uriInfo) {

        // Check the length of the search word and if it contains spaces
        if(searchWord.length() < 3 || searchWord.indexOf(" ") != -1) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        SearchView view = new SearchView();

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        DbSearch result = getSearchHandler().getSearchResult(searchWord, filters);

        view.setSearchOj(result);
        return Response.ok(view).build();
    }
}
