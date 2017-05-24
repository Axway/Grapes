package org.axway.grapes.server.reports.impl;

import org.apache.commons.lang3.StringUtils;
import org.axway.grapes.commons.datamodel.Delivery;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbCollections;
import org.axway.grapes.server.db.mongo.BatchProcessingUtils;
import org.axway.grapes.server.reports.ReportId;
import org.axway.grapes.server.reports.utils.DataFetchingUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Report showing evolution of licenses between two commercial releases of a product
 */
public class DiffsLicenseReport implements Report {

    static List<ParameterDefinition> parameters = new ArrayList<>();
    private DataFetchingUtils utils = new DataFetchingUtils();
    private BatchProcessingUtils batchProcessingUtils = new BatchProcessingUtils();

    private static final String BATCH_TEMPLATE_REGEX = "{ \"_id\" : { \"$regex\" : \"%s\"}}";

    static {
        parameters.add(new ParameterDefinition("name1", "Commercial Release Name"));
        parameters.add(new ParameterDefinition("version1", "Commercial Version"));

        parameters.add(new ParameterDefinition("name2", "Commercial Release Name"));
        parameters.add(new ParameterDefinition("version2", "Commercial Version"));
    }

    @Override
    public String getName() {
        return "License Evolution Between Product Commercial Releases";
    }

    @Override
    public String getDescription() {
        return "Displays licenses that were added or removed from one commercial release to another";
    }

    @Override
    public ReportId getId() {
        return ReportId.DIFFS_PER_PRODUCT_RELEASE;
    }

    @Override
    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"License Name", "Status"};
    }

    @Override
    public ReportExecution execute(RepositoryHandler repoHandler, ReportRequest request) {

        final Map<String, String> params = request.getParamValues();
        final String name1 = params.get("name1");
        final String version1 = params.get("version1");

        final Optional<Delivery> cd1 = utils.getCommercialDelivery(repoHandler, name1, version1);
        check(cd1, name1, version1);

        final String name2 = params.get("name2");
        final String version2 = params.get("version2");

        final Optional<Delivery> cd2 = utils.getCommercialDelivery(repoHandler, name2, version2);
        check(cd2, name2, version2);

        // Deps are in the following form: group-id:artifact-id:version
        Set<String> deps1 = utils.getDeliveryDependencies(repoHandler, cd1.get());
        Set<String> deps2 = utils.getDeliveryDependencies(repoHandler, cd2.get());

        Set<String> all = new HashSet<>(deps1);
        all.addAll(deps2);

        HashMap<String, String> artifactsLicenses = new HashMap<>();

        batchProcessingUtils.processBatch(repoHandler,
                                        DbCollections.DB_ARTIFACTS,
                               1,
                                        batch -> String.format(BATCH_TEMPLATE_REGEX, StringUtils.join(batch, ',')),
                                        all,
                                        DbArtifact.class,
                                        a -> a.getLicenses().forEach(lic -> {
                                                artifactsLicenses.put(DataUtils.strip(a.getGavc(), 2), lic);
                                        })
        );

        ReportExecution result = new ReportExecution(request, getColumnNames());

        // A \ B
        all.removeAll(deps2);
        treatSet(result, artifactsLicenses, all, "Missing");

        // A U B
        all.addAll(deps2);

        // B \ A
        all.removeAll(deps1);
        treatSet(result, artifactsLicenses, all, "Added");

        return result;
    }

    private void treatSet(final ReportExecution result,
                          final Map<String, String> map,
                          final Set<String> entries,
                          final String label) {
        entries.forEach(entry -> {
            if (map.containsKey(entry)) {
                result.addResultRow(new String[]{map.get(entry), label});
            }
        });

    }

    private void check(final Optional<?> entry, final String name, final String version) {
        if(!entry.isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.NOT_FOUND)
                            .entity(String.format("Cannot find commercial delivery [%s %s]", name, version))
                            .build());
        }
    }
}
