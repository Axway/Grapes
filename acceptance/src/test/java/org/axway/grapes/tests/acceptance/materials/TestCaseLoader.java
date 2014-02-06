package org.axway.grapes.tests.acceptance.materials;


import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.tests.acceptance.TestConfiguration;
import org.axway.grapes.tests.acceptance.materials.cases.TestCase;
import org.axway.grapes.tests.acceptance.materials.datamodel.DbArtifact;
import org.axway.grapes.tests.acceptance.materials.datamodel.DbLicense;
import org.axway.grapes.tests.acceptance.materials.datamodel.DbModule;
import org.axway.grapes.utils.client.GrapesClient;
import org.axway.grapes.utils.client.GrapesCommunicationException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.net.UnknownHostException;

public class TestCaseLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseLoader.class);

    private static TestCaseLoader INSTANCE = null;
    private TestConfiguration config;
    private Jongo datastore;

    private TestCaseLoader(){

        try {
            config = TestConfiguration.getInstance();
            final ServerAddress address = new ServerAddress(config.getDbHost() , Integer.valueOf(config.getDbPort()));
            final MongoClient mongo = new MongoClient(address);
            final DB db = mongo.getDB(config.getDbName());

            if(config.getDbUser() != null && config.getDbPassword() != null){
                boolean auth = db.authenticate(config.getDbUser(), config.getDbPassword().toCharArray());

                if (auth) {
                    System.out.println("Login is successful!");
                } else {
                    System.out.println("Login is failed!");
                }
            }

            datastore = new Jongo(db);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            LOG.error("Error during mongo client initialization.", e);
        }
    }

    public static synchronized TestCaseLoader getInstance() {
        if (INSTANCE == null){
            INSTANCE = new TestCaseLoader();
        }
        return INSTANCE;
    }

    public void dropDatabase() {
        datastore.getCollection(DbModule.class.getSimpleName()).drop();
        datastore.getCollection(DbArtifact.class.getSimpleName()).drop();
        datastore.getCollection(DbLicense.class.getSimpleName()).drop();
    }


    private void sendArtifactDirectlyInDb(final DbArtifact artifact){
        final MongoCollection collection = datastore.getCollection(DbArtifact.class.getSimpleName());
        final WriteResult result = collection.save(artifact);

        if(result.getError() != null){
            LOG.error(result.getError());
        }

        LOG.info("Artifact  "+ artifact.getGavc()  + " sent");
    }

    private void sendModuleDirectlyInDb(final DbModule module) {
        final MongoCollection collection = datastore.getCollection(DbModule.class.getSimpleName());
        final WriteResult result = collection.save(module);

        if(result.getError() != null){
            LOG.error(result.getError());
        }

        LOG.info("Module " + module.getUid() + " sent");
    }

    private void sendLicenseDirectlyInDb(final DbLicense license){
        final MongoCollection collection = datastore.getCollection(DbLicense.class.getSimpleName());
        final WriteResult result = collection.save(license);

        if(result.getError() != null){
            LOG.error(result.getError());
        }

        LOG.info("License " + license.getName() + " sent");
    }


    public void loadTestCase(final TestCase testCase) throws AuthenticationException, GrapesCommunicationException {
        final GrapesClient client = new GrapesClient(config.getGrapesHost(), config.getGrapesPort());

        for(License license: testCase.getLicenses()){
            client.postLicense(license, config.getGrapesNotifier(), config.getGrapesNotifierPwd());
        }

        for(Module module: testCase.getModules()){
            client.postModule(module, config.getGrapesNotifier(), config.getGrapesNotifierPwd());
        }

        for(License license: testCase.getLicenses()){
            for(Artifact artifact: testCase.getArtifacts()){
                if(artifact.getLicenses().contains(license.getName())){
                    client.addLicense(artifact.getGavc(), license.getName(), config.getGrapesNotifier(), config.getGrapesNotifierPwd());
                }
            }
        }

        for(String gavc: testCase.getArtifactsToNotUse()){
            client.postDoNotUseArtifact(gavc, true, config.getGrapesNotifier(), config.getGrapesNotifierPwd());
        }

    }
}
