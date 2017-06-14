package org.axway.grapes.server.config;

import java.util.Properties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.axway.grapes.server.core.services.email.GrapesEmailSender;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class GrapesEmailConfig extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private String host;

    @Valid
    @NotNull
    @JsonProperty
    private int port = 25;

    @Valid
    @NotNull
    @JsonProperty
    private String user;

    @Valid
    @NotNull
    @JsonProperty
    private String smtpFrom;

    @Valid
    @JsonProperty
    private String pwd;


    @Valid
    @JsonProperty
    private boolean debug = false;

    @Valid
    @JsonProperty
    private boolean auth = false;


    private Properties mailProperties;

    public Properties getProperties() {
        if (mailProperties != null) {
            return mailProperties;
        }

        mailProperties = new Properties();

        mailProperties.put(GrapesEmailSender.MAIL_SMTP_PORT, Integer.toString(port));
        mailProperties.put(GrapesEmailSender.MAIL_DEBUG, debug);

        mailProperties.put(GrapesEmailSender.MAIL_SMTP_HOST, host);
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_USER, user);
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_SSL_TRUST, host);
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_FROM, smtpFrom);

        mailProperties.put(GrapesEmailSender.MAIL_SMTP_AUTH, auth);

        if(pwd != null) {
            mailProperties.put(GrapesEmailSender.MAIL_SPECIAL_FIELD, pwd);
        }

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

    public String getSmtpFrom() {
        return smtpFrom;
    }
}
