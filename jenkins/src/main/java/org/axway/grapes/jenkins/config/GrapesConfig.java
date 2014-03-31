package org.axway.grapes.jenkins.config;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Grapes Server Configuration
 *
 * <p>Holds the configuration of a Grapes server</p>
 *
 * @author jdcoffre
 */
public class GrapesConfig {

    private String name;

    private String host;

    private int port;

    private Credentials publisherCredentials;

    private int timeout;

    @DataBoundConstructor
    public GrapesConfig(final String name, final String host, final int port, final int timeout, final Credentials publisherCredentials){
        this.name = name;
        this.host = host;
        this.port = port;
        this.publisherCredentials = publisherCredentials;
        this.timeout = timeout;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public Credentials getPublisherCredentials() {
        return publisherCredentials;
    }

    public void setPublisherCredentials(final Credentials publisherCredentials) {
        this.publisherCredentials = publisherCredentials;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }
}
