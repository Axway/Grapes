package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.junit.Test;

import java.io.PrintWriter;

import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.*;

public class RemoveRoleTaskTest {

    @Test
    public void testRemoveRole(){
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        final GrapesServerConfig config = GrapesTestUtils.getConfigMock();
        final RemoveRoleTask task = new RemoveRoleTask(repositoryHandler,config);

        final ImmutableMultimap.Builder<String, String> builder = new ImmutableMultimap.Builder<String, String>();
        builder.put(ServerAPI.USER_PARAM, "user");
        builder.put(ServerAPI.USER_ROLE_PARAM, "data_updater");
        Exception exception = null;

        try {
            task.execute(builder.build(), mock(PrintWriter.class));
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
        verify(repositoryHandler, times(1)).removeUserRole("user", AvailableRoles.DATA_UPDATER);
        verify(config, times(1)).loadCredentials(repositoryHandler);

    }

}

