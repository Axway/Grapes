package org.axway.grapes.server.core.services;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ErrorMessages {

private File messageFile;
	
    private Properties allMessages;
    private static final Logger LOG = LoggerFactory.getLogger(ErrorMessages.class);


	public static final String DEFAULT_ERROR_MESSAGE = "Some Error Occurred";

	public ErrorMessages(File messageFile){
		this.messageFile = messageFile;
		loadMessagesFromFile();
	}
	
	public String get(final String key) {
		if(!allMessages.containsKey(key)) {
			LOG.warn("Could not find message for [" + key + "]. Returning default message.");
		}

		return allMessages.getProperty(key, DEFAULT_ERROR_MESSAGE);
	}
	
	private void loadMessagesFromFile(){
		allMessages = new Properties();
		try {
			LOG.debug("Loading configuration messages from " + messageFile.getAbsolutePath());
			InputStream input = new FileInputStream(messageFile.getAbsolutePath());

			// load a properties file
			allMessages.load(input);
			input.close();
			LOG.debug("Message bundle contains " + allMessages.size() + " elements");
			LOG.debug("Available keys: " + allMessages.keySet().toString());
		} catch (IOException e) {
			LOG.error("Message file is not loaded properly. For all errors, service would return only default messages", e);
			e.printStackTrace();
		}	
	}
}

