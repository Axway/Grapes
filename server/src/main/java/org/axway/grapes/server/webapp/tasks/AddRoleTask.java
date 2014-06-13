package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;

import java.io.PrintWriter;
import java.net.UnknownHostException;

/**
 * Add Role Task
 * 
 * <p>At runtime, this task is able to add a role to Grapes user.
 * To add a user: POST <host>:<adminPort>/addRole?user=<user>&</>role=<role><p>
 * 
 * @author jdcoffre
 */
public class AddRoleTask extends Task{

	private final RepositoryHandler repoHandler;

	public AddRoleTask(final RepositoryHandler repoHandler) {
		super("addRole");
		this.repoHandler = repoHandler;
	}

	@Override
	public void execute(final ImmutableMultimap<String, String> args, final PrintWriter printer) throws AuthenticationException, UnknownHostException {
		printer.println("Adding role...");
        final String user = args.get(ServerAPI.USER_PARAM).asList().get(0);
        final String roleParam = args.get(ServerAPI.USER_ROLE_PARAM).asList().get(0);
        final AvailableRoles role = DbCredential.getRole(roleParam);

        if(user == null || role == null){
            printer.println("ERROR: Bad request! The provided credential are unhealthy or incomplete.");
            return;
        }
        
		repoHandler.addUserRole(user, role);

		printer.println("Task performed successfully.");
	}

}