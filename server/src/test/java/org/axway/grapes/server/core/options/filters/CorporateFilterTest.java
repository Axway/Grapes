package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

public class CorporateFilterTest {

    @Test
    public void checkModuleFilter(){
        final DbModule module = new DbModule();
        final CorporateFilter filter = new CorporateFilter(new ArrayList<String>());

        filter.setIsCorporate(true);
        assertTrue(filter.filter(module));

        filter.setIsCorporate(false);
        assertFalse(filter.filter(module));

    }

    @Test
    public void checkCorporateArtifactFilter(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);

        final DbArtifact thirdParty = new DbArtifact();
        thirdParty.setGroupId("org.somewhere");

        final CorporateFilter filter = new CorporateFilter(GrapesTestUtils.getTestCorporateGroupIds());
        filter.setIsCorporate(true);

        assertTrue(filter.filter(artifact));
        assertFalse(filter.filter(thirdParty));
    }

    @Test
    public void checkThirdPartyFilter(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId(GrapesTestUtils.CORPORATE_GROUPID_4TEST);

        final DbArtifact thirdParty = new DbArtifact();
        thirdParty.setGroupId("org.somewhere");

        final CorporateFilter filter = new CorporateFilter(GrapesTestUtils.getTestCorporateGroupIds());
        filter.setIsCorporate(false);

        assertFalse(filter.filter(artifact));
        assertTrue(filter.filter(thirdParty));

    }

    @Test
    public void checkCorporateDependencyFilter(){
        final DbDependency dependency = new DbDependency();
        dependency.setTarget(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":test:1.0.0-SNAPSHOT::jar");

        final DbDependency thirdParty = new DbDependency();
        thirdParty.setTarget("org.somewhere:thirdparty:1.0.0-6::jar");

        final CorporateFilter filter = new CorporateFilter(GrapesTestUtils.getTestCorporateGroupIds());
        filter.setIsCorporate(true);

        assertTrue(filter.filter(dependency));
        assertFalse(filter.filter(thirdParty));

    }

    @Test
    public void checkThirdPartyDependencyFilter(){
        final DbDependency dependency = new DbDependency();
        dependency.setTarget(GrapesTestUtils.CORPORATE_GROUPID_4TEST + ":test:1.0.0-SNAPSHOT::jar");

        final DbDependency thirdParty = new DbDependency();
        thirdParty.setTarget("org.somewhere:thirdparty:1.0.0-6::jar");

        final CorporateFilter filter = new CorporateFilter(GrapesTestUtils.getTestCorporateGroupIds());
        filter.setIsCorporate(false);

        assertFalse(filter.filter(dependency));
        assertTrue(filter.filter(thirdParty));
    }


    @Test
    public void checkCorporateArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("com.company.all");

        final List<String> corporateGroupIds = new ArrayList<String>();
        CorporateFilter filter = new CorporateFilter(corporateGroupIds);
        assertFalse(filter.matches(artifact));

        corporateGroupIds.add("com.company.all");
        assertTrue(filter.matches(artifact));
    }

    @Test
    public void checkCorporateDependency(){
        final DbDependency dependency = new DbDependency();
        dependency.setTarget("com.company.all:test:1.0.0::");

        final List<String> corporateGroupIds = new ArrayList<String>();
        CorporateFilter filter = new CorporateFilter(corporateGroupIds);
        assertFalse(filter.matches(dependency));

        corporateGroupIds.add("com.company.all");
        assertTrue(filter.matches(dependency));
    }

    @Test
    public void checkRegExpGeneration(){
        final List<String> corporateGroupIds = new ArrayList<String>();
        final CorporateFilter filter = new CorporateFilter(corporateGroupIds);
        assertNull(filter.getRegExp());

        corporateGroupIds.add("com.company.all");
        assertEquals("com.company.all*", filter.getRegExp().toString());

        corporateGroupIds.add("net.company.all");
        assertEquals("com.company.all*|net.company.all*", filter.getRegExp().toString());

    }
}
