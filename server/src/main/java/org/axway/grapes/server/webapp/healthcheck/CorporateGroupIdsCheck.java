package org.axway.grapes.server.webapp.healthcheck;

import com.yammer.metrics.core.HealthCheck;
import org.axway.grapes.server.db.RepositoryHandler;

import java.util.Iterator;
import java.util.List;

/**
 * Corporate GroupIds HealthCheck
 *
 * <p>Provides the list of groupIds that are considered as corporate production</p>
 *
 * @author jdcoffre
 */
public class CorporateGroupIdsCheck extends HealthCheck {

    private final RepositoryHandler repoHandler;

    public CorporateGroupIdsCheck(final RepositoryHandler repoHandler) {
        super("corporate groupIds");
        this.repoHandler = repoHandler;
    }

    @Override
    protected Result check() {
        final StringBuilder sb = new StringBuilder();

        try{
            final List<String> corporateGroupIds = repoHandler.getCorporateGroupIds();

            final Iterator<String> groupIdIterator = corporateGroupIds.iterator();

            if(groupIdIterator.hasNext()){
                sb.append("List of groupIds configured has corporate groupId: ");
                while(groupIdIterator.hasNext()){
                    sb.append(groupIdIterator.next());
                    if(groupIdIterator.hasNext()){
                        sb.append(", ");
                    }
                }
            }
            else{
                sb.append("No groupId is configured has corporate groupId yet.");
            }
        }
        catch (Exception e) {
            return Result.unhealthy(e);
        }

        return Result.healthy(sb.toString());
    }
}
