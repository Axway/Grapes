package org.axway.grapes.tests.acceptance.stories;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.axway.grapes.tests.acceptance.TestConfiguration;
import org.axway.grapes.tests.acceptance.materials.TestCaseLoader;
import org.axway.grapes.tests.acceptance.materials.cases.*;
import org.axway.grapes.utils.client.GrapesCommunicationException;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;

import javax.naming.AuthenticationException;

public class AbstractStory {

    public final static TestCaseLoader dbClient = TestCaseLoader.getInstance();

    @BeforeScenario
    @AfterStories
    public void cleanDb(){
        dbClient.dropDatabase();
    }


    @AfterStories
    public void shutdownGrapes() {
        final TestConfiguration config = TestConfiguration.getInstance();
        final ClientConfig cfg = new DefaultClientConfig();
        cfg.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, 60000);
        cfg.getClasses().add(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);

        final Client jerseyClient = Client.create(cfg);
        jerseyClient.addFilter(new HTTPBasicAuthFilter(config.getGrapesAdminUser(), config.getGrapesAdminPassword()));

        final StringBuilder serverURL = new StringBuilder();
        serverURL.append("http://");
        serverURL.append(config.getGrapesHost());
        serverURL.append(":");
        serverURL.append(config.getGrapesAdminPort());
        serverURL.append("/tasks/kill");

        try{
            jerseyClient.resource(serverURL.toString()).post(ClientResponse.class);
        }
        catch (Exception e){
            //
        }

    }

    @Given("$testCaseName loaded in the database")
    public void loadSimpleModuleCase(final String testCaseName) throws AuthenticationException, GrapesCommunicationException {
        final TestCase testCase = resolveTestCase(testCaseName);
        dbClient.loadTestCase(testCase);
    }

    public final TestCase resolveTestCase(final String name){
        if(name.equalsIgnoreCase("SimpleModuleCase")){
            return new TC01_SimpleModuleCase();
        }
        if(name.equalsIgnoreCase("ModuleWithAllKindOfDependenciesCase")){
            return new TC02_ModuleWithAllKindOfDependenciesCase();
        }
        if(name.equalsIgnoreCase("SimpleAncestorCase")){
            return new TC03_SimpleAncestorCase();
        }
        if(name.equalsIgnoreCase("ProductCase")){
            return new TC04_ProductCase();
        }

        return null;
    }
}
