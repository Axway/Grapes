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

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class RemoveCorporateGroupIdTaskTest {

    @Test
    public void missingGroupId() {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        when(repoHandler.getCorporateGroupIds()).thenReturn(new ArrayList<String>());
        final GrapesServerConfig config = mock(GrapesServerConfig.class);

        final RemoveCorporateGroupIdTask task = new RemoveCorporateGroupIdTask(repoHandler, config);

        Exception exception = null;
        ImmutableMultimap.Builder<String, String> builder = new ImmutableMultimap.Builder<String, String>();
        try {
            task.execute(builder.build(), mock(PrintWriter.class));
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void happyPass() {
        final RepositoryHandler repoHandler = mock(RepositoryHandler.class);
        final List<String> groupIds = new ArrayList<String>();
        groupIds.add("groupId");
        when(repoHandler.getCorporateGroupIds()).thenReturn(groupIds);
        final GrapesServerConfig config = mock(GrapesServerConfig.class);

        final RemoveCorporateGroupIdTask task = new RemoveCorporateGroupIdTask(repoHandler, config);

        Exception exception = null;
        ImmutableMultimap.Builder<String, String> builder = new ImmutableMultimap.Builder<String, String>();
        builder.put(ServerAPI.GROUPID_PARAM, "groupId");
        try {
            task.execute(builder.build(), mock(PrintWriter.class));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        verify(repoHandler, times(1)).getCorporateGroupIds();

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(repoHandler, times(1)).removeCorporateGroupId(captor.capture());
        assertEquals("groupId", captor.getValue());
    }

}
