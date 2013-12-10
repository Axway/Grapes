package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExtensionFilterTest {

    @Test
    public void approveNull(){
        ExtensionFilter filter = new ExtensionFilter("test");
        assertFalse(filter.filter(null));

        filter = new ExtensionFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setExtension("extensionTest");


        ExtensionFilter filter = new ExtensionFilter(artifact.getExtension());
        assertTrue(filter.filter(artifact));

        filter = new ExtensionFilter("test");
        assertFalse(filter.filter(artifact));
    }

}
