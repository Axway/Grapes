package org.axway.grapes.jongo.model;

import org.axway.grapes.jongo.datamodel.DbCollections;
import org.axway.grapes.jongo.datamodel.DbCredential;
import org.axway.grapes.model.datamodel.Credential.AvailableRoles;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

    @Test
    public  void checkDataModelVersion(){
        DbCredential credential = new DbCredential();
        credential.setDataModelVersion(DbCollections.datamodelVersion);
        assertThat(credential.getDataModelVersion()).isEqualTo(DbCollections.datamodelVersion);

    }
}
