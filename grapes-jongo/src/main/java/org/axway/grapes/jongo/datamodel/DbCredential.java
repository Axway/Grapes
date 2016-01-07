package org.axway.grapes.jongo.datamodel;

import org.axway.grapes.model.datamodel.Credential;
import org.jongo.marshall.jackson.oid.Id;

/**
 * Database Credential
 * <p>
 * <p>Class that represent Grapes credentials that are stored in the database.
 * Passwords have to be encrypted.</p>
 *
 * @author jdcoffre
 */
public class DbCredential extends Credential {

    public static final String DATA_MODEL_VERSION = "datamodelVersion";
    private String datamodelVersion = DbCollections.datamodelVersion;

    @Id
    private String user;

    public DbCredential(Credential credential) {
        this.user = credential.getUser();
        setUser(credential.getUser());
        setPassword(credential.getPassword());
        setRoles(credential.getRoles());
    }

    public DbCredential() {
    }

    @Override
    public void setUser(String user) {
        this.user = user;

    }

    @Override
    public String getUser() {
        return user;
    }
    public boolean isHealthy() {
        return user != null && super.getPassword() != null && !user.isEmpty() && !super.getPassword().isEmpty();
    }

    public void setDataModelVersion(final String newVersion) {
        this.datamodelVersion = newVersion;
    }

    public String getDataModelVersion() {
        return datamodelVersion;
    }

}
