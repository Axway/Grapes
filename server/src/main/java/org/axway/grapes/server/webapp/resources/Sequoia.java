package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.graphs.TreeNode;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Sequoia
 *
 * <p>This server resource handles dependencies rendering.</p>
 */
@Path(ServerAPI.SEQUOIA_RESOURCE)
public class Sequoia extends AbstractResource{

    private static final Logger LOG = LoggerFactory.getLogger(Sequoia.class);


    public Sequoia(final RepositoryHandler repoHandler, final ServiceHandler serviceHandler, final GrapesServerConfig dmConfig) {
    	
        super(repoHandler, serviceHandler, "Sequoia.ftl",dmConfig);

    }


    /**
     * Perform a module dependency graph of the target and return the graph as a JSON
     *
     * @param moduleName
     * @param moduleVersion
     * @param uriInfo
     * @return Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/graph/{name}/{version}")
    public Response getModuleGraph(@PathParam("name") final String moduleName,
                                   @PathParam("version") final String moduleVersion,
                                   @Context final UriInfo uriInfo){

        LOG.info("Dependency Checker got a get module graph export request.");

        if(moduleName == null || moduleVersion == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final String moduleId = DbModule.generateID(moduleName, moduleVersion);
        final AbstractGraph moduleGraph = getGraphsHandler(filters).getModuleGraph(moduleId);

        return Response.ok(moduleGraph).build();
    }


    /**
     * Provide a module dependency tree
     *
     * @param moduleName
     * @param moduleVersion
     * @param uriInfo
     * @return Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tree/{name}/{version}")
    public Response getModuleTree(@PathParam("name") final String moduleName,
                                   @PathParam("version") final String moduleVersion,
                                   @Context final UriInfo uriInfo){

        LOG.info("Dependency Checker got a get groupid tree export request.");

        if(moduleName == null || moduleVersion == null){
            return Response.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
        }

        final FiltersHolder filters = new FiltersHolder();
        filters.init(uriInfo.getQueryParameters());

        final String moduleId = DbModule.generateID(moduleName, moduleVersion);
        final TreeNode jsonTree = getGraphsHandler(filters).getModuleTree(moduleId);

        return Response.ok(jsonTree).build();
    }
}
