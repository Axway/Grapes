package org.axway.grapes.core.webapi.resources;
//todo this should go through the wisdom monitor?

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.security.Authenticated;
import org.wisdom.api.templates.Template;
import org.wisdom.monitor.service.MonitorExtension;

/**
 * Created by jennifer on 6/16/15.
 */
@Controller
public class CredentialController extends DefaultController implements MonitorExtension{

    private static final Logger LOG = LoggerFactory.getLogger(CredentialController.class);
    @View("webapp/credentials")
    Template managecredentials;

    @Authenticated("Monitor-Authenticator")
    @Route(method = HttpMethod.GET, uri = "/monitor/grapes/credentials")
    public Result manage() {
        return ok(render(managecredentials,"admin","true"));
    }

    @Authenticated("Monitor-Authenticator")
    @Route(method = HttpMethod.GET, uri = "/monitor/grapes/credentials/{credential}")
    public Result getCredential() {
        return ok(render(managecredentials));
    }

    @Authenticated("Monitor-Authenticator")
    @Route(method = HttpMethod.POST, uri = "/monitor/grapes/credentials/{credential}")
    public Result createCredential() {
        return ok(render(managecredentials));
    }

    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.POST, uri="/authenticate")
    public Result authenicateUser(){
        //should limit the number of attemps maybe?
        LOG.error("inside authenticate method");
        LOG.error(session("roles"));
        final StringBuilder sb = new StringBuilder();
        sb.append("sessions data?: ");
        sb.append(context().session().getData());
        sb.append(" id ");
        sb.append(context().session().getId());
        LOG.error(sb.toString());
        return ok();
    }
    @Authenticated("grapes-authenticator")
    @Route(method = HttpMethod.DELETE, uri="/authenticate")
    public Result logOut(){
        //todo clear auth?
        //should limit the number of attemps maybe?
        LOG.error("inside authenticate method");
        LOG.error(session("roles"));
        final StringBuilder sb = new StringBuilder();
        sb.append("sessions data?: ");
        sb.append(context().session().getData());
        sb.append(" id ");
        sb.append(context().session().getId());
        LOG.error(sb.toString());
        return ok();
    }
    @Override
    public String label() {
        return "Grapes Credentials";
    }

    @Override
    public String url() {
        return "/monitor/grapes/credentials";
    }

    @Override
    public String category() {
        return "Grapes";
    }
}
