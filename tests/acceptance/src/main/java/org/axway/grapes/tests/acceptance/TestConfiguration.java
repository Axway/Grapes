package org.axway.grapes.tests.acceptance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TestConfiguration.class);
    private static TestConfiguration INSTANCE = null;

    private static final String PROPERTY_DB_HOST    = "mongo.host";
    private static final String DEFAULT_DB_HOST     = "localhost";
    private String dbHost;

    private static final String PROPERTY_DB_PORT    = "mongo.port";
    private static final String DEFAULT_DB_PORT     = "27017";
    private String dbPort;

    private static final String PROPERTY_DB_NAME    = "grapes.database.name";
    private static final String DEFAULT_DB_NAME     = "test";
    private String dbName;

    private static final String PROPERTY_DB_USER    = "mongo.user";
    private String dbUser;

    private static final String PROPERTY_DB_PWD     = "mongo.password";
    private String dbPassword;

    private static final String PROPERTY_GRAPES_HOST    = "grapes.host";
    private static final String DEFAULT_GRAPES_HOST     = "localhost";
    private String grapesHost;

    private static final String PROPERTY_GRAPES_PORT    = "grapes.server.port";
    private static final String DEFAULT_GRAPES_PORT     = "8080";
    private String grapesPort;

    private static final String PROPERTY_GRAPES_ADMIN_PORT    = "grapes.server.admin.port";
    private static final String DEFAULT_GRAPES_ADMIN_PORT     = "8081";
    private String grapesAdminPort;

    private static final String PROPERTY_GRAPES_USER_NOTIFIER    = "grapes.user.notifier";
    private String grapesNotifier;

    private static final String PROPERTY_GRAPES_USER_NOTIFIER_PWD    = "grapes.user.notifier.pwd";
    private String grapesNotifierPwd;

    private static final String PROPERTY_GRAPES_ADMIN_USER    = "grapes.user.admin";
    private String grapesAdminUser;

    private static final String PROPERTY_GRAPES_ADMIN_PWD    = "grapes.user.admin.pwd";
    private String grapesAdminPassword;

    private TestConfiguration(){
        dbHost = System.getProperty(PROPERTY_DB_HOST, null);
        if(dbHost == null){
            LOG.info("No variable define for the database host, the default value will be used: " + DEFAULT_DB_HOST);
            dbHost = DEFAULT_DB_HOST;
        }

        dbPort = System.getProperty(PROPERTY_DB_PORT, null);
        if(dbPort == null){
            LOG.info("No variable define for the database port, the default value will be used " + DEFAULT_DB_PORT);
            dbPort = DEFAULT_DB_PORT;
        }

        dbName = System.getProperty(PROPERTY_DB_NAME, null);
        if(dbName == null){
            LOG.info("No variable define for the database name, the default value will be used " + DEFAULT_DB_NAME);
            dbName = DEFAULT_DB_NAME;
        }

        dbUser = System.getProperty(PROPERTY_DB_USER, null);
        dbPassword = System.getProperty(PROPERTY_DB_PWD, null);

        grapesHost = System.getProperty(PROPERTY_GRAPES_HOST, null);
        if(grapesHost == null){
            LOG.info("No variable define for the Dependency Manager host, the default value will be used " + DEFAULT_GRAPES_HOST);
            grapesHost = DEFAULT_GRAPES_HOST;
        }

        grapesPort = System.getProperty(PROPERTY_GRAPES_PORT, null);
        if(grapesPort == null){
            LOG.info("No variable define for the Dependency Manager port, the default value will be used " + DEFAULT_GRAPES_PORT);
            grapesPort = DEFAULT_GRAPES_PORT;
        }

        grapesAdminPort = System.getProperty(PROPERTY_GRAPES_ADMIN_PORT, null);
        if(grapesAdminPort == null){
            LOG.info("No variable define for the Dependency Manager administration port, the default value will be used " + DEFAULT_GRAPES_ADMIN_PORT);
            grapesAdminPort = DEFAULT_GRAPES_ADMIN_PORT;
        }

        grapesNotifier = System.getProperty(PROPERTY_GRAPES_USER_NOTIFIER, null);
        grapesNotifierPwd = System.getProperty(PROPERTY_GRAPES_USER_NOTIFIER_PWD, null);

        grapesAdminUser = System.getProperty(PROPERTY_GRAPES_ADMIN_USER, null);
        String adminPwd = System.getProperty(PROPERTY_GRAPES_ADMIN_PWD, null);
        if(adminPwd != null){
            grapesAdminPassword = adminPwd;
        }
    }

    public static synchronized TestConfiguration getInstance() {
        if (INSTANCE == null){
            INSTANCE = new TestConfiguration();
        }
        return INSTANCE;
    }


    public String getGrapesAdminPort() {
        return grapesAdminPort;
    }

    public String getGrapesPort() {
        return grapesPort;
    }

    public String getGrapesHost() {
        return grapesHost;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getGrapesNotifier() {
        return grapesNotifier;
    }

    public String getGrapesNotifierPwd() {
        return grapesNotifierPwd;
    }

    public String getGrapesBaseUrl(){
        final StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(grapesHost);
        sb.append(":");
        sb.append(grapesPort);

        return sb.toString();
    }

    public String getGrapesAdminUser() {
        return grapesAdminUser;
    }

    public String getGrapesAdminPassword() {
        return grapesAdminPassword;
    }
}
