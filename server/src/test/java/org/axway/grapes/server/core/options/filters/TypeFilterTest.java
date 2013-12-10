package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeFilterTest {

    @Test
    public void approveNull(){
        TypeFilter filter = new TypeFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setType("typeTest");


        TypeFilter filter = new TypeFilter(artifact.getType());
        assertTrue(filter.filter(artifact));

        filter = new TypeFilter("test");
        assertFalse(filter.filter(artifact));
    }

}
