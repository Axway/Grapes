package org.axway.grapes.core.webapi.resources;

import org.axway.grapes.model.api.ServerAPI;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;

/**
 * Created by jennifer on 4/28/15.
 */
@Controller
@Path(ServerAPI.SEQUOIA_RESOURCE)
public class SequoiaController extends DefaultController {
//    private static final Logger LOG = LoggerFactory.getLogger(Sequoia.class);
//
//
////    public Sequoia(final RepositoryHandler repoHandler, final GrapesServerConfig dmConfig) {
////        super(repoHandler, "Sequoia.ftl",dmConfig);
////    }
//
//
//    /**
//     * Perform a module dependency graph of the target and return the graph as a JSON
//     *
//     * @param moduleName
//     * @param moduleVersion
//     * @param uriInfo
//     * @return Result
//     */
//     @Route(method = HttpMethod.GET, uri = "/graph/{name}/{version}")
//    public Result getModuleGraph(@PathParameter("name") final String moduleName,
//                                   @PathParameter("version") final String moduleVersion,
//                                   @Context final UriInfo uriInfo){
//
//        LOG.info("Dependency Checker got a get module graph export request.");
//
//        if(moduleName == null || moduleVersion == null){
//            return Result.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
//        }
//
//        final FiltersHolder filters = new FiltersHolder();
//        filters.init(uriInfo.getQueryParameters());
//
//        final String moduleId = DbModule.generateID(moduleName, moduleVersion);
//        final AbstractGraph moduleGraph = getGraphsHandler(filters).getModuleGraph(moduleId);
//
//        return ok(moduleGraph).build();
//    }
//
//
//    /**
//     * Provide a module dependency tree
//     *
//     * @param moduleName
//     * @param moduleVersion
//     * @param uriInfo
//     * @return Result
//     */
//     @Route(method = HttpMethod.GET, uri ="/tree/{name}/{version}" )
//    public Result getModuleTree(@PathParameter("name") final String moduleName,
//                                  @PathParameter("version") final String moduleVersion,
//                                  @Context final UriInfo uriInfo){
//
//        LOG.info("Dependency Checker got a get groupid tree export request.");
//
//        if(moduleName == null || moduleVersion == null){
//            return Result.serverError().status(HttpStatus.NOT_ACCEPTABLE_406).build();
//        }
//
//        final FiltersHolder filters = new FiltersHolder();
//        filters.init(uriInfo.getQueryParameters());
//
//        final String moduleId = DbModule.generateID(moduleName, moduleVersion);
//        final TreeNode jsonTree = getGraphsHandler(filters).getModuleTree(moduleId);
//
//        return Result.ok(jsonTree).build();
//    }
}
