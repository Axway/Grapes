package org.axway.grapes.server;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.config.Messages;
import org.axway.grapes.server.db.DBException;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.promo.validations.PromoConfigPrinter;
import org.axway.grapes.server.promo.validations.PromoConfigValidator;
import org.axway.grapes.server.reports.writer.CsvReportWriter;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.axway.grapes.server.webapp.healthcheck.DataBaseCheck;
import org.axway.grapes.server.webapp.healthcheck.DataModelVersionCheck;
import org.axway.grapes.server.webapp.resources.*;
import org.axway.grapes.server.webapp.tasks.*;
import org.axway.grapes.server.webapp.tasks.migrate.MigrationTask;
import org.axway.grapes.server.webapp.tasks.validations.ChangeValidationsTask;
import org.axway.grapes.server.webapp.tasks.validations.ShowValidationsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

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
        PromoConfigValidator v = new PromoConfigValidator();
        v.testValidity(config.getPromoValidationCfg());
        PromoConfigPrinter.display(config.getPromoValidationCfg(),
                str -> {
                    if (LOG.isInfoEnabled()) {
                        LOG.info(str);
                    }
                });

        // init the repoHandler
        final RepositoryHandler repoHandler = getRepositoryHandler(config);
        
        Messages.init(config.getMsgBundle());
        env.scanPackagesForResourcesAndProviders(CsvReportWriter.class);

        // Add credential management
        final GrapesAuthenticator grapesAuthenticator = new GrapesAuthenticator(repoHandler);
        final BasicAuthProvider authProvider = new BasicAuthProvider<>(grapesAuthenticator, "Grapes Authenticator Provider");
        env.addProvider(authProvider);
        
        
        // Tasks
        env.addTask(new AddUserTask(repoHandler));
        env.addTask(new AddRoleTask(repoHandler));
        env.addTask(new RemoveRoleTask(repoHandler));
        env.addTask(new MaintenanceModeTask(config));
        env.addTask(new KillTask());
        env.addTask(new MigrationTask(config.getDataBaseConfig()));
        env.addTask(new RefreshCommercialDeliveriesTask(repoHandler));
        env.addTask(new SendEmailTestTask(config.getGrapesEmailConfig()));
        env.addTask(new ShowValidationsTask());
        env.addTask(new ChangeValidationsTask());

        
        // Health checks
        env.addHealthCheck(new DataBaseCheck(config.getDataBaseConfig()));
        env.addHealthCheck(new DataModelVersionCheck(config.getDataBaseConfig()));

        // Resources
        env.addResource(new OrganizationResource(repoHandler, config));
        env.addResource(new ProductResource(repoHandler, config));
        env.addResource(new ModuleResource(repoHandler, config));
        env.addResource(new ArtifactResource(repoHandler, config));
        env.addResource(new LicenseResource(repoHandler, config));
        env.addResource(new Sequoia(repoHandler, config));
        env.addResource(new WebAppResource(repoHandler, config));
        env.addResource(new RootResource(repoHandler, config));
        env.addResource(new ReportResource(repoHandler, config));
        env.addResource(new SearchResource(repoHandler, config));
        env.addResource(new WebSearchResource(repoHandler, config));
	}

    public RepositoryHandler getRepositoryHandler(final GrapesServerConfig config) throws DBException, UnknownHostException {
        return DbResolver.getNewRepoHandler(config.getDataBaseConfig());
    }

}