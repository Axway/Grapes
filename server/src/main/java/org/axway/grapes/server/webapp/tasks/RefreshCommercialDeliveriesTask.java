package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.reports.workers.DeliveryArtifactsPicker;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * Utility task for refreshing the structure of a particular product deliveries or
 * for all the existing products.
 */
public class RefreshCommercialDeliveriesTask extends Task {

    private final RepositoryHandler repoHandler;

    public RefreshCommercialDeliveriesTask(final RepositoryHandler repoHandler) {
        super("refreshDeliveries");
        this.repoHandler = repoHandler;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {
        final ImmutableMap<String, Collection<String>> params = immutableMultimap.asMap();

        DeliveryArtifactsPicker worker = new DeliveryArtifactsPicker();

        if(params.containsKey("product")) {
            final DbProduct product = repoHandler.getProduct(params.get("product").toArray(new String[0])[0]);
            if(product == null) {
                printWriter.println("No such product");
                return;
            } else {
                printWriter.println("Refreshing deliveries of " + product.getName());
                printWriter.flush();
                worker.work(repoHandler, product);
            }
        } else {
            printWriter.println("Refreshing all commercial deliveries...");
            printWriter.flush();
            worker.work(repoHandler);
        }

        printWriter.println("Refresh is done");
    }
}
