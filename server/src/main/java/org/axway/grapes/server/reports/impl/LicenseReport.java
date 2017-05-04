package org.axway.grapes.server.reports.impl;

import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.axway.grapes.commons.datamodel.*;
import org.axway.grapes.server.core.GraphsHandler;
import org.axway.grapes.server.core.graphs.AbstractGraph;
import org.axway.grapes.server.core.options.FiltersHolder;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.*;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.tmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LicenseReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseReport.class);

    private static final String BATCH_TEMPLATE = "{ \"_id\" : { \"$in\" : [%s]}}" ;
    private static final String BATCH_TEMPLATE_REGEX = "{ \"_id\" : { \"$regex\" : \"%s\"}}";

    static List<ParameterDefinition> parameters = new ArrayList<>();

    static {
        parameters.add(new ParameterDefinition("name", "Commercial Name"));
        parameters.add(new ParameterDefinition("version", "Commercial Version"));
        parameters.add(new ParameterDefinition("batch", "Batch Size"));
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
        return new String[] {"artifact", "license name"};
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

        ReportExecution result = new ReportExecution(request, getColumnNames());

        final Delivery delivery = filtered.get(0);

        long start = System.currentTimeMillis();

        final int batch = Integer.parseInt(params.get("batch"));

        Set<String> allModules = new TreeSet<>();
        loadSubmodulesTree(repoHandler, delivery.getDependencies(), 2, allModules);
        long modulesLoaded = System.currentTimeMillis();

        System.out.println("=========== modules");
        allModules.forEach(System.out::println);


        Set<String> dependencies = new TreeSet<>();
        dependencies.addAll(delivery.getDependencies()); // for artifacts which are delivery own third party dependencies
        processBatch(repoHandler,
                DbCollections.DB_MODULES,
                1,
                this::makeBatchQuery,
                new ArrayList<>(allModules),
                DbModule.class,
                module -> {
                    module.getUses()
                            .stream()
                            .forEach(dependencies::add);
                    module.getDependencies()
                            .stream()
                            .map(DbDependency::getTarget)
                            .forEach(dependencies::add);
                });
        long dependenciesLoaded = System.currentTimeMillis();

        System.out.println("=========== dependencies");
        dependencies.forEach(System.out::println);

        Map<String, String> licenses = new TreeMap<>();
        processBatch(repoHandler,
                DbCollections.DB_ARTIFACTS,
                1,
                this::makeBatchQuery,
                new ArrayList<>(dependencies),
                DbArtifact.class,
                artifact -> {
                    artifact.getLicenses()
                            .stream()
                            .filter(lic -> !lic.contains("Axway Software"))
                            .forEach(lic -> licenses.put(artifact.getGavc(), lic));
                });

        processBatch(repoHandler,
                DbCollections.DB_ARTIFACTS,
                batch,
                this::makeBatchQueryRegEx,
                delivery.getDependencies(),
                DbArtifact.class,
                artifact -> {
                    artifact.getLicenses()
                            .stream()
                            .filter(lic -> !lic.contains("Axway Software"))
                            .forEach(lic -> licenses.put(artifact.getGavc(), lic));
                });


        System.out.println("=========== licenses");
        licenses.forEach((key, value) -> System.out.println(String.format("%s -> %s", key, value)));



        long licensesLoaded = System.currentTimeMillis();

        addToResult(result, licenses);


        LOG.debug(String.format("Loading modules: %s", (modulesLoaded - start)));
        LOG.debug(String.format("Loading deps: %s", (dependenciesLoaded - modulesLoaded)));
        LOG.debug(String.format("Loading licenses: %s", (licensesLoaded - dependenciesLoaded)));

        return result;
    }

    private void addToResult(final ReportExecution result, Map<String, String> licenses) {
        licenses.entrySet()
                .stream()
//              .filter(entry -> !entry.getValue().contains("Axway Software") )   // internal Axway modules
                .forEach(entry -> result.addResultRow(new String[] {entry.getKey(), entry.getValue()}) );

//        LOG.debug(String.format("Result contains [%s] entries", result.getData().size()));
    }

    private <T> void processBatch(final RepositoryHandler repoHandler,
                                  final String collectionName,
                                  final int batchSize,
                                  final Function<List<String>, String> batchQueryFn,
                                  final List<String> entries,
                                  final Class<T> tClass,
                                  final Consumer<T> consumer) {

        final List<List<String>> batches = splitList(batchSize, entries);

        batches.forEach(batch -> {
            final List<T> dbEntry = repoHandler.getListByQuery(collectionName,
                    batchQueryFn.apply(batch),
                    tClass);

            dbEntry.forEach(consumer);
        });

    }

    private Map<String, String> loadLicenses(final RepositoryHandler repoHandler, final List<String> dependencyIds) {
        int batchSize = 10;

        final List<List<String>> batches = splitList(batchSize, dependencyIds);

        final Map<String, String> result = new HashMap<>();

        batches.forEach(idBatch -> {
                    final List<DbArtifact> artifacts = repoHandler.getListByQuery(DbCollections.DB_ARTIFACTS,
                            makeBatchQuery(idBatch),
                            DbArtifact.class);

                    artifacts.forEach(a -> {
                        a.getLicenses()
                                .stream()
                                .filter(lic -> !lic.contains("Axway Software"))
                                .forEach(lic -> result.put(a.getGavc(), lic));
                    });
                });
        return result;
    }

    private List<DbDependency> getModuleDependencies(final RepositoryHandler repoHandler,
                                                     final String moduleId) {

        final DbModule module = repoHandler.getModule(moduleId);
        if (module != null) {
            return module.getDependencies()
                    .stream()
                    .filter(dep -> dep.getScope() != Scope.TEST)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();

    }

    private Set<String> getModules(RepositoryHandler repositoryHandler, String moduleId) {

        GraphsHandler h = new GraphsHandler(repositoryHandler, new FiltersHolder());
        AbstractGraph moduleGraph = null;

        try {
            moduleGraph = h.getModuleGraph(moduleId);
        } catch(WebApplicationException e) {
            if(e.getResponse().getStatus() == 404) {
                System.out.println("Not a module " + moduleId);
                return Collections.emptySet();
            }
        }

        final Set<String> result = moduleGraph.getElements()
                .stream()
                .map(elem -> String.format("%s:%s", elem.getValue(), elem.getVersion()))
                .collect(Collectors.toSet());

        Set<String> sorted = new TreeSet<>();
        sorted.addAll(result);

//        System.out.println("Module Elements for " + moduleId);
//        sorted.stream().forEach(System.out::println);
//        System.out.println("===========");

        return sorted;
    }

    // About to be deprecated
//    private Map<String, String> getLicenses1(final RepositoryHandler repoHandler,
//                                             final DependencyHandler dependencyHandler,
//                                             final Delivery delivery) {
//        Map<String, String> licenses = new HashMap<>();
//        delivery.getDependencies()
//                .forEach(d -> {
//                    final List<DbArtifact> artifacts = repoHandler.getListByQuery(DbCollections.DB_ARTIFACTS, makeBatchQuery(d), DbArtifact.class);
//                    artifacts.forEach(a -> {
//                        final List<String> artifactLicenses = a.getLicenses();
//                        LOG.debug(String.format("Found artifact lic [%s] [%s] [%s]", d, a.getGavc(), artifactLicenses.size()));
//                        artifactLicenses.forEach(lic -> {
//                            licenses.put(a.getGavc(), lic);
//                        });
//                    });
//
//                    final DbModule module = repoHandler.getModule(d);
//                    if (module != null) {
//                        final List<Dependency> allDependencies = dependencyHandler.getModuleDependencies(d, new FiltersHolder());
//                        allDependencies.forEach(dep -> {
//                            if (dep.getTarget().getLicenses().size() > 0) {
//                                LOG.debug(String.format("Found [%s] licenses on [%s]", dep.getTarget().getLicenses().size(), dep.getTarget().getGavc()));
//                            }
//                            dep.getTarget().getLicenses().forEach(lic -> {
//                                licenses.put(dep.getTarget().getGavc(), lic);
//                            });
//                        });
//                    }
//                });
//
//        return licenses;
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
        query.append("deliveries.commercialName", new BasicDBObject("$eq", name));
        query.append("deliveries.commercialVersion", new BasicDBObject("$eq", version));

        // LOG.debug(query.toString());
        return query;
    }



    private <T> List<List<T>> splitList(final int batchSize, List<T> list) {
        List<List<T>> batches = new LinkedList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    private void loadSubmodulesTree(final RepositoryHandler repoHandler,
                                    final List<String> entryPoints,
                                    final int depth,
                                    final Set<String> collector) {

        collector.addAll(entryPoints);

        if(depth == 0) {
            return;
        }

        List<String> subModules = new ArrayList<>();

        processBatch(repoHandler,
                DbCollections.DB_MODULES,
                1,
                this::makeBatchQuery,
                entryPoints,
                DbModule.class,
                module -> module.getSubmodules().forEach(subModule -> subModules.add(subModule.getId()))
        );

        loadSubmodulesTree(repoHandler, subModules, depth - 1, collector);
    }


}
