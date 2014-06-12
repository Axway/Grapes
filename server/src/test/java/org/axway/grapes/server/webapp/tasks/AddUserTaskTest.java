package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.yammer.dropwizard.auth.AuthenticationException;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.materials.TestingRepositoryHandler;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;
import org.junit.Test;

import java.io.PrintWriter;
import java.net.UnknownHostException;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

public class AddUserTaskTest {
    @Test
	public void testAddUser() throws UnknownHostException, AuthenticationException {
		final TestingRepositoryHandler repoHandler = new TestingRepositoryHandler();
		
		final AddUserTask addUser = new AddUserTask(repoHandler);
		final ImmutableMultimap.Builder<String, String> builder = new Builder<String, String>();
		builder.put(ServerAPI.USER_PARAM, "user");
		builder.put(ServerAPI.PASSWORD_PARAM, "password");
		Exception exception = null;
		
		try {
			addUser.execute(builder.build(), mock(PrintWriter.class));
		} catch (Exception e) {
			exception = e;
		}
		
		assertNull(exception);
		
		final DbCredential credential = repoHandler.getCredential("user");
		assertEquals("user", credential.getUser());
		assertEquals(GrapesAuthenticator.encrypt("password"), credential.getPassword());
		
	}
}
