package org.axway.grapes.server.db.mongo;

import org.axway.grapes.server.db.RepositoryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class with method used in extracting and processing data from MongoDb
 */
public class BatchProcessor {

    private final RepositoryHandler repoHandler;
    private int batchSize = 1;

    private static final Logger LOG = LoggerFactory.getLogger(BatchProcessor.class);


    public BatchProcessor(RepositoryHandler handler) {
        this.repoHandler = handler;
    }

    public <T> void process(final String collectionName,
                            final Function<List<String>, String> batchQueryFn,
                            final Collection<String> primaryEntries,
                            final Class<T> resultClass,
                            final Consumer<T> consumer) {

        List<String> asList = new ArrayList<>();
        asList.addAll(primaryEntries);

        final List<List<String>> batches = splitList(this.batchSize, asList);

        batches.forEach(batch -> {
            final List<T> dbEntry = repoHandler.getListByQuery(collectionName,
                    batchQueryFn.apply(batch),
                    resultClass);

            if(dbEntry.size() != batch.size()) {
                LOG.warn(String.format("Got fewer results %s < %s", dbEntry.size(), batch.size()));
                LOG.warn("There are referred dependencies not related to known artifacts");
            }

            //
            // Apply the same consumer over every element of the retrieved primaryEntries
            //
            dbEntry.forEach(consumer);
        });

    }

    public void setBatchSize(int value) {
        this.batchSize = value;
    }

    private <T> List<List<T>> splitList(final int batchSize, List<T> list) {
        List<List<T>> batches = new LinkedList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

}
