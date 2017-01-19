package org.axway.grapes.server.webapp.links;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class JiraLinkGeneratorTest {
    @Test
    public void validEncodedDataTest() {
        final String rootLink = "www.some-link.net&";
        JiraLinkGenerator jiraLink = new JiraLinkGenerator(rootLink);
        String expectedString = rootLink + "summary=Summary&description=Description&reporter=Toto";
        String generatedLink = jiraLink.generateLink("Summary", "Description", "Toto");
        assertEquals(expectedString,generatedLink);
    }
    
    @Test
    public void validEncodedDataTestWithSpace() {
        final String rootLink = "www.some-link.net&";
        JiraLinkGenerator jiraLink=  new JiraLinkGenerator(rootLink);
        String expectedString = rootLink + "summary=This+is+the+Summary&description=This+is+the+Description&reporter=Toto";
        String generatedLink = jiraLink.generateLink("This is the Summary", "This is the Description", "Toto");
        assertEquals(expectedString,generatedLink);
    }
}