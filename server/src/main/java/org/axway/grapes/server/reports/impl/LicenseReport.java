package org.axway.grapes.server.reports.impl;

import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.db.mongo.BatchProcessingUtils;
import org.axway.grapes.server.db.mongo.QueryUtils;
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

    private static final String BATCH_TEMPLATE = "{ \"_id\" : { \"$in\" : [%s]}}" ;
    private static final String BATCH_TEMPLATE_REGEX = "{ \"_id\" : { \"$regex\" : \"%s\"}}";

    static List<ParameterDefinition> parameters = new ArrayList<>();

    private DataFetchingUtils utils = new DataFetchingUtils();
    private BatchProcessingUtils batchUtils = new BatchProcessingUtils();

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
    public String[] getColumnNames() {
        return new String[] {"Group Id", "Artifact Id", "Classifier", "Version" , "Artifact Marked (Do Not Use)", "License Name"};
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public ReportExecution execute(final RepositoryHandler repoHandler, final ReportRequest request) {
        LOG.debug(String.format("Executing %s", getName()));

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

        final Delivery delivery = deliveryOp.get();
        return computeResult(repoHandler, request, delivery);
    }

    private ReportExecution computeResult(final RepositoryHandler repoHandler, final ReportRequest request, final Delivery delivery) {
        ReportExecution result = new ReportExecution(request, getColumnNames());

        Set<String> deps = utils.getDeliveryDependencies(repoHandler, delivery);

        // If the dependency is referred with classifier, so it's fairly easy to query by exact match against id
        batchUtils.processBatch(repoHandler,
                DbCollections.DB_ARTIFACTS,
                1,
                batch -> QueryUtils.quoteIds(batch, BATCH_TEMPLATE),
                deps,
                DbArtifact.class,
                a -> {
                    a.getLicenses()
                            .stream()
                            .filter(lic -> !lic.contains("Axway Software"))
                            .forEach(lic -> result.addResultRow(makeResultsRow(a, lic)));
                });

        return result;
    }

//    private <T> void processBatch(final RepositoryHandler repoHandler,
//                                  final String collectionName,
//                                  final int batchSize,
//                                  final Function<List<String>, String> batchQueryFn,
//                                  final Set<String> entries,
//                                  final Class<T> tClass,
//                                  final Consumer<T> consumer) {
//
//        List<String> asList = new ArrayList<>();
//        asList.addAll(entries);
//
//        final List<List<String>> batches = splitList(batchSize, asList);
//
//        batches.forEach(batch -> {
//            final List<T> dbEntry = repoHandler.getListByQuery(collectionName,
//                    batchQueryFn.apply(batch),
//                    tClass);
//
//            dbEntry.forEach(consumer);
//        });
//
//    }

//    private String makeBatchQueryRegEx(final List<String> ids) {
//        if(ids == null) {
//            throw new IllegalArgumentException("Ids must not be null");
//        }
//
//        String result;
//
//        if(ids.size() == 1) {
//            result = String.format(BATCH_TEMPLATE_REGEX, ids.get(0));
//        } else {
//            result = String.format(BATCH_TEMPLATE_REGEX, StringUtils.join(ids, '|'));
//        }
//
//        // LOG.debug(result);
//        return result;
//
//    }

//    private String makeBatchQuery(final List<String> ids) {
//        if(ids == null) {
//            throw new IllegalArgumentException("Ids must not be null");
//        }
//
//        StringBuilder b = new StringBuilder();
//
//        ids.forEach(entry -> {
//            b.append("'");
//            b.append(entry);
//            b.append("',");
//        });
//        b.setLength(b.length() - 1);
//
//        String result = String.format(BATCH_TEMPLATE, b.toString());
//        // LOG.debug(result);
//        return result;
//    }


//    private <T> List<List<T>> splitList(final int batchSize, List<T> list) {
//        List<List<T>> batches = new LinkedList<>();
//        for (int i = 0; i < list.size(); i += batchSize) {
//            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
//        }
//        return batches;
//    }

    private String[] makeResultsRow(final DbArtifact a, final String lic) {
        return new String[] {a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getDoNotUse().toString(), lic};
    }
}