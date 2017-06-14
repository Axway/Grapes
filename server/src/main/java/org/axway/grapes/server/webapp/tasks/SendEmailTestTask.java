package org.axway.grapes.server.webapp.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import org.axway.grapes.server.config.GrapesEmailConfig;
import org.axway.grapes.server.core.services.email.GrapesEmailSender;

import java.io.PrintWriter;
import java.util.Properties;

/**
 * A task for validating the sending of an email
 */
public class SendEmailTestTask extends Task {

    private GrapesEmailConfig emailConfig;

    public SendEmailTestTask(final GrapesEmailConfig cfg) {
        super("testEmail");
        this.emailConfig = cfg;
    }

    @Override
    public void execute(final ImmutableMultimap<String, String> immutableMultimap,
                        final PrintWriter printWriter) throws Exception {

        final Properties properties = new Properties();
        properties.putAll(emailConfig.getProperties());
        properties.putAll(toProperties(immutableMultimap));

        GrapesEmailSender sender = new GrapesEmailSender(properties);

        try {
            sender.send(new String[]{properties.getProperty("to")},
                    properties.getProperty("subject"),
                    properties.getProperty("message"),
                    properties.getProperty("contentType"));

            printWriter.println("Email test performed...");

        } catch(Exception e) {
            printWriter.println("Exception while sending email. " + e.getMessage());
        }
    }

    private Properties toProperties(final ImmutableMultimap<String, String> map) {
        Properties p = new Properties();
        map.keySet().forEach(key -> p.put(key, map.get(key).asList().get(0)) );
        return p;
    }
}
