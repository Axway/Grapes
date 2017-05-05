package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.reports.ReportId;

import java.util.List;

public interface Report {
    String getName();
    String getDescription();
    ReportId getId();
    List<ParameterDefinition> getParameters();
    String[] getColumnNames();

    ReportExecution execute(final RepositoryHandler repoHandler, final ReportRequest request);
}
