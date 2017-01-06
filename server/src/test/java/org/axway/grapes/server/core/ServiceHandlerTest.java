package org.axway.grapes.server.core;

import static org.junit.Assert.*;

import java.io.File;

import org.axway.grapes.server.GrapesTestUtils;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.services.GrapesEmail;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceHandlerTest {

	@Test
	public void errorMessageHandlingTest() {
		final String templatePath = GrapesTestUtils.class.getResource("message.txt").getPath();
		
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		when(grapesConfig.getMessageFile()).thenReturn(new File(templatePath));
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		
		// loading
		serviceHandler.loadErrorMessages();
		
		assertNotNull(serviceHandler.getMessageHandler());
		assertEquals("Hello Grapes", serviceHandler.getErrorMessage("message1"));
		assertEquals("Hello Grapes 2", serviceHandler.getErrorMessage("message2"));
		assertEquals("Some Error Occured", serviceHandler.getErrorMessage("invalidKey"));
		
		// test defaults
		assertEquals("default message", serviceHandler.getErrorMessage("invalidKey", "default message"));
	}
	
	@Test
	public void grapesEmailHandlingSetupTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);
		
		when(grapesEmail.isSetup()).thenReturn(true);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);
		
		assertTrue(serviceHandler.isEmailServiceRunning());		
	}	
	
	@Test
	public void grapesEmailHandlingSendTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);

		when(grapesEmail.isSetup()).thenReturn(true);
		when(grapesEmail.send(any(String[].class), any(String[].class), any(String.class), any(String.class), any(String.class))).thenReturn(true);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);

		String[] toMail = { "text@axway.com" };    
		String[] ccMail = { };
		
		StringBuilder successMessage = new StringBuilder();
		successMessage.append("Successfully sent a notification Email to : ");
		for(String recipient : toMail){
			successMessage.append(recipient);
			successMessage.append(" ");
		}
		
		assertTrue(serviceHandler.isEmailServiceRunning());		
		assertEquals(successMessage.toString(), serviceHandler.sendEmail(toMail, ccMail, "subject", "message"));		
	}
	
	@Test
	public void emailSendTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);

		when(grapesEmail.isSetup()).thenReturn(true);
		when(grapesEmail.send(any(String[].class), any(String[].class), any(String.class), any(String.class), any(String.class))).thenReturn(true);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);

		String[] toMail = { "text@axway.com" };    
		String[] ccMail = { };
		
		StringBuilder successMessage = new StringBuilder();
		successMessage.append("Successfully sent a notification Email to : ");
		for(String recipient : toMail){
			successMessage.append(recipient);
			successMessage.append(" ");
		}
		
		assertTrue(serviceHandler.isEmailServiceRunning());		
		assertEquals(successMessage.toString(), serviceHandler.sendEmail(toMail, ccMail, "subject", "message"));		
	}
	
	@Test
	public void grapesEmailHandlingNullEmailConfigTest() {
		
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		
		// starting service
		serviceHandler.startGrapesEmailService();
		
		assertFalse(serviceHandler.isEmailServiceRunning());	
	}
	
	@Test
	public void emailSendServiceStoppedTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);

		when(grapesEmail.isSetup()).thenReturn(false);
		when(grapesEmail.send(any(String[].class), any(String[].class), any(String.class), any(String.class), any(String.class))).thenReturn(true);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);

		String[] toMail = { "text@axway.com" };   
		String[] ccMail = { };
		
		assertFalse(serviceHandler.isEmailServiceRunning());		
		assertEquals("Skipping sending email as there was some error in running Grape email service", serviceHandler.sendEmail(toMail, ccMail, "subject", "message"));		
	}	
	
	@Test
	public void emailSendEmptyRecipientsTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);

		when(grapesEmail.isSetup()).thenReturn(true);
		when(grapesEmail.send(any(String[].class), any(String[].class), any(String.class), any(String.class), any(String.class))).thenReturn(true);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);

		String[] toMail = { };    
		String[] ccMail = { };
		
		assertTrue(serviceHandler.isEmailServiceRunning());		
		assertEquals("Skipping sending email either recipients, subject, or message was not provided", serviceHandler.sendEmail(toMail, ccMail, "subject", "message"));		
	}	
	
	@Test
	public void emailSendEmptySubjectTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);

		when(grapesEmail.isSetup()).thenReturn(true);
		when(grapesEmail.send(any(String[].class), any(String[].class), any(String.class), any(String.class), any(String.class))).thenReturn(true);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);

		String[] toMail = { "test@axway.com" };    
		String[] ccMail = { };
		
		assertTrue(serviceHandler.isEmailServiceRunning());		
		assertEquals("Skipping sending email either recipients, subject, or message was not provided", serviceHandler.sendEmail(toMail, ccMail, "", "message"));		
	}
	
	@Test
	public void emailSendFailureTest() {
		GrapesServerConfig grapesConfig = mock(GrapesServerConfig.class);
		GrapesEmail grapesEmail = mock(GrapesEmail.class);

		when(grapesEmail.isSetup()).thenReturn(true);
		when(grapesEmail.send(any(String[].class), any(String[].class), any(String.class), any(String.class), any(String.class))).thenReturn(false);
		
		ServiceHandler serviceHandler = new ServiceHandler(grapesConfig);
		serviceHandler.setGrapesEmail(grapesEmail);

		String[] toMail = { "test@axway.com" };    
		String[] ccMail = { };
		
		assertTrue(serviceHandler.isEmailServiceRunning());	
		assertEquals("There was an error in sending email", serviceHandler.sendEmail(toMail, ccMail, "subject", "message"));		
	}


}
