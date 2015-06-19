package org.axway.grapes.core.webapi.healthchecks;

import org.wisdom.api.DefaultController;

/**
 * Database Check
 * 
 * <p>Implements Metrics health checks. Thanks to it, it is possible to know via Grapes admin webapp if the database connection is available. </p>
 * 
 * @author jdcoffre
 */
public class DataBaseCheck extends DefaultController{

	/*private final DataBaseConfig config;

	public DataBaseCheck(final DataBaseConfig dataBaseConfig) {
		super("database");
		this.config = dataBaseConfig;
	}

	@Override
	protected Result check() {	
        Mongo mongo = null;
        
		try{
			final ServerAddress adress = new ServerAddress(config.getHost() , config.getPort());
            mongo = new MongoClient(adress);

            final StringBuilder sb = new StringBuilder();
            sb.append("MogoDb version " + mongo.getVersion() + '\n');

            sb.append("  Available databases: ");
            for(String dbName: mongo.getDatabaseNames()){
                sb.append(dbName);
                sb.append(' ');
            }
            sb.append('\n');

            return Result.healthy(sb.toString());

		}
		catch (Exception e) {
			return Result.unhealthy(e);
		}
        finally{
            if(mongo != null){
                mongo.close();
            }
        }
	}
    */
}
