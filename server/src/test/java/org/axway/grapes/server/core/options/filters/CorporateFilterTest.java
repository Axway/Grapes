package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.*;

public class CorporateFilterTest {

    @Test
    public void checkFilterModuleThatHasNoArtifactAndWhichIsNotCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);


        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");

        assertFalse(filter.filter(module));

    }

    @Test
    public void checkFilterModuleThatHasNoArtifactAndWhichIsCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);


        final DbModule module = new DbModule();
        module.setName(GrapesTestUtils.CORPORATE_GROUPID_4TEST+":module");
        module.setVersion("1.0.0");

        assertTrue(filter.filter(module));

    }

    @Test
    public void checkFilterModuleWithAnArtifactAndWhichIsNotCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);


        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");
        module.getArtifacts().add("org.apache.something.module:test:1.0.0::jar");

        assertFalse(filter.filter(module));

    }

    @Test
    public void checkFilterModuleWithAnArtifactAndWhichIsCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);


        final DbModule module = new DbModule();
        module.setName("module");
        module.setVersion("1.0.0");
        module.getArtifacts().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ".module:test:1.0.0::jar");

        assertTrue(filter.filter(module));

    }

    @Test
    public void checkFilterArtifactWhichIsNotCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);


        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("org.apache.something");

        assertFalse(filter.filter(artifact));

    }

    @Test
    public void checkFilterArtifactWhichIsCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);


        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);

        assertTrue(filter.filter(artifact));

    }


    @Test
    public void checkFilterDependencyWhichIsNotCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);

        final DbDependency dependency = new DbDependency();
        dependency.setTarget("com.company.all:test:1.0.0::");

        assertFalse(filter.filter(dependency));
    }


    @Test
    public void checkFilterDependencyWhichIsCorporate(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);

        final DbDependency dependency = new DbDependency();
        dependency.setTarget(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":test:1.0.0::");

        assertTrue(filter.filter(dependency));
    }

    @Test
    public void checkMongoRegExpGenerationForArtifacts(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        organization.getCorporateGroupIdPrefixes().add("my.corporate.gid");
        final CorporateFilter filter = new CorporateFilter(organization);

        Map<String, Object> params = filter.artifactFilterFields();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(DbArtifact.GROUPID_DB_FIELD, params.keySet().iterator().next());
        assertEquals(GrapesTestUtils.CORPORATE_GROUPID_4TEST + "*|my.corporate.gid*", params.values().iterator().next().toString());
    }



    @Test
    public void checkMongoRegExpGenerationForModules(){
        final DbOrganization organization = new DbOrganization();
        organization.setName("testOrganization");
        organization.getCorporateGroupIdPrefixes().add(GrapesTestUtils.CORPORATE_GROUPID_4TEST);
        final CorporateFilter filter = new CorporateFilter(organization);

        Map<String, Object> params = filter.moduleFilterFields();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals(DbModule.ORGANIZATION_DB_FIELD, params.keySet().iterator().next());
        assertEquals(organization.getName(), params.values().iterator().next());
    }
}
