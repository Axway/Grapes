package org.axway.grapes.tests.acceptance.steps;

import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.axway.grapes.tests.acceptance.TestConfiguration;
import org.axway.grapes.utils.client.GrapesClient;

public class GrapesAbstractSteps extends ScenarioSteps{


    public GrapesAbstractSteps(Pages pages) {
        super(pages);
    }

    public GrapesClient getClient(){
        final String host = TestConfiguration.getInstance().getGrapesHost();
        final String port = TestConfiguration.getInstance().getGrapesPort();
        return new GrapesClient(host, port);
    }
}
