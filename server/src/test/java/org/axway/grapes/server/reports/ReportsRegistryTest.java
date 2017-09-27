package org.axway.grapes.server.reports;

import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.axway.grapes.server.reports.ReportId.LICENSES_PER_PRODUCT_RELEASE;

public class ReportsRegistryTest {

    @Test(expected = UnsupportedOperationException.class)
    public void registryReturnsReadOnlyCollection() {
        ReportsRegistry.allReports().clear();
    }

    @Test
    public void testNotFoundReport() {
        initReports();
        final Optional<Report> byId = ReportsRegistry.findById(-100);
        assertFalse(byId.isPresent());
    }

    @Test
    public void testReportIsCorrectlyIdentified() {
        initReports();
        final Optional<Report> byId = ReportsRegistry.findById(LICENSES_PER_PRODUCT_RELEASE.getId());
        assertTrue(byId.isPresent());
    }

    @Test
    public void testReportNoIdDuplicate() {
        initReports();
        Set<Integer> ids = new HashSet<>();
        ReportsRegistry.allReports().forEach(report -> assertTrue(ids.add(report.getId())));

    }

    private void initReports(){
        if(ReportsRegistry.allReports().isEmpty()){
            ReportsRegistry.init();
        }
    }
}
