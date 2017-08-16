package org.axway.grapes.server.core;

import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbSearch;

/**
 * Search handler
 * <p>Manages operation related with searching modules and artifacts</p>
 */
public class SearchHandler {

    private final RepositoryHandler repositoryHandler;

    public SearchHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    /**
     *
     * Retrieve modules and/or artifacts based on filters and search parameter
     * @param search - search parameter
     * @param filter - filter to include or exclude modules/artifacts
     * @return - search result
     */
    public DbSearch getSearchResult(final String search, final FiltersHolder filter) {
        return repositoryHandler.getSearchResult(search, filter);
    }
}
