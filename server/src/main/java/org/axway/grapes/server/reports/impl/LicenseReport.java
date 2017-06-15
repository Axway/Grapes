package org.axway.grapes.server.reports.impl;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.models.ParameterDefinition;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.reports.utils.DataFetchingUtils;
import org.axway.grapes.server.reports.ReportId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Creates a report of dependencies and licenses for a certain commercial version
 */
public class LicenseReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseReport.class);

    static List<ParameterDefinition> parameters = new ArrayList<>();

    private DataFetchingUtils utils = new DataFetchingUtils();

    static {
        parameters.add(new ParameterDefinition("name", "Commercial Name"));
        parameters.add(new ParameterDefinition("version", "Commercial Version"));
    }

    @Override
    public String getName() {
        return "Licenses Used in Product Release";
    }

    @Override
    public String getDescription() {
        return "Displays all the licenses used by dependencies of a product commercial release";
    }

    @Override
    public int getId() {
        return ReportId.LICENSES_PER_PRODUCT_RELEASE.getId();
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Group Id", "Artifact Id", "Classifier", "Version" , "Artifact Marked (Do Not Use)", "License Name"};
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public ReportExecution execute(final RepositoryHandler repoHandler, final ReportRequest request) {
        if(LOG.isDebugEnabled()) {
            LOG.debug(String.format("Executing %s", getName()));
        }

        final Map<String, String> params = request.getParamValues();
        final String name = params.get("name");
        final String version = params.get("version");

        final Optional<Delivery> deliveryOp = utils.getCommercialDelivery(repoHandler, name, version);

        if(!deliveryOp.isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find commercial delivery [%s %s]", params.get("name"), params.get("version")))
                            .build());
        }

        return computeResult(request, deliveryOp.get());
    }

    private ReportExecution computeResult(final ReportRequest request, final Delivery delivery) {
        final ReportExecution result = new ReportExecution(request, getColumnNames());

        delivery.getAllArtifactDependencies().forEach(
            a -> a.getLicenses()
                        .stream()
                        .filter(lic -> !lic.contains("Axway Software"))
                        .forEach(lic -> result.addResultRow(makeResultsRow(a, lic)))
            );
        return result;
    }

    private String[] makeResultsRow(final Artifact a, final String lic) {
        return new String[] {a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), Boolean.toString(a.isDoNotUse()), lic};
    }

}