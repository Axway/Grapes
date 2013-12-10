package org.axway.grapes.server.webapp.resources;

import com.sun.jersey.api.NotFoundException;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.graphs.TreeNode;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
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


    public Sequoia(final RepositoryHandler repoHandler, final GrapesServerConfig dmConfig) {
        super(repoHandler, "Sequoia.ftl",dmConfig);
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

        AbstractGraph moduleGraph;

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try{

            moduleGraph = getRequestHandler().getModuleGraph(moduleName, moduleVersion, filters);

        }
        catch(NotFoundException e){
            LOG.error(e.getMessage());
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }
        catch (Exception e) {
            LOG.error("Failed to get targeted module graph.", e);
            return Response.serverError().build();
        }

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

        TreeNode jsonTree;

        final FiltersHolder filters = new FiltersHolder(getConfig().getCorporateGroupIds());
        filters.init(uriInfo.getQueryParameters());

        try{
            jsonTree = getRequestHandler().getModuleTree(moduleName, moduleVersion, filters);
        }
        catch(NotFoundException e){
            LOG.error(e.getMessage());
            return Response.serverError().status(HttpStatus.NOT_FOUND_404).build();
        }
        catch (Exception e) {
            LOG.error("Failed to get targeted module tree.", e);
            return Response.serverError().build();
        }

        return Response.ok(jsonTree).build();
    }
}
