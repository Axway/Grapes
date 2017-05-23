package org.axway.grapes.server.db.mongo;

import com.mongodb.BasicDBObject;

import java.util.List;

/**
 *
 */
public class QueryUtils {

    public static String quoteIds(final List<String> ids, final String template) {
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

        String result = String.format(template, b.toString());
        // LOG.debug(result);
        return result;
    }


    public static String makeQuery(final String name, final String version) {
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
        return query.toString();
    }

}
