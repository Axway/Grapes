package org.axway.grapes.server.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GrapesEmail {
    private Properties properties;
    private InternetAddress from;
    private Session session;

    static String NOT_ALL_PROPERTIES_ERROR = "Not all properties were provided";
    static Logger logger = Logger.getLogger(GrapesEmail.class.getName());

    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
    public static final String MAIL_SMTP_USER = "mail.smtp.user";
    public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";
    public static final String MAIL_DEBUG = "mail.debug";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    public boolean setup(final Properties props) {
        if (!isValid(props)) {
            logger.log(Level.SEVERE, NOT_ALL_PROPERTIES_ERROR);
            return false;
        }
        this.properties = new Properties();
        this.properties.putAll(getSmtpProperties());
        this.properties.putAll(props);

        logger.info("Setting up server.....");
        // Get the default Session object.
        session = Session.getDefaultInstance(properties);
        session.setDebug(Boolean.parseBoolean(properties.get("mail.debug")
                .toString()));

        try {
            from = new InternetAddress(
                    properties.getProperty("mail.smtp.user"),
                    properties.getProperty("mail.smtp.from"));
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE,
                    "UnsupportedEncodingException :" + ex.getMessage() + ex);
            return false;
        }
        return true;
    }

    public boolean isSetup() {
        if (from == null) {
            return false;
        }
        return true;
    }

    private boolean isValid(final Properties properties) {
        String[] requiredProperties = { MAIL_SMTP_HOST, MAIL_SMTP_USER,
                MAIL_SMTP_PASSWORD, MAIL_SMTP_SSL_TRUST, MAIL_SMTP_FROM };

        for (String prop : requiredProperties) {
            if (!properties.containsKey(prop)) {
                return false;
            }
        }

        return true;
    }

    public boolean send(final String[] recipients, final String[] ccRecipients,
            final String subject, final String message, final String contentType) {
        try {

            Message msg = new MimeMessage(session);
            msg.setFrom(from);

            InternetAddress[] toAddresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                toAddresses[i] = new InternetAddress(recipients[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            InternetAddress[] bccAddresses = new InternetAddress[ccRecipients.length];
            for (int j = 0; j < ccRecipients.length; j++) {
                bccAddresses[j] = new InternetAddress(ccRecipients[j]);
            }
            msg.setRecipients(Message.RecipientType.BCC, bccAddresses);
            msg.setSubject(subject);
            msg.setContent(message, contentType);

            logger.info("Sending mail.....");
            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(properties.getProperty(MAIL_SMTP_HOST),
                    properties.getProperty(MAIL_SMTP_USER),
                    properties.getProperty(MAIL_SMTP_PASSWORD));

            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            return true;
        } catch (MessagingException ex) {
            logger.log(Level.SEVERE,
                    "Exception while sending mail :" + ex.getMessage() + ex);
            return false;
        }

    }

    // default values
    public Properties getSmtpProperties() {
        Properties smtpProperties = new Properties();
        smtpProperties.put(MAIL_SMTP_STARTTLS_ENABLE, "true");
        smtpProperties.put(MAIL_SMTP_AUTH, true);
        smtpProperties.put(MAIL_SMTP_PORT, "25");
        smtpProperties.put(MAIL_SMTP_AUTH, "true");
        smtpProperties.put(MAIL_SMTP_SSL_TRUST, "mail.axway.int");
        return smtpProperties;
    }

    public Properties getProperties() {
        return properties;
    }
}
