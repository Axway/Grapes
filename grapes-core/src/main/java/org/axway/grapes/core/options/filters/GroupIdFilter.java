package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;

import java.util.Collections;
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
        if(datamodelObj instanceof Artifact){
            return groupId.equals( ((Artifact)datamodelObj).getGroupId());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("groupId", groupId);
        return queryParams;
    }
}
