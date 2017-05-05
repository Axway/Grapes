package org.axway.grapes.server.reports.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportRequest {

    private int reportId = 0;
    private Map<String, String> paramValues = new HashMap<>();

    public ReportRequest() {
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

    public static void main(String[] args) throws IOException {
        ReportRequest instance = new ReportRequest();
        instance.setReportId(12);

        Map<String, String> params = new HashMap<>();
        params.put("name", "Gateway");
        params.put("version", "5.9.87");
        params.put("delta", "plus");

        instance.setParamValues(params);

        ObjectMapper m = new ObjectMapper();
        m.writeValue(System.out, instance);
    }
}
