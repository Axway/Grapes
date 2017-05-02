package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.tmp.ParameterDefinition;
import org.axway.grapes.server.tmp.Report;
import org.axway.grapes.server.tmp.ReportExecution;
import org.axway.grapes.server.tmp.ReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LicenseReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseReport.class);

    static List<ParameterDefinition> parameters = new ArrayList<>();

    static {
        parameters.add(new ParameterDefinition("name", "Commercial Name"));
        parameters.add(new ParameterDefinition("version", "Commercial Version"));
    }

    @Override
    public String getName() {
        return "Licenses Used in Product Release";
    }

    @Override
    public String getDescription() {
        return "Displays all the licenses used by dependencies of a product commercial release";
    }

    @Override
    public ReportId getId() {
        return ReportId.LICENSES_PER_PRODUCT_RELEASE;
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public ReportExecution execute(final ReportRequest request, final String[] colNames) {
        LOG.debug(String.format("Executing %s", getName()));
        ReportExecution result = new ReportExecution(request, colNames);

        result.addResultRow(new String[] {"Phenny Mae", "1.2.5"});
        result.addResultRow(new String[] {"WA MU", "11.22.5"});
        result.addResultRow(new String[] {"Barclays", "0.0.75"});

        return result;
    }
}
