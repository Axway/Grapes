package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.commons.api.ServerAPI;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.exceptions.GrapesException;
import org.axway.grapes.server.db.RepositoryHandler;

import java.io.PrintWriter;
import java.util.List;

/**
 * Remove Corporate GroupId
 * 
 * <p>At runtime, this task makes the administrator able to remove a groupId
 * which is not considered anymore as a corporate groupId<p>
 * 
 * @author jdcoffre
 */
public class RemoveCorporateGroupIdTask extends Task{

	private final RepositoryHandler repoHandler;
    private final GrapesServerConfig config;

    public RemoveCorporateGroupIdTask(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
		super("removeCorporateGroupId");
		this.repoHandler = repoHandler;
        this.config = config;
	}

	@Override
	public void execute(final ImmutableMultimap<String, String> args, final PrintWriter printer) throws GrapesException {
		printer.println("Removing corporate groupId ...");
        final String corporateGroupId = args.get(ServerAPI.GROUPID_PARAM).asList().get(0);
        
        if(corporateGroupId == null){
            printer.println("ERROR: Bad request! Missing groupId.");
            return;
        }

        final List<String> corporateGroupIds = repoHandler.getCorporateGroupIds();
        if(!corporateGroupIds.contains(corporateGroupId)){
            printer.println("ERROR: The provided groupId does not exist.");
            return;
        }

        repoHandler.removeCorporateGroupId(corporateGroupId);
        config.loadGroupIds(repoHandler);
		printer.println("Task performed successfully.");
	}

}