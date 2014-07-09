package org.axway.grapes.server.webapp.views;

import com.google.common.collect.Lists;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.server.db.datamodel.DbProduct;

import java.util.List;

public class ProductView extends View{

    private final DbProduct product;

    public ProductView(final DbProduct dbProduct) {
        super("ProductView.ftl");
        this.product = dbProduct;
    }

    public DbProduct getProduct() {
        return product;
    }

    public List<String> getDeliveriesVersions() {
        return Lists.newArrayList(product.getDeliveries().keySet());
    }
}
