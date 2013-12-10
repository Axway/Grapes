package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArtifactIdFilterTest {

    @Test
    public void approveNull(){
        final ArtifactIdFilter filter = new ArtifactIdFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setArtifactId("artifactIdTest");


        ArtifactIdFilter filter = new ArtifactIdFilter(artifact.getArtifactId());
        assertTrue(filter.filter(artifact));

        filter = new ArtifactIdFilter("test");
        assertFalse(filter.filter(artifact));
    }

}
