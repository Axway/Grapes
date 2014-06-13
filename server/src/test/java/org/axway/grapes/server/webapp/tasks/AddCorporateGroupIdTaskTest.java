package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.*;

public class AddCorporateGroupIdTaskTest {

    @Test
    public void simpleCase() {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        final List<String> groupIds = new ArrayList<String>();
        groupIds.add("groupId");
        when(repoHandler.getCorporateGroupIds()).thenReturn(groupIds);
        final GrapesServerConfig config = mock(GrapesServerConfig.class);
        final AddCorporateGroupIdTask task = new AddCorporateGroupIdTask(repoHandler, config);

        Exception exception = null;
        ImmutableMultimap.Builder<String, String> builder = new ImmutableMultimap.Builder<String, String>();
        builder.put(ServerAPI.GROUPID_PARAM, "groupId2");
        try {
            task.execute(builder.build(), mock(PrintWriter.class));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(repoHandler, times(1)).addNewCorporateGroupId(captor.capture());
        assertEquals("groupId2", captor.getValue());

        verify(config,times(1)).loadGroupIds(repoHandler);
    }

}
