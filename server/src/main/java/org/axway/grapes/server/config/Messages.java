package org.axway.grapes.server.config;

import org.axway.grapes.server.core.services.email.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Utility class to retrieve properties in the configuration files.
 */
public final class Messages {

    private static final Logger LOG = LoggerFactory.getLogger(Messages.class);
    private static Properties data = new Properties();

    private Messages() {

    }

    public static void init(String filePath) {
        loadFile(filePath);
    }

    /**
     * Retrieves the configured message by property key
     * @param key The key in the file
     * @return The associated value in case the key is found in the message bundle file. If
     * no such key is defined, the returned value would be the key itself.
     */
    public static String get(MessageKey key) {
        return data.getProperty(key.toString(), key.toString());
    }

    private static void loadFile(String filePath) {
        final File f = new File(filePath);
        FileInputStream stream = null;

        try {
            stream = new FileInputStream(f);
            data.clear();
            data.load(stream);
        } catch(FileNotFoundException e) {
            LOG.warn("File not found " + f.getAbsolutePath(), e);
        } catch(IOException e1) {
            LOG.warn("Exception while loading " + f.getAbsolutePath(), e1);
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (IOException ioExc) {
                LOG.warn("Exception while closing message bundle stream", ioExc);
            }
        }
    }
}
