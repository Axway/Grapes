package org.axway.grapes.server.config;

import java.util.Properties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axway.grapes.server.email.GrapesEmail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class MailConfig extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private String host;

    @Valid
    @NotNull
    @JsonProperty
    private int port;

    @Valid
    @JsonProperty
    private String user;

    @Valid
    @JsonProperty
    private String pwd;

    @Valid
    @NotNull
    @JsonProperty
    private String sslTrust;

    @Valid
    @NotNull
    @JsonProperty
    private String smtpFrom;

    private Properties mailProperties;

    public Properties getProperties() {
        if (mailProperties != null) {
            return mailProperties;
        }

        mailProperties = new Properties();
        mailProperties.put(GrapesEmail.MAIL_SMTP_HOST, host);
        mailProperties.put(GrapesEmail.MAIL_SMTP_USER, port);
        mailProperties.put(GrapesEmail.MAIL_SMTP_PASSWORD, pwd);
        mailProperties.put(GrapesEmail.MAIL_SMTP_SSL_TRUST, sslTrust);
        mailProperties.put(GrapesEmail.MAIL_SMTP_FROM, user);
        mailProperties.put(GrapesEmail.MAIL_DEBUG, true);

        return mailProperties;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getSslTrust() {
        return sslTrust;
    }

    public String getSmtpFrom() {
        return smtpFrom;
    }
}
