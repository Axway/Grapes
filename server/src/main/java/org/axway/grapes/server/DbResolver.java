package org.axway.grapes.server;

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
    
    public static RepositoryHandler getNewRepoHandler(final DataBaseConfig config) throws DBException, UnknownHostException{
        if(MONGO.equalsIgnoreCase(config.getDbsystem())){
            final MongodbHandler dbHandler = new MongodbHandler(config);
            return dbHandler;
        }
        
        throw new DBException("Cannot find any matching database system currently implemented.");
    }
}
