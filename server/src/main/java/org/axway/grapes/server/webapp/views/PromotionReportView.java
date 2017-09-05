package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Comment;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.Pair;
import org.axway.grapes.server.webapp.views.serialization.PromotionReportSerializer;

import java.util.*;

/**
 * Promotion Report View
 * <p>
 * <p>This view handle the promotion report. It contains the dependencies that are not promoted.</p>
 */

@JsonSerialize(using = PromotionReportSerializer.class)
public class PromotionReportView extends View {

    private Module rootModule;
    private List<String> unPromotedDependencies = new ArrayList<String>();
    private Map<String, PromotionReportView> dependencyReports = new HashMap<String, PromotionReportView>();
    private Map<Artifact, Comment> doNotUseArtifacts = new HashMap<>();
    private Map<String, List<String>> mismatchVersions = new HashMap<String, List<String>>();
    private List<Artifact> missingThirdPartyDependencyLicenses = new ArrayList<Artifact>();
    private List<Pair> dependenciesWithNotAcceptedLicenses = new ArrayList<Pair>();

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
        final List<PromotionReportView> reports = new ArrayList<PromotionReportView>();

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

    public Boolean canBePromoted() {
        if (isSnapshot()) {
            return false;
        }
        return unPromotedDependencies.isEmpty() &&
                doNotUseArtifacts.isEmpty() && missingThirdPartyDependencyLicenses.isEmpty() && dependenciesWithNotAcceptedLicenses.isEmpty();
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
        final List<PromotionReportView> reports = new ArrayList<PromotionReportView>();
        for (final PromotionReportView report : dependencyReports.values()) {
            reports.addAll(report.getAllDependencyReport());
        }
        reports.add(this);

        return reports;
    }

    public List<String> getPromotionPlan() {
        return unPromotedDependencies;
    }

    public List<Artifact> getMissingThirdPartyDependencyLicenses() {
        return missingThirdPartyDependencyLicenses;
    }

    public void addMissingThirdPartyDependencyLicenses(final Artifact dependency) {
        if (!missingThirdPartyDependencyLicenses.contains(dependency)) {
            missingThirdPartyDependencyLicenses.add(dependency);
        }
    }

    public List<Pair> getDependenciesWithNotAcceptedLicenses() {
        return dependenciesWithNotAcceptedLicenses;
    }

    public void setDependenciesWithNotAcceptedLicenses(Pair<?,?> pair) {
        if (!dependenciesWithNotAcceptedLicenses.contains(pair)) {
            dependenciesWithNotAcceptedLicenses.add(pair);
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
            final PromotionReportView report2 = dependencyReports.get(module2);

            if (report1.canBePromoted() ||
                    report2.getUnPromotedDependencies().contains(module1)) {
                return -1;
            } else if (report2.canBePromoted() ||
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
