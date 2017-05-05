package org.axway.grapes.server;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.reports.impl.CsvReportWriter;
import org.axway.grapes.server.reports.impl.ReportResource;
import org.axway.grapes.server.reports.impl.ReportsLoader;
import org.axway.grapes.server.webapp.healthcheck.DataModelVersionCheck;

/**
 * Created by mganuci on 4/28/17.
 */
public class ReportsServer extends Service<GrapesServerConfig> {

    private RepositoryHandler repoHandler;

    @Override
    public void initialize(Bootstrap<GrapesServerConfig> bootstrap) {
        bootstrap.setName("Reporting Service");
    }

    @Override
    public void run(GrapesServerConfig config, Environment env) throws Exception {
        env.setJerseyProperty("com.sun.jersey.api.json.POJOMappingFeature", true);
        env.scanPackagesForResourcesAndProviders(CsvReportWriter.class);
        ReportsLoader.init();

        repoHandler = DbResolver.getNewRepoHandler(config.getDataBaseConfig());
        env.addHealthCheck(new DataModelVersionCheck(config.getDataBaseConfig()));
        env.addResource(new ReportResource(repoHandler, config));

    }




    public static void main(String[] args) {
        try {
            ReportsServer server = new ReportsServer();
            server.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
