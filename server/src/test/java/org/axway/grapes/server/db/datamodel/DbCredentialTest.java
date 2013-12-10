package org.axway.grapes.server.db.datamodel;

import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.junit.Test;

import static org.junit.Assert.*;

public class DbCredentialTest {

    @Test
    public void isHealthy(){
        DbCredential credential = new DbCredential();
        assertFalse(credential.isHealthy());
        
        credential.setUser("test");
        assertFalse(credential.isHealthy());
        
        credential.setPassword("test");
        assertTrue(credential.isHealthy());
        
    }

    @Test
    public void checkGetRole(){
        assertEquals(null, DbCredential.getRole("wrongRole"));
        assertEquals(AvailableRoles.ARTIFACT_CHECKER, DbCredential.getRole("artifact_checker"));
        assertEquals(AvailableRoles.DATA_DELETER, DbCredential.getRole("DATA_DELETER"));
        assertEquals(AvailableRoles.DATA_UPDATER, DbCredential.getRole("DATA_updater"));
        assertEquals(AvailableRoles.DEPENDENCY_NOTIFIER, DbCredential.getRole("dependency_NOTIFIER"));
        assertEquals(AvailableRoles.LICENSE_CHECKER, DbCredential.getRole("license_checker"));
    }
}
