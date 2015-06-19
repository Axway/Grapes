package org.axway.grapes.core.webapi.resources;

import org.axway.grapes.model.api.ServerAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;

/**
 * Created by jennifer on 4/28/15.
 */
@Controller
@Path(ServerAPI.WEBAPP_RESOURCE)
public class WebAppController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactController.class);



    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {
        return ok( "Welcome to The New Grapes Under Construction!").html();
    }

//    public WebAppResource(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
//        super(repoHandler, "WebApp.ftl", config);
//    }
}
