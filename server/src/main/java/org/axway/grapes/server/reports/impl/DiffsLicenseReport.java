package org.axway.grapes.server.reports.impl;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.reports.models.ParameterDefinition;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.reports.utils.DataFetchingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Report showing evolution of licenses between two commercial releases of a product
 */
public class DiffsLicenseReport implements Report {

    static List<ParameterDefinition> parameters = new ArrayList<>();
    private DataFetchingUtils utils = new DataFetchingUtils();
    private static final Logger LOG = LoggerFactory.getLogger(DiffsLicenseReport.class);

    static {
        parameters.add(new ParameterDefinition("name1", "Commercial Release Name"));
        parameters.add(new ParameterDefinition("version1", "Commercial Version"));

        parameters.add(new ParameterDefinition("name2", "Commercial Release Name"));
        parameters.add(new ParameterDefinition("version2", "Commercial Version"));
    }

    @Override
    public String getName() {
        return "License Evolution Between Product Commercial Releases";
    }

    @Override
    public String getDescription() {
        return "Displays licenses that were added or removed from one commercial release to another";
    }

    @Override
    public int getId() {
        return ReportId.DIFFS_PER_PRODUCT_RELEASE.getId();
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"License Name", "Status"};
    }

    @Override
    public ReportExecution execute(RepositoryHandler repoHandler, ReportRequest request) {
        final Map<String, String> params = request.getParamValues();
        final String name1 = params.get("name1");
        final String version1 = params.get("version1");

        final Optional<Delivery> cd1 = utils.getCommercialDelivery(repoHandler, name1, version1);
        check(cd1, name1, version1);

        final String name2 = params.get("name2");
        final String version2 = params.get("version2");

        final Optional<Delivery> cd2 = utils.getCommercialDelivery(repoHandler, name2, version2);
        check(cd2, name2, version2);

        return processReport(repoHandler, request, cd1.get(), cd2.get());
    }

    private ReportExecution processReport(final RepositoryHandler repoHandler,
                                          final ReportRequest request,
                                          final Delivery d1,
                                          final Delivery d2) {
        ReportExecution result = new ReportExecution(request, getColumnNames());
        Set<String> a1 = new HashSet<>();

        LicenseHandler licenseHandler = new LicenseHandler(repoHandler);

        d1.getAllArtifactDependencies()
                .stream()
                .map(Artifact::getLicenses)
                .map(licenseHandler::resolveLicenses)
                .map(dbLicenses -> dbLicenses.stream().map(DbLicense::getName).collect(Collectors.toSet()))
                .forEach(a1::addAll);

        Set<String> a2 = new HashSet<>();
        d2.getAllArtifactDependencies()
                .stream()
                .map(Artifact::getLicenses)
                .map(licenseHandler::resolveLicenses)
                .map(dbLicenses -> dbLicenses.stream().map(DbLicense::getName).collect(Collectors.toSet()))
                .forEach(a2::addAll);

        Set<String> all = new HashSet<>();
        all.addAll(a1);
        all.addAll(a2);

        all.removeAll(a2);

        all.forEach(entry -> {
            LOG.debug(String.format("%s is missing", entry));
            result.addResultRow(new String[]{entry, "Missing"});
        });

        all.addAll(a2);
        all.removeAll(a1);

        all.forEach(entry -> {
            LOG.debug(String.format("%s is added", entry));
            result.addResultRow(new String[]{entry, "Added"});
        });

        return result;
    }


    private void check(final Optional<?> entry, final String name, final String version) {
        if(!entry.isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find commercial delivery [%s %s]", name, version))
                            .build());
        }
    }
}
