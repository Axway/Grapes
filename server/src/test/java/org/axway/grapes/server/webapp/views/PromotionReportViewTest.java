package org.axway.grapes.server.webapp.views;

import org.axway.grapes.commons.datamodel.DataModelFactory;
import org.axway.grapes.commons.datamodel.PromotionEvaluationReport;
import org.axway.grapes.commons.datamodel.ReportMessage;
import org.axway.grapes.server.webapp.resources.PromotionReportTranslator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PromotionReportViewTest {

    @Test
    public void testNullRootModuleIsNPESafe() {
        PromotionReportView sut = new PromotionReportView();
        sut.compute();

        // NO NPE being thrown
    }

    @Test
    public void testMissingDepReportsIsNPESafe() {
        PromotionReportView sut = new PromotionReportView();
        sut.setRootModule(DataModelFactory.createModule("a name", "123"));
        sut.addUnPromotedDependency("abc:def:42");
        sut.addUnPromotedDependency("abc1:def1:421");
        sut.compute();

        // NO NPE being thrown
    }


    @Test
    public void testTranslatorAcceptsNullViews() {
        final PromotionEvaluationReport report = PromotionReportTranslator.toReport(null);

        assertNotNull(report);
        assertTrue(report.isPromotable());
        assertTrue(report.getMessages().size() == 1);

        final ReportMessage msg = report.getMessages().iterator().next();
        assertTrue(msg.getBody().contains("Null argument"));
    }


}
