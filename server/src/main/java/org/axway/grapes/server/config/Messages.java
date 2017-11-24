package org.axway.grapes.server.config;

import org.axway.grapes.server.core.services.email.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

    /**
     * Loads the file content in the properties collection
     * @param filePath The path of the file to be loaded
     */
    private static void loadFile(String filePath) {
        final Path path = FileSystems.getDefault().getPath(filePath);

        try {
            data.clear();
            data.load(Files.newBufferedReader(path));
        } catch(IOException e) {
            LOG.warn("Exception while loading " + path.toString(), e);
        }
    }
}
