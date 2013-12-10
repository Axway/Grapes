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
 * Add Corporate GroupId
 * 
 * <p>At runtime, this task makes the administrator able to add a groupId
 * considered as a corporate groupId<p>
 * 
 * @author jdcoffre
 */
public class AddCorporateGroupIdTask extends Task{

	private final RepositoryHandler repoHandler;
    private final GrapesServerConfig config;

    public AddCorporateGroupIdTask(final RepositoryHandler repoHandler, final GrapesServerConfig config) {
		super("addCorporateGroupId");
		this.repoHandler = repoHandler;
        this.config = config;
	}

	@Override
	public void execute(final ImmutableMultimap<String, String> args, final PrintWriter printer) throws GrapesException {
		printer.println("Adding corporate groupId ...");
        final String corporateGroupId = args.get(ServerAPI.GROUPID_PARAM).asList().get(0);

        if(corporateGroupId == null){
            printer.println("ERROR: Bad request! Missing groupId.");
            return;
        }

        if(isInvalid(corporateGroupId)){
            printer.println("ERROR: Bad request! Invalid groupId.");
            return;
        }

        final List<String> corporateGroupIds = config.getCorporateGroupIds();
        if(corporateGroupIds.contains(corporateGroupId)){
            printer.println("ERROR: The provided groupId already exist.");
            return;
        }

        corporateGroupIds.add(corporateGroupId);
        repoHandler.addNewCorporateGroupId(corporateGroupId);
        config.loadGroupIds(repoHandler);
		printer.println("Task performed successfully.");
	}

    private boolean isInvalid(final String corporateGroupId) {
        final String regex = "(?i)(.*(\\\\|\\*|/|\'|\").*)";
        return corporateGroupId.matches(regex);
    }


}