package org.axway.grapes.core.service;

import org.axway.grapes.model.datamodel.Credential;

/**
 * Created by jennifer on 5/15/15.
 */
public interface CredentialService {

    public void store(final Credential credential);

    public void addUserRole(final String user, final Credential.AvailableRoles role);

    public void removeUserRole(final String user, final Credential.AvailableRoles role);

    public Credential getCredential(final String user);
}
