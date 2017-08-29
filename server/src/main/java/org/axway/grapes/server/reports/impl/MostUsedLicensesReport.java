package org.axway.grapes.server.reports.impl;

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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Report which are the most popular strings which are part of the artifact license strings
 */
public class MostUsedLicensesReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(MostUsedLicensesReport.class);

    static List<ParameterDefinition> parameters = new ArrayList<>();

    static {
        parameters.add(new ParameterDefinition("organization", "Organization Name"));
    }

    @Override
    public String getName() {
        return "List of most used license strings";
    }

    @Override
    public String getDescription() {
        return "Shows the list of most matched strings against the license entity regular expressions";
    }

    @Override
    public int getId() {
        return ReportId.MOST_USED_LICENSES.getId();
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Occurrences", "String", "License"};
    }

    @Override
    public ReportExecution execute(RepositoryHandler repoHandler, ReportRequest request) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Executing %s", getName()));
        }

        final Map<String, String> params = request.getParamValues();
        final String orgName = params.get("organization");


        final ReportExecution result = new ReportExecution(request, getColumnNames());

        DataFetchingUtils data = new DataFetchingUtils();
        data.initCorporateIDs(repoHandler, orgName);

        final Map<String, Integer> counters = new HashMap<>();

        repoHandler.consumeByQuery(DbCollections.DB_ARTIFACTS,
                "",
                DbArtifact.class,
                artifact -> {
                    if (data.isThirdParty(artifact) && !artifact.getLicenses().isEmpty()) {
                        appendToCounters(artifact.getLicenses(), counters);
                    }
                });

        final AtomicInteger categories = new AtomicInteger(0);
        final AtomicInteger items = new AtomicInteger(0);

        counters.entrySet()
                .forEach(entry -> {
                    final Set<DbLicense> matchingLicenses = repoHandler.getMatchingLicenses(entry.getKey());

                    if (!matchingLicenses.isEmpty()) {
                        final DbLicense lic = matchingLicenses.iterator().next();
                        result.addResultRow(new String[]{entry.getValue().toString(), entry.getKey(), lic.getName()});
                    } else {
                        result.addResultRow(new String[] { entry.getValue().toString(), entry.getKey(), "?"});

                        //
                        //  Increase the counters
                        //
                        categories.set(categories.get() + 1);
                        items.set(items.get() + entry.getValue());
                    }
                });

        result.setSorting(0, (a, b) -> {
            try {
                int aVal = Integer.parseInt(a);
                int bVal = Integer.parseInt(b);
                return bVal - aVal;
            } catch(NumberFormatException e) {
                return 1;
            }
        });

        return result;
    }

    private void appendToCounters(List<String> licenses, Map<String, Integer> counters) {
        licenses.forEach(lic -> {
            if (counters.containsKey(lic)) {
                final Integer count = counters.get(lic);
                counters.put(lic, count + 1);
            } else {
                counters.put(lic, 1);
            }
        });
    }
}