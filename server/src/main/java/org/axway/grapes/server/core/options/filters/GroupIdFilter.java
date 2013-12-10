package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

import java.util.HashMap;
import java.util.Map;

public class GroupIdFilter implements Filter {

    private String groupId;

    /**
     * The parameter must never be null
     *
     * @param groupId
     */
    public GroupIdFilter(final String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof DbArtifact){
            return groupId.equals( ((DbArtifact)datamodelObj).getGroupId());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(DbArtifact.GROUPID_DB_FIELD, groupId);
        return queryParams;
    }
}
