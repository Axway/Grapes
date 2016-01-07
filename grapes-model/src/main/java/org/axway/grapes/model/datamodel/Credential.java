package org.axway.grapes.model.datamodel;


import java.util.ArrayList;
import java.util.List;


public class Credential {


    /**
     * All the available role for Grapes
     */
    public static enum AvailableRoles {
        // Update dependencies data: module and artifact notification
        DEPENDENCY_NOTIFIER,
        // Update the license attribution and the third party fields "download URL" and "provider"
        DATA_UPDATER,
        // Delete artifacts modules or licenses
        DATA_DELETER,
        // Approve or reject artifacts
        ARTIFACT_CHECKER,
        // Approve or reject licenses
        LICENSE_CHECKER
    }


    private String user="";
    private String password = "";

    public static final String ROLES_FIELD = "roles";
    private List<AvailableRoles> roles = new ArrayList<AvailableRoles>();

    public List<AvailableRoles> getRoles() {
        return roles;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(final List<AvailableRoles> roles) {
        this.roles = roles;
    }

    public void addRole(final AvailableRoles role) {
        roles.add(role);
    }

    public void removeRole(final AvailableRoles role) {
        roles.remove(role);
    }


    public static AvailableRoles getRole(final String roleParam) {
        if (AvailableRoles.LICENSE_CHECKER.toString().equalsIgnoreCase(roleParam)) {
            return AvailableRoles.LICENSE_CHECKER;
        }
        if (AvailableRoles.DEPENDENCY_NOTIFIER.toString().equalsIgnoreCase(roleParam)) {
            return AvailableRoles.DEPENDENCY_NOTIFIER;
        }
        if (AvailableRoles.DATA_UPDATER.toString().equalsIgnoreCase(roleParam)) {
            return AvailableRoles.DATA_UPDATER;
        }
        if (AvailableRoles.DATA_DELETER.toString().equalsIgnoreCase(roleParam)) {
            return AvailableRoles.DATA_DELETER;
        }
        if (AvailableRoles.ARTIFACT_CHECKER.toString().equalsIgnoreCase(roleParam)) {
            return AvailableRoles.ARTIFACT_CHECKER;
        }

        return null;
    }
    public boolean isHealthy() {
        return user != null && password != null && !user.isEmpty() && !password.isEmpty();
    }

}


