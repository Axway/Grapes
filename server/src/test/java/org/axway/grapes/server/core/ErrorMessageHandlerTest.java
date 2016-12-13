package org.axway.grapes.server.core;

import static org.junit.Assert.*;

import java.io.File;

import org.axway.grapes.server.GrapesTestUtils;
import org.junit.Test;

public class ErrorMessageHandlerTest {

	@Test
    public void getMessageTest(){
        final String templatePath = GrapesTestUtils.class.getResource("message.txt").getPath();
        final File messageFile = new File(templatePath);
        
        ErrorMessageHandler messageHandler = new ErrorMessageHandler(messageFile);
        
        assertEquals("Hello Grapes", messageHandler.getMessage("message1"));
    }
	
	@Test
    public void getDefaultMessageTest(){
        final String templatePath = GrapesTestUtils.class.getResource("message.txt").getPath();
        final File messageFile = new File(templatePath);

        ErrorMessageHandler messageHandler = new ErrorMessageHandler(messageFile);

        assertEquals("Error Message", messageHandler.getMessage("sample"));
    }
}
