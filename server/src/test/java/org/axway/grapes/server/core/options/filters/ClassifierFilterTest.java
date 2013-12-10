package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClassifierFilterTest {

    @Test
    public void approveNull(){
        ClassifierFilter filter = new ClassifierFilter("test");
        assertFalse(filter.filter(null));

        filter = new ClassifierFilter("test");
        assertFalse(filter.filter(null));
    }

    @Test
    public void approveArtifact(){
        final DbArtifact artifact = new DbArtifact();
        artifact.setClassifier("classifierTest");


        ClassifierFilter filter = new ClassifierFilter(artifact.getClassifier());
        assertTrue(filter.filter(artifact));

        filter = new ClassifierFilter("test");
        assertFalse(filter.filter(artifact));
    }

}
