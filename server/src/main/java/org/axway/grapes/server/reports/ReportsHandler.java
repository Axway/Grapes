package org.axway.grapes.server.reports;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.reports.workers.DeliveryArtifactsPicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class is the report execution and caching
 */
public class ReportsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsHandler.class);

    private final RepositoryHandler repositoryHandler;
    private final DeliveryArtifactsPicker artifactsPicker = new DeliveryArtifactsPicker();

    public ReportsHandler(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    public ReportExecution execute(final Report def, final ReportRequest request) {
        if(LOG.isDebugEnabled()) {
            LOG.debug(String.format("Execution report [%s]", request.getReportId()));
        }

        return def.execute(repositoryHandler, request);
    }

    public void refreshDelivery3rdParty(final DbProduct product) {
        artifactsPicker.work(repositoryHandler, product);
    }

    public void refreshAllDeliveries3rdParty() {
        artifactsPicker.work(repositoryHandler);
    }
}
