package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.reports.models.ParameterDefinition;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.reports.utils.DataFetchingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Report showing third party artifacts depending on the organization name provided
 * as argument.
 */
public class ThirdPartyArtifactsReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(ThirdPartyArtifactsReport.class);

    static List<ParameterDefinition> parameters = new ArrayList<>();

    static {
        parameters.add(new ParameterDefinition("organization", "Organization Name"));
    }

    @Override
    public String getName() {
        return "List of third party artifacts";
    }

    @Override
    public String getDescription() {
        return "Displays the full list of artifacts considered third-party";
    }

    @Override
    public int getId() {
        return ReportId.THIRD_PARTY_ARTIFACTS.getId();
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Artifact Identifier"};
    }

    @Override
    public ReportExecution execute(RepositoryHandler repoHandler, ReportRequest request) {
        if(LOG.isDebugEnabled()) {
            LOG.debug(String.format("Executing %s", getName()));
        }

        final Map<String, String> params = request.getParamValues();
        final String orgName = params.get("organization");


        final ReportExecution result = new ReportExecution(request, getColumnNames());

        DataFetchingUtils data = new DataFetchingUtils();
        data.initCorporateIDs(repoHandler, orgName);

        repoHandler.consumeByQuery(DbCollections.DB_ARTIFACTS,
                "",
                DbArtifact.class,
                artifact -> {
                    if(data.isThirdParty(artifact)) {
                        result.addResultRow(new String[]{artifact.getGavc()});
                    }
                } );

        return result;
    }
}