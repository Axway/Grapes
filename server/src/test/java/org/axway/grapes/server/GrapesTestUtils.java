package org.axway.grapes.server;

import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GrapesTestUtils {

    public final static String ORGANIZATION_NAME_4TEST = "corp";
    public final static String CORPORATE_GROUPID_4TEST = "com.corporate.test";

    public final static String USER_4TEST = "user";
    public final static String PASSWORD_4TEST = "password";

    public static List<String> getTestCorporateGroupIds(){
        final List<String> corporateGroupIds = new ArrayList<String>();
        corporateGroupIds.add(CORPORATE_GROUPID_4TEST);

        return corporateGroupIds;
    }

    public static RepositoryHandler getRepoHandlerMock() {
        final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);

        try{

            final DbCredential user = new DbCredential();
            user.setUser(USER_4TEST);
            user.setPassword(GrapesAuthenticator.encrypt(PASSWORD_4TEST));
            user.addRole(AvailableRoles.ARTIFACT_CHECKER);
            user.addRole(AvailableRoles.DATA_DELETER);
            user.addRole(AvailableRoles.DATA_UPDATER);
            user.addRole(AvailableRoles.DEPENDENCY_NOTIFIER);
            user.addRole(AvailableRoles.LICENSE_CHECKER);

            when(repositoryHandler.getCredential(USER_4TEST)).thenReturn(user);

        }catch (Exception e){
            System.err.println("Failed to mock Grapes configuration due to password encryption error.");
        }

        return repositoryHandler;
    }
}
