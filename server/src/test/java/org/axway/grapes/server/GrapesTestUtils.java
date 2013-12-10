package org.axway.grapes.server;

import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.webapp.auth.CredentialManager;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GrapesTestUtils {

    public final static String CORPORATE_GROUPID_4TEST = "com.corporate.test";

    public final static String USER_4TEST = "user";
    public final static String PASSWORD_4TEST = "password";

    public static List<String> getTestCorporateGroupIds(){
        final List<String> corporateGroupIds = new ArrayList<String>();
        corporateGroupIds.add(CORPORATE_GROUPID_4TEST);

        return corporateGroupIds;
    }

    public static GrapesServerConfig getConfigMock() {
        final GrapesServerConfig config = mock(GrapesServerConfig.class);

        try{
            final List<DbCredential> credentials = new ArrayList<DbCredential>();

            final DbCredential user = new DbCredential();
            user.setUser(USER_4TEST);
            user.setPassword(CredentialManager.encrypt(PASSWORD_4TEST));
            user.addRole(AvailableRoles.ARTIFACT_CHECKER);
            user.addRole(AvailableRoles.DATA_DELETER);
            user.addRole(AvailableRoles.DATA_UPDATER);
            user.addRole(AvailableRoles.DEPENDENCY_NOTIFIER);
            user.addRole(AvailableRoles.LICENSE_CHECKER);
            credentials.add(user);

            when(config.getCredentials()).thenReturn(credentials);
            when(config.getCorporateGroupIds()).thenReturn(getTestCorporateGroupIds());
            when(config.getAuthenticationCachePolicy()).thenReturn("maximumSize=1000, expireAfterAccess=10m");
        }catch (Exception e){
            System.err.println("Failed to mock Grapes configuration due to password encryption error.");
        }

        return config;
    }
}
