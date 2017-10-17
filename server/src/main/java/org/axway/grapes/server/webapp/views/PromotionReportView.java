package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Comment;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.PromotionEvaluationReport;
import org.axway.grapes.server.webapp.resources.PromotionReportTranslator;
import org.axway.grapes.server.webapp.views.serialization.PromotionReportSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Promotion Report View
 * <p>
 * <p>This view handle the promotion report. It contains the dependencies that are not promoted.</p>
 */

@JsonSerialize(using = PromotionReportSerializer.class)
public class PromotionReportView extends View {

    private Module rootModule;
    private List<String> unPromotedDependencies = new ArrayList<>();
    private Map<String, PromotionReportView> dependencyReports = new HashMap<>();
    private Map<Artifact, Comment> doNotUseArtifacts = new HashMap<>();
    private Map<String, List<String>> mismatchVersions = new HashMap<>();
    private List<Artifact> missingLicenses = new ArrayList<>();
    private Map<String, String> dependenciesWithNotAcceptedLicenses = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(PromotionReportView.class);


    public PromotionReportView() {
        super("PromotionReportView.ftl");
    }

    public Module getRootModule() {
        return rootModule;
    }

    public void setRootModule(final Module rootModule) {
        this.rootModule = rootModule;
    }

    public void addUnPromotedDependency(final String dependencyId) {
        if (!unPromotedDependencies.contains(dependencyId)) {
            unPromotedDependencies.add(dependencyId);
        }
    }

    public List<String> getUnPromotedDependencies() {
        return unPromotedDependencies;
    }

    public void addDependencyPromotionReport(final String moduleId, final PromotionReportView report) {
        if (moduleId != null && report != null) {
            dependencyReports.put(moduleId, report);

            // Add children reports
            dependencyReports.putAll(report.dependencyReports);
        }
    }

    public PromotionReportView getTargetedDependencyReport(final String moduleId) {
        return dependencyReports.get(moduleId);
    }

    public Map<Artifact, Comment> getDoNotUseArtifacts() {
        return doNotUseArtifacts;
    }

    public void addDoNotUseArtifact(final Artifact doNotUseArtifact, final Comment comment) {
        doNotUseArtifacts.put(doNotUseArtifact, comment);
    }

    public List<PromotionReportView> getReportsWithDoNotUseArtifacts() {
        final List<PromotionReportView> reports = new ArrayList<>();

        if (!getDoNotUseArtifacts().isEmpty()) {
            reports.add(this);
        }

        for (final PromotionReportView report : dependencyReports.values()) {
            if (!report.getDoNotUseArtifacts().isEmpty()) {
                reports.add(report);
            }
        }

        return reports;
    }

    public boolean isSnapshot() {
        return getRootModule().getVersion().contains("SNAPSHOT");
    }

    public Set<String> getMisMatchModules() {
        return mismatchVersions.keySet();
    }

    public List<String> getMisMatchVersions(final String moduleName) {
        return mismatchVersions.get(moduleName);
    }

    public void compute() {
        /* Order the module to promote */
        final Comparator<String> promotionPlanComparator = new PromotionPlanComparator(dependencyReports);
        Collections.sort(unPromotedDependencies, promotionPlanComparator);

        /* Identify the mismatch versions */
        // Collect all the modules names and versions
        for (final PromotionReportView promotionReport : getAllDependencyReport()) {
            final Module module = promotionReport.getRootModule();

            if(null == module) {
                LOG.warn("No root module found. Aborting compute().");
                return;
            }

            final List<String> versions = mismatchVersions.get(module.getName());

            if (versions == null) {
                mismatchVersions.put(module.getName(), Lists.newArrayList(module.getVersion()));
            } else if (!versions.contains(module.getVersion())) {
                versions.add(module.getVersion());
            }
        }

        // Remove the modules that appears in only one version
        final Iterator<String> moduleNames = mismatchVersions.keySet().iterator();
        while (moduleNames.hasNext()) {
            final String moduleName = moduleNames.next();
            final List<String> versions = mismatchVersions.get(moduleName);
            if (versions.size() == 1) {
                moduleNames.remove();
            }
        }
    }

    private List<PromotionReportView> getAllDependencyReport() {
        final List<PromotionReportView> reports = new ArrayList<>();
        for (final PromotionReportView report : dependencyReports.values()) {
            reports.addAll(report.getAllDependencyReport());
        }
        reports.add(this);

        return reports;
    }

    public List<String> getPromotionPlan() {
        return unPromotedDependencies;
    }

    public List<Artifact> getMissingLicenses() {
        return missingLicenses;
    }

    public void addMissingThirdPartyDependencyLicenses(final Artifact dependency) {
        if (!missingLicenses.contains(dependency)) {
            missingLicenses.add(dependency);
        }
    }

    public Map<String, String> getDependenciesWithNotAcceptedLicenses() {
        return Collections.unmodifiableMap(dependenciesWithNotAcceptedLicenses);
    }

    public void addUnacceptedLicenseEntry(final String artifact, final String license) {
        if(!dependenciesWithNotAcceptedLicenses.containsKey(artifact)) {
            dependenciesWithNotAcceptedLicenses.put(artifact, license);
        }
    }

    private class PromotionPlanComparator implements Comparator<String> {
        private final Map<String, PromotionReportView> dependencyReports;

        public PromotionPlanComparator(final Map<String, PromotionReportView> dependencyReports) {
            this.dependencyReports = dependencyReports;
        }

        @Override
        public int compare(final String module1, final String module2) {
            final PromotionReportView report1 = dependencyReports.get(module1);
            final PromotionEvaluationReport ev1 = PromotionReportTranslator.toReport(report1);

            final PromotionReportView report2 = dependencyReports.get(module2);
            final PromotionEvaluationReport ev2 = PromotionReportTranslator.toReport(report2);

            if (ev1.isPromotable() ||
                    report2.getUnPromotedDependencies().contains(module1)) {
                return -1;
            } else if (ev2.isPromotable() ||
                    report1.getUnPromotedDependencies().contains(module2)) {
                return 1;
            }

            return 0;
        }
    }

    public String getArtifactLink(final String gavc) {
        final String[] parts = gavc.split(":");
        if(parts.length > 2) {
            return String.format("/webapp?section-artifacts&groupId=%s&artifactId=%s&version=%s",
                    parts[0], parts[1], parts[2] );
        } else {
            return "/webapp?section=artifacts";
        }
    }
}
