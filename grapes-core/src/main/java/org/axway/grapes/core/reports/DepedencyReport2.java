package org.axway.grapes.core.reports;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.handler.DataUtils;
import org.axway.grapes.core.service.VersionsService;
import org.axway.grapes.core.webapi.resources.DependencyComplete;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.wisdom.api.annotations.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dependency Report
 *
 * <p>This class has been design to provide a quick feedback about module dependencies.</p>
 *
 * @author jdcoffre
 */

public class DepedencyReport2 {



    private List<DependencyComplete> dependencies = new ArrayList<DependencyComplete>();
    private List<String> shouldNotBeUsed = new ArrayList<String>();


    private Map<String, String> lastVersion = new HashMap<String,String>();
    public List<DependencyComplete> getDependencies() {
        return dependencies;
    }



    public DepedencyReport2(){

       }

    public DependencyComplete createComplete(String source, String scope, String lastVersion,Artifact dependency){
        DependencyComplete dependencyComplete = new DependencyComplete();
        dependencyComplete.groupId = dependency.getGroupId();
        dependencyComplete.artifactId = dependency.getArtifactId();
        dependencyComplete.currentVersion = dependency.getVersion();
        System.out.println("i am here atleast?");
        dependencyComplete.mostRecentVersion = lastVersion;
        dependencyComplete.doNotUse = dependency.getDoNotUse();
        dependencyComplete.Scope = scope;
        dependencyComplete.Source = source;
        return dependencyComplete;

    }


    public void addDependency(final Dependency dependency, String lastVersion, final Artifact target) {
       dependencies.add(createComplete(dependency.getSource(), dependency.getScope().toString(), lastVersion, target));
    }



    public String getLastVersion(final Artifact artifact){
        System.out.println("and here?");
//       final String version= versionsService.getLastVersion(artifact,true);
//
//        if(version == null){
//            return "not available";
//        }
//
//        return version;
        return null;
    }





    public class DependencyComplete{
        public String groupId;
        public String artifactId;
        public String currentVersion;
        public String mostRecentVersion;
        public Boolean doNotUse;
        public String Source;
        public String Scope;
        public  DependencyComplete(){

        }
    }
}
