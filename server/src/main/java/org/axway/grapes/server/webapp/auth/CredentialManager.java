package org.axway.grapes.server.webapp.auth;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import org.apache.commons.codec.binary.Base64;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.datamodel.DbCredential;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.List;

/**
 * Credential Manager
 * 
 * <p>Handle the credentials on Grapes.</p>
 * 
 * @author jdcoffre
 */
public class CredentialManager implements Authenticator<BasicCredentials, List<AvailableRoles>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CredentialManager.class);
	private final GrapesServerConfig config;
	
	/**
	 * Loads the available user/password from the database
	 */
	public CredentialManager(final GrapesServerConfig config) {
		this.config = config;
	}

	public Optional<List<AvailableRoles>> authenticate(final BasicCredentials authentication) throws AuthenticationException {
		if(authentication == null || authentication.getUsername() == null || authentication.getPassword() == null){
        	LOG.error("There are missing information for the authentication");
			return Optional.absent();
		}
		
		DbCredential credential = null;

        for(DbCredential configCred : config.getCredentials()){
            if(configCred.getUser().equals(authentication.getUsername())){
                credential = configCred;
                break;
            }
        }
		
		if(credential == null){
        	LOG.error("Unknown user");
			return Optional.absent();
		}
		
		else if(credential.getPassword().equals(encrypt(authentication.getPassword()))){
			return Optional.of(credential.getRoles());
		} 

    	LOG.error("Wrong password");
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
        	throw new AuthenticationException("Error occured during password encryption", e);
        } 
        
        return hashValue;
    }
}
