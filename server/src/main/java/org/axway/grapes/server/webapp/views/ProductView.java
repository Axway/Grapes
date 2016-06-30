package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.yammer.dropwizard.views.View;

import java.util.ArrayList;
import java.util.List;

import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.webapp.views.serialization.ProductSerializer;

@JsonSerialize(using= ProductSerializer.class)
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
    	List<String> deliveryVersion = new ArrayList<String>();
    	
    	for(Delivery delivery : getDeliveries()){
    		deliveryVersion.add(delivery.getCommercialName() + " " + delivery.getCommercialVersion());
    	}
        return deliveryVersion;
    }

    public List<Delivery> getDeliveries() {
        return Lists.newArrayList(product.getDeliveries());
    }
}
