package org.axway.grapes.core.webapi.resources;

import org.axway.grapes.model.api.ServerAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Path;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;

/**
 * Created by jennifer on 4/28/15.
 */
@Controller
@Path(ServerAPI.WEBAPP_RESOURCE)
public class WebAppController extends DefaultController {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactController.class);

    @View("layoutpage")
    Template webapp2;

    @View("webapp/organization")
    Template organizationView;

    @View("webapp/product")
    Template productView;

    @View("webapp/license")
    Template licenseView;

    @View("webapp/module")
    Template moduleView;

    @View("webapp/artifact")
    Template artifactView;

    @View("webApp2")
    Template webApp;





    /**
     * The action method returning the welcome page. It handles
     * HTTP GET request on the "/" URL.
     *
     * @return the welcome page
     */
    @Route(method = HttpMethod.GET, uri = "")
    public Result welcome() {

        return ok(render(webApp, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

    @Route(method = HttpMethod.GET, uri = "/organization")
    public Result getOrganization() {

        return ok(render(organizationView, "welcome", "Welcome to The New Grapes Under Construction!"));
    }
    @Route(method = HttpMethod.GET, uri = "/licenses")
    public Result getLicense() {

        return ok(render(licenseView, "welcome", "Welcome to The New Grapes Under Construction!"));
    }
    @Route(method = HttpMethod.GET, uri = "/products")
    public Result getProduct() {

        return ok(render(productView, "welcome", "Welcome to The New Grapes Under Construction!"));
    }
    @Route(method = HttpMethod.GET, uri = "/modules")
    public Result getModule() {

        return ok(render(moduleView, "welcome", "Welcome to The New Grapes Under Construction!"));
    }
    @Route(method = HttpMethod.GET, uri = "/artifacts")
    public Result getArtifact() {

        return ok(render(artifactView, "welcome", "Welcome to The New Grapes Under Construction!"));
    }

//    public WebAppResource(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
//        super(repoHandler, "webApp.thl.html", config);
//    }
@Route(method = HttpMethod.GET, uri = "/webapp")
public Result welcome2() {

    return ok(render(webapp2, "welcome", "Welcome to The New Grapes Under Construction!"));
}

}
