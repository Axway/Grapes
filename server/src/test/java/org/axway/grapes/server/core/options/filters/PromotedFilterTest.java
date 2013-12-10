package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;
import org.axway.grapes.server.db.datamodel.DbModule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PromotedFilterTest {

    @Test
    public void approveNull(){
        PromotedFilter filter = new PromotedFilter(true);
        assertFalse(filter.filter(null));

        filter = new PromotedFilter(false);
        assertFalse(filter.filter(null));
    }

    @Test
    public void promotedArtifact(){
        final DbArtifact promotedArtifact = new DbArtifact();
        promotedArtifact.setPromoted(true);
        final DbArtifact notPromotedArtifact = new DbArtifact();
        notPromotedArtifact.setPromoted(false);

        PromotedFilter filter = new PromotedFilter(true);
        assertTrue(filter.filter(promotedArtifact));
        assertFalse(filter.filter(notPromotedArtifact));
        filter = new PromotedFilter(false);
        assertFalse(filter.filter(promotedArtifact));
        assertTrue(filter.filter(notPromotedArtifact));
    }

    @Test
    public void promotedModule(){
        final DbModule promotedModule = new DbModule();
        promotedModule.setPromoted(true);
        final DbModule notPromotedModule = new DbModule();
        notPromotedModule.setPromoted(false);

        PromotedFilter filter = new PromotedFilter(true);
        assertTrue(filter.filter(promotedModule));
        assertFalse(filter.filter(notPromotedModule));
        filter = new PromotedFilter(false);
        assertFalse(filter.filter(promotedModule));
        assertTrue(filter.filter(notPromotedModule));
    }

}
