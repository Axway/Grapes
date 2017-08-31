package org.axway.grapes.server.reports.impl;

import org.axway.grapes.server.core.LicenseHandler;
import org.axway.grapes.server.core.interfaces.LicenseMatcher;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.datamodel.DbLicense;
import org.axway.grapes.server.reports.Report;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.reports.models.ParameterDefinition;
import org.axway.grapes.server.reports.models.ReportExecution;
import org.axway.grapes.server.reports.models.ReportRequest;
import org.axway.grapes.server.reports.utils.DataFetchingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Report showing third party artifacts depending on the organization name provided
 * as argument.
 */
public class MultipleMatchingReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(MultipleMatchingReport.class);

    static List<ParameterDefinition> parameters = new ArrayList<>();

    static {
        parameters.add(new ParameterDefinition("organization", "Organization Name"));
    }

    @Override
    public String getName() {
        return "Artifacts matching multiple licenses";
    }

    @Override
    public String getDescription() {
        return "Artifacts which contain license strings that match more than one license";
    }

    @Override
    public int getId() {
        return ReportId.MULTIPLE_LICENSE_MATCHING_STRINGS.getId();
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Artifact", "String", "Matching"};
    }

    @Override
    public ReportExecution execute(RepositoryHandler repoHandler, ReportRequest request) {
        final LicenseMatcher licenseMatcher = new LicenseHandler(repoHandler);

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Executing %s", getName()));
        }

        final Map<String, String> params = request.getParamValues();
        final String orgName = params.get("organization");

        final DataFetchingUtils dataUtils = new DataFetchingUtils();
        dataUtils.initCorporateIDs(repoHandler, orgName);

        final ReportExecution result = new ReportExecution(request, getColumnNames());

        repoHandler.consumeByQuery(DbCollections.DB_ARTIFACTS, "", DbArtifact.class,
                a -> {
                    if (dataUtils.isThirdParty(a)) {
                        a.getLicenses().forEach(licString -> {
                            final Set<DbLicense> matchingLicenses = licenseMatcher.getMatchingLicenses(licString);
                            if (matchingLicenses.size() > 1) {
                                result.addResultRow(new String[]{a.getGavc(), licString, matchingLicenses.toString()});
                            }
                        });
                    }
                });

        if(result.getData().isEmpty()) {
            result.addResultRow(new String[] {"All OK. All regexp strings are disjoint", "", ""});
        }

        return result;
    }
}