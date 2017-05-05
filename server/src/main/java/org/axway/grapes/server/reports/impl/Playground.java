package org.axway.grapes.server.reports.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbProduct;
import org.axway.grapes.server.db.mongo.JongoUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mganuci on 4/28/17.
 */
public class Playground {

    private RepositoryHandler repoHandler;

    public Playground(RepositoryHandler h) {
        this.repoHandler = h;
    }

    public void printLicenseReport(String comName, String comVersion) {
        BasicDBObject query = new BasicDBObject();
        query.append("deliveries.0", new BasicDBObject("$exists", true));

        System.out.println(String.format("Query: %s", query.toString()));

        Optional<DbProduct> reply = repoHandler.getOneByQuery(DbCollections.DB_PRODUCT, query.toString(), DbProduct.class);

        System.out.println(String.format("Results: %s", reply.isPresent()));
        if(reply.isPresent()) {
            System.out.println(String.format("Data: %s", reply.get()));
        }

        System.out.println("Done");
    }
}
