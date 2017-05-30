package org.axway.grapes.server.reports;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class is the report execution and caching
 */
public class ReportsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsHandler.class);

    private CacheAccess<String, ReportExecution> cache = null;
    private final RepositoryHandler repositoryHandler;

    public ReportsHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
        initCache();
    }

    private void initCache() {
        try {
            cache = JCS.getInstance("reports");
            LOG.info("Reports cache initialized");
        } catch (CacheException e) {
            LOG.warn(String.format("Problem initializing report cache: %s %s", e.getMessage(), e));
        }
    }

    public ReportExecution execute(final Report def, final ReportRequest request) {
        final boolean useCache = true;

        final ReportExecution cachedExecution = cache.get(request.toString());

        if(useCache) {
            if (cachedExecution != null) {
                LOG.info("Returning cached report execution");
                return cachedExecution;
            }
        }

        LOG.debug(String.format("Execution report [%s]", request.getReportId()));
        final ReportExecution execution = def.execute(repositoryHandler, request);

        // Caching the execution for later retrieval
        cache.put(request.toString(), execution);

        return execution;
    }
}
