package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbSearch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SearchFilterTest {
    @Test
    public void filter() throws Exception {
        SearchFilter filter = new SearchFilter(true, true);
        assertFalse(filter.filter(null));

        filter = new SearchFilter(true, false);
        assertFalse(filter.filter(null));

        filter = new SearchFilter(false, true);
        assertFalse(filter.filter(null));

        filter = new SearchFilter(null, null);
        assertFalse(filter.filter(null));
    }

    @Test
    public void testModuleSearch() {
        List<String> moduleIds  = new ArrayList<>();
        moduleIds.add("test_id_1");
        moduleIds.add("test_id_2");
        List<String> artifactIds  = new ArrayList<>();
        artifactIds.add("test_artifact_id_1");
        artifactIds.add("test_artifact_id_2");

        DbSearch filterModules = new DbSearch();
        filterModules.setModules(moduleIds);

        DbSearch filterArtifacts = new DbSearch();
        filterArtifacts.setArtifacts(artifactIds);

        SearchFilter filter = new SearchFilter(false, true);

        assertTrue(filter.filter(filterModules));
        assertFalse(filter.filter(filterArtifacts));

        filter = new SearchFilter(true, false);

        assertTrue(filter.filter(filterArtifacts));
        assertFalse(filter.filter(filterModules));

        filter = new SearchFilter(false, false);
        assertFalse(filter.filter(filterArtifacts));
        assertFalse(filter.filter(filterModules));
    }
}