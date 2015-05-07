package org.axway.grapes.core.reports;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.utils.DataUtils;


import java.util.*;

/**
 * Dependency Report
 *
 * <p>This class has been design to provide a quick feedback about module dependencies.</p>
 *
 * @author jdcoffre
 */
@JsonSerialize(using=DependencyReportSerializer.class)
public class DependencyReport  {

  /*  private String title;
    private List<Dependency> dependencies = new ArrayList<Dependency>();
    private List<String> shouldNotBeUsed = new ArrayList<String>();

    public List<Dependency> getDependencies() {
        return dependencies;
    }
    private Map<String, String> lastVersion = new HashMap<String,String>();


    public DependencyReport(final String title) {
        super("DependencyReport.ftl");
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void addDependency(final Dependency dependency, final String lastRelease) {
        final String depId = Artifact.generateGAVC(dependency.getTarget());

        if(!dependencies.contains(dependency)){
            dependencies.add(dependency);
        }

        if(!lastVersion.containsKey(depId)){
            lastVersion.put(depId, lastRelease);
        }

    }

    public List<Artifact> getDependencyTargets(){
        final List<Artifact> targets = new ArrayList<Artifact>();
        final List<String> gavcs = new ArrayList<String>();

        for(Dependency dependency: dependencies){
            final String depGavc = DbArtifact.generateGAVC(dependency.getTarget());
            if(!gavcs.contains(depGavc)){
                targets.add(dependency.getTarget());
                gavcs.add(depGavc);
            }
        }

        DataUtils.sort(targets);

        return targets;
    }

    public List<String> getVersions(final Artifact target){
        final List<String> versions = new ArrayList<String>();

        for(Dependency dependency: dependencies){
            final String depGavc = DbArtifact.generateGAVC(dependency.getTarget());
            if(depGavc.equals(DbArtifact.generateGAVC(target))
                    && !versions.contains(dependency.getTarget().getVersion())){
                versions.add(dependency.getTarget().getVersion());
            }
        }

        return versions;
    }

    public String getLastVersion(final Artifact artifact){
        final String version = lastVersion.get(artifact.getGavc());

        if(version == null){
            return "not available";
        }

        return version;
    }


    public int getNbEntry(final Artifact artifact){
        int nbEntries = 0;

        final List<String> versions = getVersions(artifact);

        for(String version: versions){
			nbEntries += getDependencies(artifact, version).size();
        }


        return nbEntries;
    }

    public List<Dependency> getDependencies(final Artifact target, final String version) {
        final List<Dependency> sources = new ArrayList<Dependency>();

        for(Dependency dependency: dependencies){
            if(target.getGavc().equals(dependency.getTarget().getGavc()) &&
                    version.equals(dependency.getTarget().getVersion())){
                sources.add(dependency);
            }
        }

        int n = sources.size();
        while(n != 0){
            int newn = 0;

            for(int i = 1 ; i <= n-1 ; i++){
                if (sources.get(i-1).getSourceName().compareTo(sources.get(i).getSourceName()) > 0){
                    Collections.swap(sources,i-1,i);
                    newn = i;
                }
            }

            n = newn;
        }

        return sources;
    }

    public void addShouldNotUse(final String gavc){
        shouldNotBeUsed.add(gavc);
    }

    /**
     * Return 1 if the targeted artifact has the flag "DO_NOT_USE" otherwise 0
     *
     * @param gavc
     * @return int
     */
   /* public int shouldNotBeUsed(final String gavc){
        if(shouldNotBeUsed.contains(gavc)){
            return 1;
        }

        return 0;
    }
*/
}
