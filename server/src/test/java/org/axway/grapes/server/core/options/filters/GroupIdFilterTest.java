package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupIdFilterTest {

    @Test
    public void approveNull(){
        GroupIdFilter filter = new GroupIdFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("groupIdTest");


        GroupIdFilter filter = new GroupIdFilter(artifact.getGroupId());
        assertTrue(filter.filter(artifact));

        filter = new GroupIdFilter("test");
        assertFalse(filter.filter(artifact));
    }

}
