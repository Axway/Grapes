package org.axway.grapes.server.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ErrorMessages {

    private File messageFile;
	
	private Properties allMessages;
	
	public static final String DEFAULT_ERROR_MESSAGE = "Some Error Occured";

	public ErrorMessages(File messageFile){
		this.messageFile = messageFile;
		loadMessagesFromFile();
	}
	
	public String get(final String key){
		if(allMessages == null){
			loadMessagesFromFile();
		}
		return allMessages.getProperty(key, DEFAULT_ERROR_MESSAGE);
	}
	
	private void loadMessagesFromFile(){
		allMessages = new Properties();
		try {		
		InputStream input = new FileInputStream(messageFile.getAbsolutePath());
		// load a properties file
		allMessages.load(input);
		input.close();
		} catch (IOException e) {
			// doing nothing
		}	
	}
}
