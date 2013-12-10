package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoNotUseFilterTest {

    @Test
    public void approveNull(){
        DoNotUseFilter filter = new DoNotUseFilter(true);
        assertFalse(filter.filter(null));

        filter = new DoNotUseFilter(false);
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact doNotUseArtifact = new DbArtifact();
        doNotUseArtifact.setDoNotUse(true);
        final DbArtifact artifactThatCanBeUsed = new DbArtifact();
        artifactThatCanBeUsed.setDoNotUse(false);


        DoNotUseFilter filter = new DoNotUseFilter(true);
        assertTrue(filter.filter(doNotUseArtifact));
        assertFalse(filter.filter(artifactThatCanBeUsed));

        filter = new DoNotUseFilter(false);
        assertFalse(filter.filter(doNotUseArtifact));
        assertTrue(filter.filter(artifactThatCanBeUsed));
    }

}
