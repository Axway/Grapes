package org.axway.grapes.core.webapi.tasks;

import org.wisdom.api.DefaultController;

/**
 * Add User Task
 * 
 * <p>At runtime, this task is able to add credentials to Grapes.
 * To add a user: POST <host>:<adminPort>/addUser?user=<user>&password=<password> <p>
 * 
 * @author jdcoffre
 */
public class AddUserTask extends DefaultController {

/*	private final RepositoryHandler repoHandler;

	public AddUserTask(final RepositoryHandler repoHandler) {
		super("addUser");
		this.repoHandler = repoHandler;
	}

	@Override
	public void execute(final ImmutableMultimap<String, String> args, final PrintWriter printer) throws AuthenticationException {
		printer.println("Adding/update credentials ...");
        final DbCredential credential = new DbCredential();
		credential.setUser(args.get(ServerAPI.USER_PARAM).asList().get(0));
		credential.setPassword(GrapesAuthenticator.encrypt(args.get(ServerAPI.PASSWORD_PARAM).asList().get(0)));
        
        if(!credential.isHealthy()){
            printer.println("ERROR: Bad request! The provided credential are unhealthy or incomplete.");
            return;
        }
        
		repoHandler.store(credential);

		printer.println("Task performed successfully.");
	}
*/
}