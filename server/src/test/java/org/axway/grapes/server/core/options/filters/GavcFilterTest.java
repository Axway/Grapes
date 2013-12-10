package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GavcFilterTest {

    @Test
    public void approveNull(){
        GavcFilter filter = new GavcFilter("test");
        assertFalse(filter.filter(null));

        filter = new GavcFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setGroupId("test.test.test");
        artifact.setArtifactId("test");
        artifact.setVersion("1.0.0-8");


        GavcFilter filter = new GavcFilter(artifact.getGavc());
        assertTrue(filter.filter(artifact));

        filter = new GavcFilter("test");
        assertFalse(filter.filter(artifact));
    }

}
