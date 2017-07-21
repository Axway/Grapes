package org.axway.grapes.server.reports.workers;

import org.apache.commons.lang3.StringUtils;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.server.core.DependencyHandler;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.db.mongo.BatchProcessor;
import org.axway.grapes.server.db.mongo.QueryUtils;
import org.axway.grapes.server.reports.utils.DataFetchingUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Picks up the artifact dependencies for a product commercial delivery
 */
public class DeliveryArtifactsPicker {

    private static final String BATCH_TEMPLATE = "{ \"_id\" : { $in : [%s]} }";
    private static final String BATCH_TEMPLATE_REGEX = "{ \"_id\" : { \"$regex\" : \"%s\"}}";

    public void work(RepositoryHandler repositoryHandler) {
        DataFetchingUtils utils = new DataFetchingUtils();
        final List<DbProduct> products = utils.getProductWithCommercialDeliveries(repositoryHandler);
        products.forEach(product -> work(repositoryHandler, product));
    }

    // refresh all deliveries dependencies for a particular product
    public void work(RepositoryHandler repoHandler, DbProduct product) {
        if (!product.getDeliveries().isEmpty()) {

            product.getDeliveries().forEach(delivery -> {

                final Set<Artifact> artifacts = new HashSet<>();

                final DataFetchingUtils utils = new DataFetchingUtils();
                final DependencyHandler depHandler = new DependencyHandler(repoHandler);
                final Set<String> deliveryDependencies = utils.getDeliveryDependencies(repoHandler, depHandler, delivery);

                final Set<String> fullGAVCSet = deliveryDependencies.stream().filter(DataUtils::isFullGAVC).collect(Collectors.toSet());
                final Set<String> shortIdentiferSet = deliveryDependencies.stream().filter(entry -> !DataUtils.isFullGAVC(entry)).collect(Collectors.toSet());


                processDependencySet(repoHandler,
                        shortIdentiferSet,
                        batch -> String.format(BATCH_TEMPLATE_REGEX, StringUtils.join(batch, '|')),
                        1,
                        artifacts::add
                        );

                processDependencySet(repoHandler,
                        fullGAVCSet,
                        batch -> QueryUtils.quoteIds(batch, BATCH_TEMPLATE),
                        10,
                        artifacts::add
                );

                if (!artifacts.isEmpty()) {
                    delivery.setAllArtifactDependencies(new ArrayList<>(artifacts));
                }
            });

            repoHandler.store(product);
        }
    }

    private void processDependencySet(final RepositoryHandler repoHandler,
                                      final Set<String> set,
                                      final Function<List<String>, String> fn,
                                      final int batchSize,
                                      final Consumer<Artifact> artifactConsumer) {
        BatchProcessor batchProcessor = new BatchProcessor(repoHandler);
        batchProcessor.setBatchSize(batchSize);

        batchProcessor.process(DbCollections.DB_ARTIFACTS,
                fn,
                set,
                DbArtifact.class,
                a -> {
                    Artifact artifact = DataModelFactory.createArtifact(
                            a.getGroupId(), a.getArtifactId(), a.getVersion(), a.getClassifier(),
                            a.getType(), a.getExtension(), a.getOrigin());
                    a.getLicenses().forEach(artifact::addLicense);
                    artifact.setDoNotUse(a.getDoNotUse());

                    artifactConsumer.accept(artifact);
                });

    }
}
