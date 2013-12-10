package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;

import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Add Role Task
 * 
 * <p>At runtime, this task is able to remove a role from Grapes user.
 * To add a user: POST <host>:<adminPort>/removeRole?user=<user>&</>role=<role><p>
 * 
 * @author jdcoffre
 */
public class RemoveRoleTask extends Task{

	private final GrapesServerConfig config;
	private final RepositoryHandler repoHandler;

	public RemoveRoleTask(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
		super("removeRole");
		this.repoHandler = repoHandler;
		this.config = config;
	}

	@Override
	public void execute(final ImmutableMultimap<String, String> args, final PrintWriter printer) throws AuthenticationException, UnknownHostException {
		printer.println("Removing role...");
        final String user = args.get(ServerAPI.USER_PARAM).asList().get(0);
        final String roleParam = args.get(ServerAPI.USER_ROLE_PARAM).asList().get(0);
        final AvailableRoles role = DbCredential.getRole(roleParam);

        if(user == null || role == null){
            printer.println("ERROR: Bad request! The provided credential are unhealthy or incomplete.");
            return;
        }
        
		repoHandler.removeUserRole(user, role);
        config.loadCredentials(repoHandler);

		printer.println("Task performed successfully.");
	}

}