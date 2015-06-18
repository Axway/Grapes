package org.axway.grapes.core.webapi.tasks.migrate;

import org.wisdom.api.DefaultController;

public class MigrationTask extends DefaultController {

  /*  private final DataBaseConfig config;
    private MongoClient mongo;

    public MigrationTask(final DataBaseConfig configuration) {
        super("migrate");
        this.config = configuration;
    }

    @Override
    public void execute(final ImmutableMultimap<String, String> stringStringImmutableMultimap, final PrintWriter printWriter) throws Exception {
        final Jongo db = initDBConnection();

        final DbGrapesInfo info = db.getCollection(DbCollections.DB_GRAPES_INFO).findOne().as(DbGrapesInfo.class);

        if(info == null){
            Migration220.perform(db, printWriter);
            return;
        }

        mongo.close();
        printWriter.println("Your database is up-to-date.");
        printWriter.flush();

    }


    private Jongo initDBConnection() throws Exception {
        final ServerAddress address = new ServerAddress(config.getHost() , config.getPort());
        mongo = new MongoClient(address);
        final DB db = mongo.getDB(config.getDatastore());

        if(config.getUser() != null && config.getPwd() != null){
            db.authenticate(config.getUser(), config.getPwd());
        }

        return new Jongo(db);

    }*/
}
