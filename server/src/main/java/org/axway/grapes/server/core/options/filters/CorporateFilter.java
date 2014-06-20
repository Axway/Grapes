package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.DBRegExp;
import org.axway.grapes.server.db.DataUtils;
import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbDependency;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.axway.grapes.server.db.datamodel.DbOrganization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CorporateFilter implements Filter {

    private DbOrganization organization;

    public CorporateFilter(final DbOrganization organization) {
        this.organization = organization;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbModule){
            return matches((DbModule) datamodelObj);
        }
        if(datamodelObj instanceof DbArtifact){
            return matches((DbArtifact)datamodelObj);
        }
        if(datamodelObj instanceof DbDependency){
            return matches((DbDependency)datamodelObj);
        }

        return false;
    }

    public boolean matches(final DbModule module) {
        final List<String> artifacts = DataUtils.getAllArtifacts(module);

        if(artifacts.isEmpty()){
            return evaluate(module.getId());
        }

        return evaluate(artifacts.get(0));
    }

    public boolean matches(final DbArtifact artifact) {
        return evaluate(artifact.getGavc());
    }

    public boolean matches(final DbDependency dependency) {
        return evaluate(dependency.getTarget());
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(DbModule.ORGANIZATION_DB_FIELD, organization.getName());

        return queryParams;
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        final DBRegExp regExp = getMongoRegExp();

        if(regExp != null){
            queryParams.put(DbArtifact.GROUPID_DB_FIELD, regExp);
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
