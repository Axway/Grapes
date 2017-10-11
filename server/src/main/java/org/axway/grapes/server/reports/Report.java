package org.axway.grapes.server.reports;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.reports.models.ParameterDefinition;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;

import java.util.List;

/**
 * Model class for a generic report
 */
public interface Report {

    String getName();

    String getDescription();

    int getId();

    /**
     * Returns the list of expected parameters. All the parameter entries are considered mandatory.
     * @return The list of parameters.
     */
    List<ParameterDefinition> getParameters();

    /**
     * These are the column names of the response
     * @return The ordered list of column names
     */
    String[] getColumnNames();

    /**
     * Executes the report request and returns the generic response
     * @param repoHandler The repository handler
     * @param request The report request
     * @return The report results.
     */
    ReportExecution execute(final RepositoryHandler repoHandler, final ReportRequest request);
}
