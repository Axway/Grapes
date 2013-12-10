package org.axway.grapes.server.webapp.views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yammer.dropwizard.views.View;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Promotion Report View
 *
 * <p>This view handle the promotion report. It contains the dependencies that are not promoted.</p>
 *
 */
public class PromotionReportView extends View {

    private Module rootModule;
    private List<Module> unPromotedDependencies = new ArrayList<Module>();
    private Map<String, PromotionReportView> dependencyReports = new HashMap<String, PromotionReportView>();
    private List<Artifact> doNotUseArtifacts = new ArrayList<Artifact>();

    public PromotionReportView() {
        super("PromotionReportView.ftl");
    }

    @JsonIgnore
    public void addUnPromotedDependency(final Module dependency) {
        unPromotedDependencies.add(dependency);
    }

    public List<Module> getUnPromotedDependencies() {
        return unPromotedDependencies;
    }

    public void setUnPromotedDependencies(final List<Module> unPromotedDependencies) {
        this.unPromotedDependencies = unPromotedDependencies;
    }

    @JsonIgnore
    public void addDependencyReport(final String moduleId, final PromotionReportView report) {
        if(moduleId != null && report != null){
            dependencyReports.put(moduleId, report);

            // Add children reports
            dependencyReports.putAll(report.dependencyReports);
        }
    }

    @JsonIgnore
    public PromotionReportView getTargetedDependencyReport(final String moduleId) {
        return dependencyReports.get(moduleId);
    }


    public Map<String, PromotionReportView> getDependencyReports() {
        return dependencyReports;
    }


    @JsonIgnore
    public List<PromotionReportView> getReportsWithDoNotUseArtifacts() {
        final List<PromotionReportView> reports = new ArrayList<PromotionReportView>();

        if(!getDoNotUseArtifacts().isEmpty()){
            reports.add(this);
        }

        for(PromotionReportView report: dependencyReports.values()){
            if(!report.getDoNotUseArtifacts().isEmpty()){
                reports.add(report);
            }
        }

        return reports;
    }

    public void setDependencyReports(final Map<String, PromotionReportView> dependencyReport) {
        this.dependencyReports = dependencyReport;
    }

    @JsonIgnore
    public String getModuleUid(final Module module) {
        return DbModule.generateUID(module.getName(), module.getVersion());
    }

    public Module getRootModule() {
        return rootModule;
    }

    public void setRootModule(final Module rootModule) {
        this.rootModule = rootModule;
    }

    public List<Artifact> getDoNotUseArtifacts() {
        return doNotUseArtifacts;
    }

    public void setDoNotUseArtifacts(final List<Artifact> doNotUseArtifacts) {
        this.doNotUseArtifacts = doNotUseArtifacts;
    }

    public void addDoNotUseArtifact(final Artifact doNotUseArtifact) {
        doNotUseArtifacts.add(doNotUseArtifact);
    }

    @JsonIgnore
    public Boolean canBePromoted() {
        return unPromotedDependencies.isEmpty() && doNotUseArtifacts.isEmpty();
    }


    // WorkAround for Freemarker
    @JsonIgnore
    public PromotionReportView getThis(){
        return this;
    }
}
