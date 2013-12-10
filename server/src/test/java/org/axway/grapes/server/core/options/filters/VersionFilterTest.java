package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VersionFilterTest {

    @Test
    public void approveNull(){
        VersionFilter filter = new VersionFilter("1.0.0");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setVersion("1.0.0");


        VersionFilter filter = new VersionFilter(artifact.getVersion());
        assertTrue(filter.filter(artifact));

        filter = new VersionFilter("test");
        assertFalse(filter.filter(artifact));
    }

    @Test
    public void approveModule(){
        final DbModule module = new DbModule();
        module.setVersion("1.0.0");


        VersionFilter filter = new VersionFilter(module.getVersion());
        assertTrue(filter.filter(module));

        filter = new VersionFilter("test");
        assertFalse(filter.filter(module));
    }

}
