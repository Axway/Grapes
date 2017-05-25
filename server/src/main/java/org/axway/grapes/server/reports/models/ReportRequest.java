package org.axway.grapes.server.reports.models;

import java.util.HashMap;
import java.util.Map;

public class ReportRequest {

    private int reportId = 0;
    private Map<String, String> paramValues;

    public ReportRequest() {
        paramValues = new HashMap<>();
    }


    public int getReportId() {
        return reportId;
    }

    public Map<String, String> getParamValues() {
        return paramValues;
    }

    public void setReportId(int id) {
        this.reportId = id;
    }

    public void setParamValues(Map<String, String> paramValues) {
        this.paramValues = paramValues;
    }

    @Override
    public String toString() {
        return "ReportRequest{" +
                "reportId=" + reportId +
                ", paramValues=" + paramValues +
                '}';
    }
}
