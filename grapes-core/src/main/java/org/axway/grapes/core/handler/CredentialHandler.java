package org.axway.grapes.core.handler;
//todo passwords are encypted in what form?

import com.google.common.cache.LoadingCache;
import org.axway.grapes.core.service.CredentialService;
import org.axway.grapes.model.datamodel.Credential;
import org.wisdom.api.annotations.Model;
import org.wisdom.api.annotations.Service;
import org.wisdom.api.model.Crud;

import java.util.NoSuchElementException;

/**
 * Created by jennifer on 5/15/15.
 */
@Service
public class CredentialHandler implements CredentialService {

    //todo see mongo handler for this cache thing
    //there is no cache apprently distrubeted was attempted and failed completly
    private LoadingCache<String, Credential> credentialCache;
    @Model(value = Credential.class)
    private Crud<Credential, String> credentialCrud;

    @Override
    public void store(final Credential credential) {
        Boolean isExsiting = true;
        try {
            final Credential existing = getCredential(credential.getUser());
        } catch (NoSuchElementException e) {
            isExsiting = false;
        }
        if (!isExsiting) {
            if (credential.getUser() != null || credential.getPassword() != null
                    || !credential.getUser().isEmpty() || !credential.getPassword().isEmpty()) {
                credentialCrud.save(credential);
                credentialCache.invalidate(credential.getUser());
            } else {
                System.out.println("cannot create user name or password is null or empty");
            }
        } else {
            //todo throw some error
            System.out.println("omg already exisits, we didnt save it again");
        }
    }

    @Override
    public void addUserRole(final String user, final Credential.AvailableRoles role) {
        final Credential credential = getCredential(user);
        if (!credential.getRoles().contains(role)) {
            credential.addRole(role);
        }
        credentialCrud.save(credential);
        credentialCache.invalidate(credential.getUser());
    }

    @Override
    public void removeUserRole(final String user, final Credential.AvailableRoles role) {
        final Credential credential = getCredential(user);
        if (credential.getRoles().contains(role)) {
            credential.removeRole(role);
        }
        credentialCrud.save(credential);
        credentialCache.invalidate(credential.getUser());
    }

    @Override
    public Credential getCredential(final String user) {
        final Credential credential = credentialCrud.findOne(user);
        if (credential == null) {
            throw new NoSuchElementException(user);
        }
        return credential;
    }
}
