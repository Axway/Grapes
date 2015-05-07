package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;
import org.axway.grapes.model.utils.DBRegExp;
import org.axway.grapes.model.utils.DataUtils;

import java.util.*;

public class CorporateFilter implements Filter {

    private Organization organization;

    public CorporateFilter(final Organization organization) {
        this.organization = organization;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof Module){
            return matches((Module) datamodelObj);
        }
        if(datamodelObj instanceof Artifact){
            return matches((Artifact)datamodelObj);
        }
        if(datamodelObj instanceof Dependency){
            return matches((Dependency)datamodelObj);
        }

        return false;
    }

    public boolean matches(final Module module) {
        final Set<Artifact> artifacts = DataUtils.getAllArtifacts(module);

        if(artifacts.isEmpty()){
            return evaluate(module.getId());


        }
//todo look at originial
        return evaluate(artifacts.iterator().next().getArtifactId());
    }

    public boolean matches(final Artifact artifact) {
        return evaluate(artifact.getGavc());
    }

    public boolean matches(final Dependency dependency) {
        return evaluate(dependency.getTarget().getArtifactId());
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("orginization", organization.getName());

        return queryParams;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        final DBRegExp regExp = getMongoRegExp();

        if(regExp != null){
            queryParams.put("groupId", regExp);
        }

        return queryParams;
    }

    private boolean evaluate(final String id){
        for(String corporateGroupId: organization.getCorporateGroupIdPrefixes()){
            if(id.startsWith(corporateGroupId)){
                return true;
            }
        }
        return false;
    }

    public DBRegExp getMongoRegExp() {
        final List<String> corporateGroupIds = organization.getCorporateGroupIdPrefixes();

        if(corporateGroupIds.isEmpty()){
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        final Iterator<String> groupIdsIterator = corporateGroupIds.iterator();

        while(groupIdsIterator.hasNext()){
            String corporateGroupId = groupIdsIterator.next();

            // If the groupId ends with '.' this loop removes it to avoid to break the regexp
            if(corporateGroupId.endsWith(".")){
                corporateGroupId = corporateGroupId.substring(0 , corporateGroupId.length()-1);
            }

            sb.append(corporateGroupId);
            sb.append("*");

            if(groupIdsIterator.hasNext()){
                sb.append("|");
            }
        }

        return new DBRegExp(sb.toString());
    }
}
