package org.axway.grapes.server.config;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.db.RepositoryHandler;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class GrapesServerConfigTest {

    @Test
    public void corporateGroupIdsLoadingCheck(){
        final GrapesServerConfig config = new GrapesServerConfig();
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        when(repositoryHandler.getCorporateGroupIds()).thenReturn(GrapesTestUtils.getTestCorporateGroupIds());

        config.loadGroupIds(repositoryHandler);
        verify(repositoryHandler, times(1)).getCorporateGroupIds();

        final List<String> corporateGroupIds = config.getCorporateGroupIds();
        assertNotNull(corporateGroupIds);
        assertEquals(1, corporateGroupIds.size());
        assertEquals(GrapesTestUtils.CORPORATE_GROUPID_4TEST, corporateGroupIds.get(0));


    }
}
