package org.axway.grapes.server.webapp.auth;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.*;

public class CredentialManagerTest {
    
    @Test
	public void testEncryption() {
		final String password = "myPassword";
		Exception exception = null;
		String encryptedPassword1 = null,  encryptedPassword2 = null;
		
		try {
			encryptedPassword1 = CredentialManager.encrypt(password);
			encryptedPassword2 = CredentialManager.encrypt(password); 
		} catch (Exception e) {
			exception = e;
		}
		
		assertNull(exception);
		assertNotNull(encryptedPassword1);
		assertFalse(password.equals(encryptedPassword1));
		assertNotNull(encryptedPassword2);
		assertEquals(encryptedPassword1, encryptedPassword2);
	}

	@Test
	public void testWrongEncryption() {
		final String password = null;
		Exception exception = null;
		
		try {
			CredentialManager.encrypt(password);
		} catch (Exception e) {
			exception = e;
		}
		
		assertNotNull(exception);
	}
	
	@Test
	public void checkAutentication() throws AuthenticationException, UnknownHostException{
        final GrapesServerConfig config = GrapesTestUtils.getConfigMock();
		final CredentialManager authentificator = new CredentialManager(config);
		
		Exception exception = null;
		Optional<List<AvailableRoles>> result = null;
		
		try {
			result = authentificator.authenticate(new BasicCredentials(GrapesTestUtils.USER_4TEST, GrapesTestUtils.PASSWORD_4TEST));
			
		} catch (Exception e) {
			exception = e;
		}

		assertNull(exception);
		assertNotNull(result);
		assertTrue(result.isPresent());

        final List<AvailableRoles> roles = result.get();
        assertNotNull(roles);
        assertTrue(roles.contains(AvailableRoles.ARTIFACT_CHECKER));
        assertTrue(roles.contains(AvailableRoles.DATA_DELETER));
        assertTrue(roles.contains(AvailableRoles.DATA_UPDATER));
        assertTrue(roles.contains(AvailableRoles.DEPENDENCY_NOTIFIER));
        assertTrue(roles.contains(AvailableRoles.LICENSE_CHECKER));
	}
	
	@Test
	public void checkWrongAutentication() throws AuthenticationException, UnknownHostException{
        final GrapesServerConfig config = GrapesTestUtils.getConfigMock();
        final CredentialManager authentificator = new CredentialManager(config);

        Exception exception = null;
        Optional<List<AvailableRoles>> result = null;
		
		try {
			result = authentificator.authenticate(new BasicCredentials(GrapesTestUtils.USER_4TEST, "wrongPassword"));
			
		} catch (Exception e) {
			exception = e;
		}

		assertNull(exception);
		assertNotNull(result);
		assertFalse(result.isPresent());
		
		exception = null;
		result = null;
		
		try {
			result = authentificator.authenticate(new BasicCredentials("userWrong", GrapesTestUtils.PASSWORD_4TEST));
			
		} catch (Exception e) {
			exception = e;
		}

		assertNull(exception);
		assertNotNull(result);
		assertFalse(result.isPresent());
		
		exception = null;
		result = null;
		
		try {
			result = authentificator.authenticate(null);
			
		} catch (Exception e) {
			exception = e;
		}

		assertNull(exception);
		assertNotNull(result);
		assertFalse(result.isPresent());
	}
    
}
