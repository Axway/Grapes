package org.axway.grapes.server.reports.impl;

import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbProduct;
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
import java.util.stream.Collectors;

import static org.axway.grapes.server.reports.ReportId.LICENSE_ON_PRODUCT_RELEASES;

/**
 * Collects all the commercial delivery names which are using a particular third party license
 */
public class LicenseOnProductReleases implements Report {

    private List<ParameterDefinition> parameters = new ArrayList<>();
    private String[] columnNames = new String[] {"Commercial Release"};

    private static final Logger LOG = LoggerFactory.getLogger(LicenseOnProductReleases.class);


    public LicenseOnProductReleases() {
        parameters.add(new ParameterDefinition("license", "License"));
    }

    @Override
    public String getName() {
        return "License in commercial releases";
    }

    @Override
    public String getDescription() {
        return "Displays the list of products which uses artifacts using a particular license";
    }

    @Override
    public int getId() {
        return LICENSE_ON_PRODUCT_RELEASES.getId();
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public ReportExecution execute(RepositoryHandler repoHandler, ReportRequest request) {

        final Map<String, String> params = request.getParamValues();
        final String licenseName = params.get("license");

        LicenseHandler licenseHandler = new LicenseHandler(repoHandler);
        final DbLicense license = licenseHandler.getLicense(licenseName);

        if(license == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find license [%s]", licenseName))
                            .build());
        }


        return reportExecution(repoHandler, request, licenseName);
    }

    private ReportExecution reportExecution(final RepositoryHandler repoHandler,
                                            final ReportRequest request,
                                            final String license) {
        ReportExecution result = new ReportExecution(request, getColumnNames());

        DataFetchingUtils x = new DataFetchingUtils();
        final List<DbProduct> products = x.getProductWithCommercialDeliveries(repoHandler);

        Set<Delivery> allDeliveries = new HashSet<>();

        products.forEach(p -> allDeliveries.addAll(p.getDeliveries()));

        allDeliveries
                .stream()
                .filter(d -> {
                    final List<Artifact> artifacts = d.getAllArtifactDependencies()
                            .stream()
                            .filter(a -> a.getLicenses().contains(license))
                            .collect(Collectors.toList());

                    return !artifacts.isEmpty();
                })
                .map(d -> d.getCommercialName() + " " + d.getCommercialVersion())
                .forEach(name -> result.addResultRow(new String[] {name}));

        return result;
    }
}
