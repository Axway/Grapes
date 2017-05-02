package org.axway.grapes.server.reports.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.sun.org.omg.CORBA.Repository;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.tmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LicenseReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseReport.class);

    static List<ParameterDefinition> parameters = new ArrayList<>();

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
    public ReportId getId() {
        return ReportId.LICENSES_PER_PRODUCT_RELEASE;
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public ReportExecution execute(final RepositoryHandler repoHandler, final ReportRequest request) {
        LOG.debug(String.format("Executing %s", getName()));

        ReportExecution result = new ReportExecution(request, ReportUtils.getTabularColumnNames(this));

        final Map<String, String> params = request.getParamValues();
        final String name = params.get("name");
        final String version = params.get("version");

        final Optional<DbProduct> productOptional = repoHandler.getOneByQuery(DbCollections.DB_PRODUCT,
                makeQuery(name, version).toString(),
                DbProduct.class);

        if(!productOptional.isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find commercial delivery [%s %s]", params.get("name"), params.get("version")))
                            .build());
        }

        final DbProduct dbProduct = productOptional.get();

        final List<Delivery> filtered = dbProduct.getDeliveries()
                .stream()
                .filter(d -> {
                            return d.getCommercialName().equals(name) &&
                                    d.getCommercialVersion().equals(version);
                        }
                )
                .collect(Collectors.toList());

        if(filtered.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find commercial delivery [%s %s]", name, version))
                            .build());
        }

        final Delivery delivery = filtered.get(0);
        final List<String> dependencies = delivery.getDependencies();

        for(String d : dependencies) {
            result.addResultRow(new String[] {delivery.getCommercialName(), d});
        }

        return result;
    }

    private BasicDBObject makeQuery(final String name, final String version) {
        if(name == null || version == null) {
            throw new IllegalArgumentException("Commercial name and version must not be null");
        }

        BasicDBObject query = new BasicDBObject();
        query.append("deliveries.commercialName", new BasicDBObject("$eq", name));
        query.append("deliveries.commercialVersion", new BasicDBObject("$eq", version));

        LOG.debug(query.toString());
        return query;
    }
}
