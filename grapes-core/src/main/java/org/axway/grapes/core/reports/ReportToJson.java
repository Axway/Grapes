package org.axway.grapes.core.reports;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.handler.ModuleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.content.Json;

/**
 * Created by jennifer on 8/12/15.
 */
@Service
public class ReportToJson {
    @Requires
    Json json;
    private static final Logger LOG = LoggerFactory.getLogger(ReportToJson.class);

    public ReportToJson() {
    }

    public ObjectNode promotionReportToJson(PromotionReport report){
            LOG.error("make json report");
       ObjectNode jsonReport = json.newObject();
        jsonReport.put("canBePromoted", report.canBePromoted());
        jsonReport.put("unPromoted", String.valueOf(report.getUnPromotedDependencies()));
        jsonReport.put("promotionPlan", String.valueOf(report.getPromotionPlan()));
        jsonReport.put("rootModel", String.valueOf(report.getRootModule()));
        jsonReport.put("doNotUseArtifacts", String.valueOf(report.getDoNotUseArtifacts().size()));
        return jsonReport;
//        return null;
    }
}
