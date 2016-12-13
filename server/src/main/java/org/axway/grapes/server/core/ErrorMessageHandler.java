package org.axway.grapes.server.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ErrorMessageHandler {

    private File messageFile;
	
	private Properties allTypeMessage;

	public ErrorMessageHandler(File messageFile){
		this.messageFile = messageFile;
		loadMessagesFromFile();
	}
	
	public String getMessage(final String key){
		if(allTypeMessage == null){
			loadMessagesFromFile();
		}
		return allTypeMessage.getProperty(key, "Error Message");
	}
	
	private void loadMessagesFromFile(){
		allTypeMessage = new Properties();
		try {		
		InputStream input = new FileInputStream(messageFile.getAbsolutePath());
		// load a properties file
		allTypeMessage.load(input);
		input.close();
		} catch (IOException e) {
			// doing nothing
		}	
	}
}
