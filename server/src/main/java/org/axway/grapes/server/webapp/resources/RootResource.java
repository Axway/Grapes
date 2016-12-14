package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.db.RepositoryHandler;

import javax.ws.rs.Path;

@Path("")
public class RootResource extends AbstractResource {

    public RootResource(final RepositoryHandler repoHandler, final ServiceHandler serviceHandler, final GrapesServerConfig config) {
        super(repoHandler, serviceHandler, "RootResource.ftl", config);
    }

}