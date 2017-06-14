package org.axway.grapes.server.email;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.axway.grapes.server.core.services.email.GrapesEmailSender;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GrapesEmailTest {

    @Rule
    public ExpectedException exc = ExpectedException.none();

    @Test
    public void validPropertiesTest() throws UnsupportedEncodingException {
        Properties mailProperties = new Properties();
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_HOST, "a host");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_PORT, "a port");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_USER, "foo@email.com");
        mailProperties.put(GrapesEmailSender.MAIL_SPECIAL_FIELD, "***");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_SSL_TRUST, "a host");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_FROM, "foo@email.com");
        mailProperties.put(GrapesEmailSender.MAIL_DEBUG, true);

        GrapesEmailSender grapesEmailSender = new GrapesEmailSender(mailProperties);

        Properties defaultProps = grapesEmailSender.getDefaultSmtpProperties();

        // Checking if all default values are existing
        assertTrue(defaultProps.containsKey(GrapesEmailSender.MAIL_SMTP_STARTTLS_ENABLE));
        assertTrue(defaultProps.containsKey(GrapesEmailSender.MAIL_SMTP_AUTH));
    }


    @Test
    public void incompleteSetupThrowsIllegalArgumentException() throws UnsupportedEncodingException {
        Properties mailProperties = new Properties();
        // SMTP host is missing
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_PORT, "a port");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_USER, "foo@email.com");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_SSL_TRUST, "a host");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_FROM, "foo@email.com");
        mailProperties.put(GrapesEmailSender.MAIL_DEBUG, true);

        exc.expect(IllegalArgumentException.class);
        new GrapesEmailSender(mailProperties);

    }

    @Test
    public void testConfiguredValueOverridesDefault() throws UnsupportedEncodingException {
        Properties mailProperties = new Properties();
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_HOST, "a host");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_PORT, "8025");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_USER, "foo@email.com");
//        mailProperties.put(GrapesEmailSender.MAIL_SPECIAL_FIELD, "***");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_SSL_TRUST, "a host");
        mailProperties.put(GrapesEmailSender.MAIL_SMTP_FROM, "foo@email.com");
        mailProperties.put(GrapesEmailSender.MAIL_DEBUG, true);

        GrapesEmailSender grapesEmailSender = new GrapesEmailSender(mailProperties);

        Properties properties = grapesEmailSender.getProperties();
        assertNotNull(properties);
        assertEquals("8025", properties.get(GrapesEmailSender.MAIL_SMTP_PORT));
    }
}
