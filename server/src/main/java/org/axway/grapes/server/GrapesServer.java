package org.axway.grapes.server;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import net.sourceforge.argparse4j.inf.Namespace;

import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.ServiceHandler;
import org.axway.grapes.server.db.DBException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.axway.grapes.server.webapp.healthcheck.DataBaseCheck;
import org.axway.grapes.server.webapp.healthcheck.DataModelVersionCheck;
import org.axway.grapes.server.webapp.resources.*;
import org.axway.grapes.server.webapp.tasks.*;
import org.axway.grapes.server.webapp.tasks.migrate.MigrationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Grapes service class.
 * This is the main entry point of the project.
 * This class starts and registers the different resources of Grapes.
 *
 * This server is entirely based on dropwizard library:
 * http://dropwizard.codahale.com/manual
 *
 * INFO:
 * All the resources of Grapes server are available in org.axway.grapes.server.resources.
 *
 * @author jdcoffre
 */
public class GrapesServer extends Service<GrapesServerConfig> {

	private static final Logger LOG = LoggerFactory.getLogger(GrapesServer.class);

    protected GrapesServer() {
        super();
    }

	/**
	 * Runs Grapes
	 */
	public static void main(final String[] args) throws ExceptionInInitializerError {
        try {
            final GrapesServer grapesServer = new GrapesServer();
            grapesServer.run(args);
		} catch (Exception e) {
			LOG.error("Grapes server failed to start:" + e.getMessage());
            throw new ExceptionInInitializerError(e);
		}
    }

	@Override
	public void initialize(final Bootstrap<GrapesServerConfig> bootstrap) {

		bootstrap.setName("Grapes");

		// Create assets bundle to ass static resources
		bootstrap.addBundle(new AssetsBundle("/public/", "/public"));
		bootstrap.addBundle(new AssetsBundle("/assets/", "/assets"));

		bootstrap.addBundle(new ViewBundle());
	}

	@Override
	public void run(final GrapesServerConfig config, final Environment env) throws DBException, UnknownHostException {
		// init the repoHandler
        final RepositoryHandler repoHandler = getRepositoryHandler(config);
        
        // init the serviceHandler
        final ServiceHandler serviceHandler = new ServiceHandler(config);
        // load error Messages - start service
        serviceHandler.loadErrorMessages();
        // start grapes email service
        serviceHandler.startGrapesEmailService();
        
        // Add credential management
        final GrapesAuthenticator grapesAuthenticator = new GrapesAuthenticator(repoHandler);
        final BasicAuthProvider authProvider = new BasicAuthProvider<DbCredential>(grapesAuthenticator, "Grapes Authenticator Provider");
        env.addProvider(authProvider);
        
        
        // Tasks
        env.addTask(new AddUserTask(repoHandler));
        env.addTask(new AddRoleTask(repoHandler));
        env.addTask(new RemoveRoleTask(repoHandler));
        env.addTask(new MaintenanceModeTask(config));
        env.addTask(new KillTask());
        env.addTask(new MigrationTask(config.getDataBaseConfig()));
        env.addTask(new LoggingTask(config.getLoggingConfiguration().getFileConfiguration().getCurrentLogFilename()));

        
        // Health checks
        env.addHealthCheck(new DataBaseCheck(config.getDataBaseConfig()));
        env.addHealthCheck(new DataModelVersionCheck(config.getDataBaseConfig()));

        // Resources
        env.addResource(new OrganizationResource(repoHandler, serviceHandler, config));
        env.addResource(new ProductResource(repoHandler, serviceHandler, config));
        env.addResource(new ModuleResource(repoHandler, serviceHandler, config));
        env.addResource(new ArtifactResource(repoHandler, serviceHandler, config));
        env.addResource(new LicenseResource(repoHandler, serviceHandler, config));
        env.addResource(new Sequoia(repoHandler, serviceHandler, config));
        env.addResource(new WebAppResource(repoHandler, serviceHandler, config));
        env.addResource(new RootResource(repoHandler, serviceHandler, config));

	}

    public RepositoryHandler getRepositoryHandler(final GrapesServerConfig config) throws DBException, UnknownHostException {
        return DbResolver.getNewRepoHandler(config.getDataBaseConfig());
    }

}