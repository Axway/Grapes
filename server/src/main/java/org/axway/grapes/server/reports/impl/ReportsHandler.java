package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.db.RepositoryHandler;

/**
 * Reports Handler
 *
 */
public class ReportsHandler {

    private final RepositoryHandler repositoryHandler;

    public ReportsHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

//    public <T> Optional<T> getOneByQuery(final String collectionName, final String query, final Class<T> c) {
//        return repositoryHandler.getOneByQuery(collectionName, query, c);
//    }

    public ReportExecution execute(final Report def, final ReportRequest request) {
        return def.execute(repositoryHandler, request);
    }
}
