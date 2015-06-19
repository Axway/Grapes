package org.axway.grapes.core.webapi.healthchecks;



import org.wisdom.api.DefaultController;

/**
 * Database Check
 * 
 * <p>Implements Metrics health checks. Thanks to it, it is possible to know via Grapes admin webapp if the database connection is available. </p>
 * 
 * @author jdcoffre
 */
public class DataModelVersionCheck extends DefaultController {

	/*private final DataBaseConfig config;

	public DataModelVersionCheck(final DataBaseConfig dataBaseConfig) {
		super("data-model-version");
		this.config = dataBaseConfig;
	}

	@Override
	protected Result check() {
        Mongo mongo = null;
        
		try{
			final ServerAddress adress = new ServerAddress(config.getHost() , config.getPort());
            mongo = new MongoClient(adress);
            final DB db = mongo.getDB(config.getDatastore());

            if(config.getUser() != null && config.getPwd() != null){
                db.authenticate(config.getUser(), config.getPwd());
            }

            final Jongo jongo = new Jongo(db);
            final DbGrapesInfo info = jongo.getCollection(DbCollections.DB_GRAPES_INFO).findOne().as(DbGrapesInfo.class);

            if(info == null){
                return Result.healthy("not found\n");
            }

            return Result.healthy(info.getDatamodelVersion()+'\n');
		}
		catch (Exception e) {
			return Result.unhealthy(e);
		}
        finally{
            if(mongo != null){
                mongo.close();
            }
        }
	}*/
    
}
