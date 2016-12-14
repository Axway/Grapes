package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.db.RepositoryHandler;

import javax.ws.rs.Path;

/**
 * Web Application Resource
 *
 * <p>Provide an ui over grapes REST API</p>
 *
 * @author jdcoffre
 */
@Path(ServerAPI.WEBAPP_RESOURCE)
public class WebAppResource extends AbstractResource{

    public WebAppResource(final RepositoryHandler repoHandler, final ServiceHandler serviceHandler, final GrapesServerConfig config) {
        super(repoHandler, serviceHandler, "WebApp.ftl", config);
    }
}
