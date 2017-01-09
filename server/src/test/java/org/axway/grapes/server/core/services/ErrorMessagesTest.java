package org.axway.grapes.server.core.services;

import static org.junit.Assert.*;

import java.io.File;

import org.axway.grapes.server.GrapesTestUtils;
import org.junit.Test;

public class ErrorMessagesTest {

	@Test
    public void getMessageTest(){
        final String templatePath = GrapesTestUtils.class.getResource("message.txt").getPath();
        final File messageFile = new File(templatePath);
        
        ErrorMessages messageHandler = new ErrorMessages(messageFile);
        
        assertEquals("Hello Grapes", messageHandler.get("message1"));
    }
	
	@Test
    public void getDefaultMessageTest(){
        final String templatePath = GrapesTestUtils.class.getResource("message.txt").getPath();
        final File messageFile = new File(templatePath);

        ErrorMessages messageHandler = new ErrorMessages(messageFile);

        assertEquals("Some Error Occured", messageHandler.get("sample"));
    }
}

