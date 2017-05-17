package org.axway.grapes.server.reports.impl;

import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.DependencyHandler;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.reports.ReportId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Creates a report of dependencies and licenses for a certain commercial version
 */
public class LicenseReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseReport.class);

    private static final String BATCH_TEMPLATE = "{ \"_id\" : { \"$in\" : [%s]}}" ;
    private static final String BATCH_TEMPLATE_REGEX = "{ \"_id\" : { \"$regex\" : \"%s\"}}";

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
                .filter(d -> d.getCommercialName().equals(name) &&
                        d.getCommercialVersion().equals(version)

                )
                .collect(Collectors.toList());

        if(filtered.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find commercial delivery [%s %s]", name, version))
                            .build());
        }

        final Delivery delivery = filtered.get(0);

        return computeResult(repoHandler, request, delivery);
    }

    private ReportExecution computeResult(final RepositoryHandler repoHandler, final ReportRequest request, final Delivery delivery) {
        ReportExecution result = new ReportExecution(request, getColumnNames());

        DependencyHandler dependencyHandler = new DependencyHandler(repoHandler);

        final FiltersHolder filters = new FiltersHolder();
        filters.getDecorator().setShowCorporate(Boolean.FALSE);
        filters.getDecorator().setShowThirdparty(Boolean.TRUE);

        Set<String> deps = new HashSet<>();

        delivery.getDependencies().forEach(d -> {
            final DbModule module = repoHandler.getModule(d);
            if (module != null) {
                final List<Dependency> allDependencies = dependencyHandler.getModuleDependencies(module.getId(), filters);

                allDependencies.forEach(dep -> {
                    deps.add(dep.getTarget().getGavc());
                });
            } else {
                deps.add(d);
            }
        });

        // If the dependency is referred with classifier, so it's fairly easy to query by exact match against id
        processBatch(repoHandler,
                DbCollections.DB_ARTIFACTS,
                1,
                this::makeBatchQuery,
                deps,
                DbArtifact.class,
                a -> {
                    a.getLicenses()
                            .stream()
                            .filter(lic -> !lic.contains("Axway Software"))
                            .forEach(lic -> result.addResultRow(makeResultsRow(a, lic)));
                });

        // If the dependency is referred without classifier then a regexp matching needs to be performed
//        processBatch(repoHandler,
//                DbCollections.DB_ARTIFACTS,
//                1,
//                this::makeBatchQueryRegEx,
//                deps,
//                DbArtifact.class,
//                artifact -> {
//                    artifact.getLicenses()
//                            .stream()
//                            .filter(lic -> !lic.contains("Axway Software"))
//                            .forEach(lic -> result.addResultRow(makeResultsRow(artifact, lic)));
//                });

        return result;
    }

    private <T> void processBatch(final RepositoryHandler repoHandler,
                                  final String collectionName,
                                  final int batchSize,
                                  final Function<List<String>, String> batchQueryFn,
                                  final Set<String> entries,
                                  final Class<T> tClass,
                                  final Consumer<T> consumer) {

        List<String> asList = new ArrayList<>();
        asList.addAll(entries);

        final List<List<String>> batches = splitList(batchSize, asList);

        batches.forEach(batch -> {
            final List<T> dbEntry = repoHandler.getListByQuery(collectionName,
                    batchQueryFn.apply(batch),
                    tClass);

            dbEntry.forEach(consumer);
        });

    }

//    private Map<String, String> loadLicenses(final RepositoryHandler repoHandler, final List<String> dependencyIds) {
//        int batchSize = 10;
//
//        final List<List<String>> batches = splitList(batchSize, dependencyIds);
//
//        final Map<String, String> result = new HashMap<>();
//
//        batches.forEach(idBatch -> {
//                    final List<DbArtifact> artifacts = repoHandler.getListByQuery(DbCollections.DB_ARTIFACTS,
//                            makeBatchQuery(idBatch),
//                            DbArtifact.class);
//
//                    artifacts.forEach(a -> {
//                        a.getLicenses()
//                                .stream()
//                                .filter(lic -> !lic.contains("Axway Software"))
//                                .forEach(lic -> result.put(a.getGavc(), lic));
//                    });
//                });
//        return result;
//    }
//
//    private List<DbDependency> getModuleDependencies(final RepositoryHandler repoHandler,
//                                                     final String moduleId) {
//
//        final DbModule module = repoHandler.getModule(moduleId);
//        if (module != null) {
//            return module.getDependencies()
//                    .stream()
//                    .filter(dep -> dep.getScope() != Scope.TEST)
//                    .collect(Collectors.toList());
//        }
//
//        return Collections.emptyList();
//
//    }

    private String makeBatchQueryRegEx(final List<String> ids) {
        if(ids == null) {
            throw new IllegalArgumentException("Ids must not be null");
        }

        String result;

        if(ids.size() == 1) {
            result = String.format(BATCH_TEMPLATE_REGEX, ids.get(0));
        } else {
            result = String.format(BATCH_TEMPLATE_REGEX, StringUtils.join(ids, '|'));
        }

        // LOG.debug(result);
        return result;

    }

    private String makeBatchQuery(final List<String> ids) {
        if(ids == null) {
            throw new IllegalArgumentException("Ids must not be null");
        }

        StringBuilder b = new StringBuilder();

        ids.forEach(entry -> {
            b.append("'");
            b.append(entry);
            b.append("',");
        });
        b.setLength(b.length() - 1);

        String result = String.format(BATCH_TEMPLATE, b.toString());
        // LOG.debug(result);
        return result;
    }

    private BasicDBObject makeQuery(final String name, final String version) {
        if(name == null || version == null) {
            throw new IllegalArgumentException("Commercial name and version must not be null");
        }

        BasicDBObject query = new BasicDBObject();
        // MongoDb 2.4 does not support $eq for comparison
        // query.append("deliveries.commercialName", new BasicDBObject("$eq", name));
        // query.append("deliveries.commercialVersion", new BasicDBObject("$eq", version));

        // TODO: Switch to using $eq when MongoDb 2.6 available on the host machine
        query.append("deliveries.commercialName", new BasicDBObject("$in", new String[] {name}));
        query.append("deliveries.commercialVersion", new BasicDBObject("$in", new String[] {version}));

        //LOG.debug(query.toString());
        return query;
    }



    private <T> List<List<T>> splitList(final int batchSize, List<T> list) {
        List<List<T>> batches = new LinkedList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    private String[] makeResultsRow(final DbArtifact a, final String lic) {
        return new String[] {a.getGroupId(), a.getArtifactId(), a.getClassifier(), a.getVersion(), a.getDoNotUse().toString(), lic};
    }
}