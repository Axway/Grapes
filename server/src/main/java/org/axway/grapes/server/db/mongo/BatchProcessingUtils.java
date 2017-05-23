package org.axway.grapes.server.db.mongo;

import org.axway.grapes.server.db.RepositoryHandler;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class with method used in extracting and processing data from MongoDb
 */
public class BatchProcessingUtils {

    public <T> void processBatch(final RepositoryHandler repoHandler,
                                 final String collectionName,
                                 final int batchSize,
                                 final Function<List<String>, String> batchQueryFn,
                                 final Collection<String> entries,
                                 final Class<T> resultClass,
                                 final Consumer<T> consumer) {

        List<String> asList = new ArrayList<>();
        asList.addAll(entries);

        final List<List<String>> batches = splitList(batchSize, asList);

        batches.forEach(batch -> {
            final List<T> dbEntry = repoHandler.getListByQuery(collectionName,
                    batchQueryFn.apply(batch),
                    resultClass);

            //
            // Apply the same consumer over every element of the retrieved entries
            //
            dbEntry.forEach(consumer);
        });

    }

    private <T> List<List<T>> splitList(final int batchSize, List<T> list) {
        List<List<T>> batches = new LinkedList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

}
