package org.axway.grapes.server.reports.impl;

import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.db.mongo.QueryUtils;
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

import static org.axway.grapes.server.reports.ReportId.LICENSE_ON_PRODUCT_RELEASES;

/**
 *
 */
public class LicenseOnProductReleases implements Report {

    private List<ParameterDefinition> parameters = new ArrayList<>();
    private String[] columnNames = new String[] {"Commercial Release"};

    private DataFetchingUtils utils = new DataFetchingUtils();
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
    public ReportId getId() {
        return LICENSE_ON_PRODUCT_RELEASES;
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

        final List<DbProduct> listByQuery = repoHandler.getListByQuery(DbCollections.DB_PRODUCT, QueryUtils.makeQueryAllDeliveries(), DbProduct.class);

        final ReportExecution execution = new ReportExecution(request, getColumnNames());
        final Set<Delivery> allDeliveries = new HashSet<>();

        listByQuery.forEach(product -> {
            allDeliveries.addAll(product.getDeliveries());
        });

        Set<String> products = new HashSet<>();

        utils.processDeliveryLicenses(repoHandler,
                allDeliveries,
                (commercialDelivery, gavc, artifactLic) -> {
                    LOG.info(String.format("%s : %s : %s", commercialDelivery, gavc, artifactLic));

                    if(licenseName.equalsIgnoreCase(artifactLic)) {
                        products.add(commercialDelivery);
                    }
                }
        );

        products.forEach(product -> execution.addResultRow(new String[] {product}));

        return execution;
    }
}
