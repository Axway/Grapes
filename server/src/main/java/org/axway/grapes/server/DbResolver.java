package org.axway.grapes.server;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.axway.grapes.server.config.DataBaseConfig;
import org.axway.grapes.server.db.DBException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.mongo.MongodbHandler;

import java.net.UnknownHostException;

/**
 * Db Resolver
 * 
 * <p>This utility class provide the resolution between the dbsystem configuration and the repository handler to use.</p>
 * 
 * @author jdcoffre
 */
public final class DbResolver {

    private static final String MONGO = "mongodb";
    
    private DbResolver(){
        // Utility class should never be instanciate
    }

    private static DB makeDB(DataBaseConfig config) throws UnknownHostException {
        final ServerAddress address = new ServerAddress(config.getHost() , config.getPort());
        final MongoClient mongo = new MongoClient(address);
        return mongo.getDB(config.getDatastore());
    }

    public static RepositoryHandler getNewRepoHandler(final DataBaseConfig config) throws DBException, UnknownHostException{
        if(MONGO.equalsIgnoreCase(config.getDbsystem())) {
            return new MongodbHandler(config, makeDB(config));
        }

/*

//        this.dbFactory = dbFactory;
        this.db = dbFactory.create();

//        final MongoClient mongo = new MongoClient(address);
//        db = mongo.getDB(config.getDatastore());
* */

        throw new DBException("Cannot find any matching database system currently implemented.");
    }
}
