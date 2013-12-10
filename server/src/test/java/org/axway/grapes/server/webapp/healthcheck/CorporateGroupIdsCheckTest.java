package org.axway.grapes.server.webapp.healthcheck;

import com.yammer.metrics.core.HealthCheck;
import org.axway.grapes.server.db.RepositoryHandler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CorporateGroupIdsCheckTest {

    @Test
    public void checkOk() {
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);

        final List<String> corporateGroupIds = new ArrayList<String>();
        corporateGroupIds.add("GroupId1");
        corporateGroupIds.add("GroupId2");
        when(repositoryHandler.getCorporateGroupIds()).thenReturn(corporateGroupIds);

        final CorporateGroupIdsCheck checker = new CorporateGroupIdsCheck(repositoryHandler);
        HealthCheck.Result result = null;
        Exception exception = null;

        try{
            result = checker.check();
        }
        catch (Exception e){
            exception = e;
        }

        assertNull(exception);
        assertNotNull(result);
        assertTrue(result.isHealthy());
        assertEquals("List of groupIds configured has corporate groupId: GroupId1, GroupId2" , result.getMessage());
    }
}
