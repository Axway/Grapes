package org.axway.grapes.jenkins.config;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Credentials
 *
 * <p>Holds the credentials for Grapes authentication </p>
 *
 * @author jdcoffre
 */
public class Credentials {

    private String username;

    private String password;

    @DataBoundConstructor
    public Credentials(final String username, final String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
