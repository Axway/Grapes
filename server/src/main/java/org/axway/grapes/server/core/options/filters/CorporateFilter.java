package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.DBRegExp;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;

import java.util.*;

public class CorporateFilter implements Filter {

    private Boolean isCorporate;
    private final List<String> corporateGroupIds;

    public CorporateFilter(final List<String> corporateGroupIds) {
        this.corporateGroupIds = corporateGroupIds;
    }

    public void setIsCorporate(final Boolean corporate) {
        isCorporate = corporate;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(isCorporate == null){
            return true;
        }
        else{
            boolean targetIsCorporate = false;
            if(datamodelObj instanceof DbModule){
                targetIsCorporate = true;
            }
            if(datamodelObj instanceof DbArtifact){
                targetIsCorporate = matches((DbArtifact)datamodelObj);
            }
            if(datamodelObj instanceof DbDependency){
                targetIsCorporate = matches((DbDependency)datamodelObj);
            }

            return isCorporate.equals(targetIsCorporate);
        }
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        final DBRegExp regExp = getRegExp();

        if(regExp != null){
            queryParams.put(DbArtifact.GROUPID_DB_FIELD, regExp);
        }

        return queryParams;
    }

    public boolean matches(final DbArtifact artifact) {
        return evaluate(artifact.getGroupId());
    }

    public boolean matches(final DbDependency dependency) {
        final String evaluatedGroupId = DataUtils.getGroupId(dependency.getTarget());
        return evaluate(evaluatedGroupId);
    }

    private boolean evaluate(final String groupId){
        for(String corporateGroupId: corporateGroupIds){
            if(groupId.startsWith(corporateGroupId)){
                return true;
            }
        }
        return false;
    }

    public DBRegExp getRegExp() {
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
