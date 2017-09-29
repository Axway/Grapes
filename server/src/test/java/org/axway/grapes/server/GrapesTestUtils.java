package org.axway.grapes.server;

import com.google.common.collect.Lists;

import org.axway.grapes.server.config.GrapesEmailConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.axway.grapes.server.db.datamodel.DbOrganization;
import org.axway.grapes.server.webapp.auth.GrapesAuthenticator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GrapesTestUtils {

    public final static String ORGANIZATION_NAME_4TEST = "corp";
    public final static String CORPORATE_GROUPID_4TEST = "com.corporate.test";

    public final static String USER_4TEST = "user";
    public final static String PASSWORD_4TEST = "password";
    
    public final static String UNAUTHORIZED_USER_FOR_POSTING = "user1";

    public final static String WRONG_USER_4TEST = "wrongUser";
    public final static String WRONG_PASSWORD_4TEST = "wrongPassword";

    public final static String MISSING_LICENSE_GROUPID_4TEST = "org.missing.license";
    public final static String MISSING_LICENSE_MESSAGE_4TEST = "The module you are trying to promote has dependencies that miss the license information: ";
    public final static String MISSING_LICENSE_ARTIFACTID_4TEST = "MissingLicense";
    public static final String ARTIFACT_VERSION_4TEST = "1.2.3";
    public static final String COLON = ":";
    public static final String ARTIFACT_CLASSIFIER_4TEST = "classifier";
    public static final String ARTIFACT_EXTENSION_4TEST = "extension";

    public static RepositoryHandler getRepoHandlerMock() {
        try{
            final RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);

            final DbCredential user = new DbCredential();
            user.setUser(USER_4TEST);
            user.setPassword(GrapesAuthenticator.encrypt(PASSWORD_4TEST));
            user.addRole(AvailableRoles.ARTIFACT_CHECKER);
            user.addRole(AvailableRoles.DATA_DELETER);
            user.addRole(AvailableRoles.DATA_UPDATER);
            user.addRole(AvailableRoles.DEPENDENCY_NOTIFIER);
            user.addRole(AvailableRoles.LICENSE_CHECKER);
            user.addRole(AvailableRoles.LICENSE_SETTER);
            when(repositoryHandler.getCredential(USER_4TEST)).thenReturn(user);

            final DbCredential wrongUser = new DbCredential();
            wrongUser.setUser(WRONG_USER_4TEST);
            wrongUser.setPassword(GrapesAuthenticator.encrypt(WRONG_PASSWORD_4TEST));
            when(repositoryHandler.getCredential(WRONG_USER_4TEST)).thenReturn(wrongUser);

            final DbOrganization organization = new DbOrganization();
            organization.setName(ORGANIZATION_NAME_4TEST);
            organization.getCorporateGroupIdPrefixes().add(CORPORATE_GROUPID_4TEST);
            when(repositoryHandler.getOrganization(ORGANIZATION_NAME_4TEST)).thenReturn(organization);
            when(repositoryHandler.getAllOrganizations()).thenReturn(Lists.newArrayList(organization));
            
            final DbCredential unAuthorizedUser = new DbCredential();
            unAuthorizedUser.setUser(UNAUTHORIZED_USER_FOR_POSTING);
            unAuthorizedUser.setPassword(GrapesAuthenticator.encrypt(PASSWORD_4TEST));
            unAuthorizedUser.addRole(AvailableRoles.ARTIFACT_CHECKER);
            unAuthorizedUser.addRole(AvailableRoles.DATA_DELETER);
            unAuthorizedUser.addRole(AvailableRoles.DEPENDENCY_NOTIFIER);
            unAuthorizedUser.addRole(AvailableRoles.LICENSE_CHECKER);
            when(repositoryHandler.getCredential(UNAUTHORIZED_USER_FOR_POSTING)).thenReturn(unAuthorizedUser);

            return repositoryHandler;

        }catch (Exception e){
            System.err.println("Failed to mock Grapes configuration due to password encryption error.");
        }

        return mock(RepositoryHandler.class);
    }

    public static GrapesServerConfig getGrapesConfig() {
        GrapesServerConfig config = mock(GrapesServerConfig.class);
        List<String> validatedTypes = new ArrayList<String>();
        validatedTypes.add("filetype1");
        validatedTypes.add("filetype2");
        when(config.getExternalValidatedTypes()).thenReturn(validatedTypes);
        when(config.getArtifactNotificationRecipients()).thenReturn(new String[] {"toto@axway.com"});

        GrapesEmailConfig emailCfgMock = mock(GrapesEmailConfig.class);
        when(config.getGrapesEmailConfig()).thenReturn(emailCfgMock);

        Properties p = new Properties();
        p.setProperty("mail.smtp.host", "1");
        p.setProperty("mail.smtp.user", "2");
        p.setProperty("mail.smtp.password", "3");
        p.setProperty("mail.smtp.ssl.trust", "4");
        p.setProperty("mail.smtp.from", "5");

        when(emailCfgMock.getProperties()).thenReturn(p);

        return config;
    }

}
