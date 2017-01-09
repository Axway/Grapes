package org.axway.grapes.server.email;

import static org.junit.Assert.*;

import java.util.Properties;

import org.axway.grapes.server.core.services.GrapesEmail;
import org.junit.Test;

public class GrapesEmailTest {

    @Test
    public void validPropertiesTest() {
        Properties mailProperties = new Properties();
        mailProperties.put(GrapesEmail.MAIL_SMTP_HOST, "a host");
        mailProperties.put(GrapesEmail.MAIL_SMTP_PORT, "a port");
        mailProperties.put(GrapesEmail.MAIL_SMTP_USER, "foo@email.com");
        mailProperties.put(GrapesEmail.MAIL_SMTP_PASSWORD, "***");
        mailProperties.put(GrapesEmail.MAIL_SMTP_SSL_TRUST, "a host");
        mailProperties.put(GrapesEmail.MAIL_SMTP_FROM, "foo@email.com");
        mailProperties.put(GrapesEmail.MAIL_DEBUG, true);

        Throwable e = null;
        GrapesEmail grapesEmail = new GrapesEmail();
        try {
            grapesEmail.setup(mailProperties);
        } catch (Throwable ex) {
            e = ex;
        }
        Properties defaultProps = grapesEmail.getSmtpProperties();

        // Checking if all default values are existing
        assertTrue(defaultProps.containsKey(GrapesEmail.MAIL_SMTP_STARTTLS_ENABLE));
        assertTrue(defaultProps.containsKey(GrapesEmail.MAIL_SMTP_AUTH));
        assertTrue(defaultProps.containsKey(GrapesEmail.MAIL_SMTP_PORT));
        assertTrue(defaultProps.containsKey(GrapesEmail.MAIL_SMTP_SSL_TRUST));

        // No exception should be thrown as user values are provided
        assertNull(e);

    }

    @Test
    public void invalidPropertiesTest() {
        Properties mailProperties = new Properties();
        mailProperties.put(GrapesEmail.MAIL_SMTP_HOST, "a host");
        mailProperties.put(GrapesEmail.MAIL_SMTP_PORT, "a port");
        mailProperties.put(GrapesEmail.MAIL_SMTP_USER, "foo@email.com");
        mailProperties.put(GrapesEmail.MAIL_SMTP_SSL_TRUST, "a host");
        mailProperties.put(GrapesEmail.MAIL_SMTP_FROM, "foo@email.com");
        mailProperties.put(GrapesEmail.MAIL_DEBUG, true);

        GrapesEmail grapesEmail = new GrapesEmail();
        assertFalse(grapesEmail.setup(mailProperties));
    }

    @Test
    public void testConfiguredValueOverridesDefault() {
        Properties mailProperties = new Properties();
        mailProperties.put(GrapesEmail.MAIL_SMTP_HOST, "a host");
        mailProperties.put(GrapesEmail.MAIL_SMTP_PORT, "8025");
        mailProperties.put(GrapesEmail.MAIL_SMTP_USER, "foo@email.com");
        mailProperties.put(GrapesEmail.MAIL_SMTP_PASSWORD, "***");
        mailProperties.put(GrapesEmail.MAIL_SMTP_SSL_TRUST, "a host");
        mailProperties.put(GrapesEmail.MAIL_SMTP_FROM, "foo@email.com");
        mailProperties.put(GrapesEmail.MAIL_DEBUG, true);

        GrapesEmail grapesEmail = new GrapesEmail();
        assertTrue(grapesEmail.setup(mailProperties));

        Properties properties = grapesEmail.getProperties();

        assertNotNull(properties);
        assertEquals("8025", properties.get(GrapesEmail.MAIL_SMTP_PORT));
    }
}
