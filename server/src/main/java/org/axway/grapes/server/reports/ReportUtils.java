package org.axway.grapes.server.reports;

import org.axway.grapes.server.reports.models.ParameterDefinition;
import org.axway.grapes.server.reports.models.ReportRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ReportUtils {
    public static ReportRequest generateSampleRequest(Report report, Map<String, String> params) {
        ReportRequest result = new ReportRequest();
        result.setReportId(report.getId().getId());

        Map<String, String> requestParams = new HashMap<>();

        report.getParameters().forEach(param -> {
            if(params.containsKey(param.getName())) {
                requestParams.put(param.getName(), params.get(param.getName()));
            } else {
                requestParams.put(param.getName(), String.format("Sample %s value", param.getName()));
            }
        });
        result.setParamValues(requestParams);

        return result;
    }
}
