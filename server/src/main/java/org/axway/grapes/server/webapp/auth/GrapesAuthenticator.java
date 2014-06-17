package org.axway.grapes.server.webapp.auth;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import org.apache.commons.codec.binary.Base64;
import org.axway.grapes.server.db.RepositoryHandler;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * Grapes Authenticator
 *
 * <p>Handles all Grapes authentications</p>
 *
 * @author jdcoffre
 */
public class GrapesAuthenticator implements Authenticator<BasicCredentials, DbCredential> {

    private static final Logger LOG = LoggerFactory.getLogger(GrapesAuthenticator.class);

    private final RepositoryHandler repoHandler;

    public GrapesAuthenticator(final RepositoryHandler repoHandler) {
        this.repoHandler = repoHandler;
    }

    @Override
    public Optional<DbCredential> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if(credentials == null || credentials.getUsername() == null || credentials.getPassword() == null){
            LOG.error("Missing credentials for the authentication");
            return Optional.absent();
        }

        final String encryptedPwd = encrypt(credentials.getPassword());

        final DbCredential dbCredential = repoHandler.getCredential(credentials.getUsername());

        if(dbCredential != null && encryptedPwd.equals(dbCredential.getPassword())){
            return Optional.of(dbCredential);
        }

        return Optional.absent();
    }

    /**
     * Encrypt passwords
     *
     * @param password
     * @return String
     * @throws AuthenticationException
     */
    public static String encrypt(final String password) throws AuthenticationException {
        String hashValue;
        try {
            final MessageDigest msgDigest = MessageDigest.getInstance("SHA");
            msgDigest.update(password.getBytes("UTF-8"));
            final byte rawByte[] = msgDigest.digest();
            hashValue = new String(Base64.encodeBase64(rawByte));

        } catch (Exception e) {
            LOG.error("Encryption failed.");
            throw new AuthenticationException("Error occurred during password encryption", e);
        }

        return hashValue;
    }
}
