package org.axway.grapes.core.reports;

import com.google.common.collect.Lists;
import org.axway.grapes.core.handler.ModuleHandler;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisdom.api.annotations.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jennifer on 8/12/15.
 */

public class PromotionReport {
    //todo this report is call recursivley how can we keep track of the recursive do not use artifacts?
    private static final Logger LOG = LoggerFactory.getLogger(PromotionReport.class);
    private Module rootModule;
    private List<String> unPromotedDependencies = new ArrayList<String>();
    private Map<String, PromotionReport> dependencyReports = new HashMap<String, PromotionReport>();
    private List<Artifact> doNotUseArtifacts = new ArrayList<Artifact>();
    private Map<String , List<String>> mismatchVersions = new HashMap<String, List<String>>();

    public PromotionReport() {

    }

    public Module getRootModule() {
        return rootModule;
    }

    public void setRootModule(final Module rootModule) {
        this.rootModule = rootModule;
    }

    public void addUnPromotedDependency(final String dependencyId) {
        if(!unPromotedDependencies.contains(dependencyId)){
            unPromotedDependencies.add(dependencyId);
        }
    }

    public List<String> getUnPromotedDependencies() {
        return unPromotedDependencies;
    }

    public void addDependencyPromotionReport(final String moduleId, final PromotionReport report) {
        if(moduleId != null && report != null){
            dependencyReports.put(moduleId, report);

            // Add children reports
            dependencyReports.putAll(report.dependencyReports);
        }
    }

    public PromotionReport getTargetedDependencyReport(final String moduleId) {
        return dependencyReports.get(moduleId);
    }

    public List<Artifact> getDoNotUseArtifacts() {
        return doNotUseArtifacts;
    }

    public void addDoNotUseArtifact(final Artifact doNotUseArtifact) {
        LOG.error("inside add to list "+doNotUseArtifact.getGavc());
        doNotUseArtifacts.add(doNotUseArtifact);
        LOG.error("do not use legnth is: "+doNotUseArtifacts.size());
    }

    public List<PromotionReport> getReportsWithDoNotUseArtifacts() {
        final List<PromotionReport> reports = new ArrayList<PromotionReport>();

        if(!getDoNotUseArtifacts().isEmpty()){
            reports.add(this);
        }

        for(PromotionReport report: dependencyReports.values()){
            if(!report.getDoNotUseArtifacts().isEmpty()){
                reports.add(report);
            }
        }

        return reports;
    }

    public Boolean canBePromoted() {
        if(isSnapshot()){
            return false;
        }
        return unPromotedDependencies.isEmpty() &&
                doNotUseArtifacts.isEmpty();
    }

    public boolean isSnapshot() {
        //only for semantic versioning.
        return getRootModule().getVersion().contains("SNAPSHOT");
    }

    public Set<String> getMisMatchModules(){
        return mismatchVersions.keySet();
    }

    public List<String> getMisMatchVersions(final String moduleName){
        return mismatchVersions.get(moduleName);
    }

    public void compute() {

        /* Order the module to promote */
        final Comparator<String> promotionPlanComparator = new PromotionPlanComparator(dependencyReports);
        Collections.sort(unPromotedDependencies, promotionPlanComparator);

        /* Identify the mismatch versions */
        // Collect all the modules names and versions
        for(PromotionReport promotionReport: getAllDependencyReport()){
            final Module module = promotionReport.getRootModule();
            List<String> versions = mismatchVersions.get(module.getName());

            if(versions == null){
                mismatchVersions.put(module.getName(), Lists.newArrayList(module.getVersion()));
            }
            else if(!versions.contains(module.getVersion())){
                versions.add(module.getVersion());
            }
        }

        // Remove the modules that appears in only one version
        final Iterator<String> moduleNames = mismatchVersions.keySet().iterator();
        while (moduleNames.hasNext()){
            final String moduleName = moduleNames.next();
            final List<String> versions = mismatchVersions.get(moduleName);
            if(versions.size() == 1 ){
                moduleNames.remove();
            }
        }
    }

    private List<PromotionReport> getAllDependencyReport() {
        final List<PromotionReport> reports = new ArrayList<PromotionReport>();
        for(PromotionReport report: dependencyReports.values()){
            reports.addAll(report.getAllDependencyReport());
        }
        reports.add(this);

        return reports;
    }

    public List<String> getPromotionPlan(){
        return unPromotedDependencies;
    }


    private class PromotionPlanComparator implements Comparator<String> {
        private final Map<String, PromotionReport> dependencyReports;

        public PromotionPlanComparator(final Map<String, PromotionReport> dependencyReports) {
            this.dependencyReports = dependencyReports;
        }


        @Override
        public int compare(final String module1, final String module2) {
            final PromotionReport report1 = dependencyReports.get(module1);
            final PromotionReport report2 = dependencyReports.get(module2);

            if(report1.canBePromoted() ||
                    report2.getUnPromotedDependencies().contains(module1)){
                return -1;
            }
            else if(report2.canBePromoted() ||
                    report1.getUnPromotedDependencies().contains(module2)){
                return 1;
            }

            return 0;
        }
    }

}
