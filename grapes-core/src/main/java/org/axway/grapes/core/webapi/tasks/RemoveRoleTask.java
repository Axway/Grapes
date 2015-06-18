package org.axway.grapes.core.webapi.tasks;

import org.wisdom.api.DefaultController;

/**
 * Add Role Task
 * 
 * <p>At runtime, this task is able to remove a role from Grapes user.
 * To add a user: POST <host>:<adminPort>/removeRole?user=<user>&</>role=<role><p>
 * 
 * @author jdcoffre
 */
public class RemoveRoleTask extends DefaultController {

/*	private final RepositoryHandler repoHandler;

	public RemoveRoleTask(final RepositoryHandler repoHandler) {
		super("removeRole");
		this.repoHandler = repoHandler;
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

		printer.println("Task performed successfully.");
	}*/

}