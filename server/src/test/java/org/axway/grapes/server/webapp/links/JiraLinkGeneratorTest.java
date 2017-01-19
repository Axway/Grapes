package org.axway.grapes.server.webapp.links;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import org.axway.grapes.server.webapp.links.JiraLinkGenerator;

public class JiraLinkGeneratorTest {
    @Test
    public void validEncodedDataTest() {
        JiraLinkGenerator jiraLink=new JiraLinkGenerator();
        String expectedString = JiraLinkGenerator.JIRA_LINK + "pid=13820&issuetype=3&priority=3&summary=Summary&description=Description&reporter=Toto";
        String generatedLink = jiraLink.generateLink("Summary", "Description", "Toto");
            assertEquals(expectedString,generatedLink);
    }
    
    @Test
    public void validEncodedDataTestWithSpace() {
        JiraLinkGenerator jiraLink=new JiraLinkGenerator();
        String expectedString = JiraLinkGenerator.JIRA_LINK + "pid=13820&issuetype=3&priority=3&summary=This+is+the+Summary&description=This+is+the+Description&reporter=Toto";
        String generatedLink = jiraLink.generateLink("This is the Summary", "This is the Description", "Toto");
        assertEquals(expectedString,generatedLink);
    }
    
    @Test
    public void validDefaultFields() {
        JiraLinkGenerator jiraLink=new JiraLinkGenerator();
        Map<String, Object> defaultValues=jiraLink.getFixedFields();
        
        // checking if all fields are existing
        assertTrue(defaultValues.containsKey("pid"));
        assertTrue(defaultValues.containsKey("issuetype"));
        assertTrue(defaultValues.containsKey("priority"));
    }
    
    @Test
    public void validDefaultValues() {
        JiraLinkGenerator jiraLink=new JiraLinkGenerator();
        Map<String, Object> defaultValues=jiraLink.getFixedFields();

        // checking if all default values are correct
        assertEquals(13820, defaultValues.get("pid"));
        assertEquals(3, defaultValues.get("issuetype"));
        assertEquals(3, defaultValues.get("priority"));
    }

}
