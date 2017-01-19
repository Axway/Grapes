package org.axway.grapes.server.core.services.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class GrapesEmailSender {
    private Properties properties;
    private InternetAddress from;
    private Session session;

    static final String NOT_ALL_PROPERTIES_ERROR = "Not all properties were provided";
    private static final Logger LOG = LoggerFactory.getLogger(GrapesEmailSender.class);

    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
    public static final String MAIL_SMTP_USER = "mail.smtp.user";
    public static final String MAIL_SPECIAL_FIELD = "mail.smtp.password";
    public static final String MAIL_SMTP_FROM = "mail.smtp.from";
    public static final String MAIL_DEBUG = "mail.debug";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    public GrapesEmailSender(final Properties emailProperties) throws UnsupportedEncodingException {
        setup(emailProperties);
    }

    private void setup(final Properties props) throws UnsupportedEncodingException {
        if (!isValid(props)) {
            LOG.warn(NOT_ALL_PROPERTIES_ERROR);
            throw new IllegalArgumentException("Not all the mandatory fields were found. Mandatory: " + Arrays.toString(getRequiredPropertyNames()));
        }

        this.properties = new Properties();
        this.properties.putAll(getSmtpProperties());
        this.properties.putAll(props);

        LOG.info("Setting up Grapes Email server.....");

        // Get the default Session object.
        session = Session.getDefaultInstance(properties);

        String debugStr = properties.getProperty(MAIL_DEBUG, "false");
        session.setDebug(Boolean.parseBoolean(debugStr));

        from = new InternetAddress(
                properties.getProperty(MAIL_SMTP_USER),
                properties.getProperty(MAIL_SMTP_FROM));
    }

    private boolean isValid(final Properties properties) {
        String[] requiredProperties = getRequiredPropertyNames();

        for (String prop : requiredProperties) {
            if (!properties.containsKey(prop)) {
                return false;
            }
        }

        return true;
    }

    private String[] getRequiredPropertyNames() {
        return new String[]{MAIL_SMTP_HOST, MAIL_SMTP_USER,
                MAIL_SPECIAL_FIELD, MAIL_SMTP_SSL_TRUST, MAIL_SMTP_FROM};
    }

    public void send(final String[] recipients, final String subject, final String message) {
        send(recipients, subject, message, "text/html");
    }

    public void send(final String[] recipients,
                     final String subject,
                     final String message,
                     final String contentType) {

        if (null == recipients) {
            throw new IllegalArgumentException("Cannot use null recipients");
        }

        if (!areThereRecipients(recipients)) {
            LOG.warn("No address specified on neither recipients nor cc. No email was sent.");
            return;
        }

        Thread senderThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Message msg = new MimeMessage(session);
                    msg.setFrom(from);

                    InternetAddress[] toAddresses = new InternetAddress[recipients.length];

                    for (int i = 0; i < recipients.length; i++) {
                        toAddresses[i] = new InternetAddress(recipients[i]);
                    }

                    msg.setRecipients(Message.RecipientType.TO, toAddresses);

                    msg.setSubject(subject);
                    msg.setContent(message, contentType);

                    LOG.debug("Sending mail.....");

                    // Send message
                    Transport transport = session.getTransport("smtp");
                    transport.connect(properties.getProperty(MAIL_SMTP_HOST),
                            properties.getProperty(MAIL_SMTP_USER),
                            properties.getProperty(MAIL_SPECIAL_FIELD));

                    transport.sendMessage(msg, msg.getAllRecipients());
                    transport.close();

                    LOG.info("Successfully notified " + getDestinationAddresses(recipients));

                } catch (MessagingException ex) {
                    LOG.warn("Exception while sending mail :" + ex.getMessage(), ex);
                }
            }
        });

        senderThread.start();
    }

    // default values
    public Properties getSmtpProperties() {
        Properties smtpProperties = new Properties();
        smtpProperties.put(MAIL_SMTP_STARTTLS_ENABLE, "true");
        smtpProperties.put(MAIL_SMTP_AUTH, true);
        smtpProperties.put(MAIL_SMTP_PORT, "25");
        smtpProperties.put(MAIL_SMTP_SSL_TRUST, "mail.axway.int");
        return smtpProperties;
    }

    private boolean areThereRecipients(String[] recipients) {
        return recipients.length > 0;
    }

    private String getDestinationAddresses(String[] recipients) {
        return Arrays.toString(recipients);
    }

    public Properties getProperties() {
        return properties;
    }
}
