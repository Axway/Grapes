package org.axway.grapes.server.db.datamodel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DbSearchTest {

    @Test
    public void checkToString() {

        List<String> moduleIds  = new ArrayList<>();
        moduleIds.add("test_id_1");
        moduleIds.add("test_id_2");
        List<String> artifactIds  = new ArrayList<>();
        artifactIds.add("test_artifact_id_1");
        artifactIds.add("test_artifact_id_2");

        DbSearch search = new DbSearch();
        search.setArtifacts(artifactIds);
        search.setModules(moduleIds);
        assertEquals("{ modules: [test_id_1, test_id_2], artifacts: [test_artifact_id_1, test_artifact_id_2]}", search.toString());
    }

}