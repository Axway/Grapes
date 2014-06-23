package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;

@Path("")
public class RootResource extends AbstractResource {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactResource.class);

    public RootResource(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
        super(repoHandler, "RootResource.ftl", config);
    }

}