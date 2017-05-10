package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.db.RepositoryHandler;

/**
 * Reports Handler
 */
public class ReportsHandler {

    private final RepositoryHandler repositoryHandler;

    public ReportsHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    public ReportExecution execute(final Report def, final ReportRequest request) {
        return def.execute(repositoryHandler, request);
    }
}
