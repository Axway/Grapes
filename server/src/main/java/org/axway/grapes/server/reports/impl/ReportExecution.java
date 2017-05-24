package org.axway.grapes.server.reports.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportExecution {

    private static final Logger LOG = LoggerFactory.getLogger(ReportExecution.class);


    private ReportRequest request;

    public String[] getResultColumnNames() {
        return resultColumnNames;
    }

    private String[] resultColumnNames;
    private List<String[]> tabularData;

    public ReportExecution(final ReportRequest request, final String[] colNames) {
        this.request = request;
        this.resultColumnNames = colNames;

        tabularData = new ArrayList<>();
    }

    public ReportRequest getRequest() {
        return request;
    }

    public List<String[]> getData() {
        return Collections.unmodifiableList(tabularData);
    }

    public void addResultRow(String[] row) {
        if(row.length < resultColumnNames.length) {
            LOG.warn(String.format("Required column count: %s, got: %s", resultColumnNames.length, row.length));
            throw new IllegalArgumentException("Invalid row data");
        }

        tabularData.add(row);
    }
}
