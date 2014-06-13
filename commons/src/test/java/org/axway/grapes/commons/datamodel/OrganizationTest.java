package org.axway.grapes.commons.datamodel;


import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OrganizationTest {


    @Test
    public void checkToString(){
        final Organization organization = new Organization();
        organization.setName("organizationTest");
        organization.getCorporateGroupIdPrefixes().add("com.test");
        organization.getCorporateGroupIdPrefixes().add("net.test");
        organization.getCorporateGroupIdPrefixes().add("org.test");

        assertEquals("name: organizationTest, corporateGroupIds: com.test net.test org.test", organization.toString());

    }


    @Test
    public void checkEquals(){
        final Organization organization1 = new Organization();
        final Organization organization2 = new Organization();
        assertEquals(organization1, organization2);

        organization1.setName("organizationTest");
        organization1.getCorporateGroupIdPrefixes().add("com.test");
        organization1.getCorporateGroupIdPrefixes().add("net.test");
        organization1.getCorporateGroupIdPrefixes().add("org.test");
        assertNotEquals(organization1, organization2);

        organization2.setName("organizationTest");
        assertNotEquals(organization1, organization2);

        organization2.getCorporateGroupIdPrefixes().add("com.test");
        assertNotEquals(organization1, organization2);

        organization2.getCorporateGroupIdPrefixes().add("net.test");
        organization2.getCorporateGroupIdPrefixes().add("org.test");
        assertEquals(organization1, organization2);
    }


    @Test
    public void checkEqualsDisordered(){
        final Organization organization1 = new Organization();
        organization1.setName("organizationTest");
        organization1.getCorporateGroupIdPrefixes().add("com.test");
        organization1.getCorporateGroupIdPrefixes().add("net.test");
        organization1.getCorporateGroupIdPrefixes().add("org.test");

        final Organization organization2 = new Organization();
        organization2.setName("organizationTest");
        organization2.getCorporateGroupIdPrefixes().add("org.test");
        organization2.getCorporateGroupIdPrefixes().add("com.test");
        organization2.getCorporateGroupIdPrefixes().add("net.test");

        assertEquals(organization1, organization2);
    }
}
