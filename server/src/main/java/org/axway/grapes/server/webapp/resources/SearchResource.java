package org.axway.grapes.server.webapp.resources;

import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;

import javax.ws.rs.Path;

/**
 * Search resource
 * <p>This server resource handles all the request about searching modules and/or artifacts.</p>
 */

@Path(ServerAPI.SEARCH_DOC_RESOURCE)
public class SearchResource extends AbstractResource {

    public SearchResource(RepositoryHandler repoHandler, GrapesServerConfig dmConfig) {
        super(repoHandler, "SearchResourceDocumentation.ftl", dmConfig);
    }
}
