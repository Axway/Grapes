package org.axway.grapes.server.core;


import org.axway.grapes.server.config.GrapesEmailConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.services.ErrorMessages;
import org.axway.grapes.server.core.services.email.GrapesEmailSender;

public class ServiceHandler {
//	private ErrorMessages errorMessages;
//	private GrapesEmailSender grapesEmailSender;
	private GrapesServerConfig grapesConfig;
	
	public ServiceHandler(final GrapesServerConfig grapesConfig){
		this.grapesConfig = grapesConfig;
	}
	
//	public void loadErrorMessages(){
//		errorMessages = new ErrorMessages(grapesConfig.getMessageFile());
//	}

//	public String getErrorMessage(final String key) {
//		return getErrorMessage(key, "");
//	}

//	public String getErrorMessage(final String key, final String defaultMessage) {
//		final String error = errorMessages.get(key);
//
//		// if no key were found
//		if(error.equals(ErrorMessages.DEFAULT_ERROR_MESSAGE)){
//			return defaultMessage.isEmpty() ? ErrorMessages.DEFAULT_ERROR_MESSAGE : defaultMessage;
//		}
//
//		return error;
//	}

//	public ErrorMessages getMessageHandler() {
//		return errorMessages;
//	}

//	public void setMessageHandler(ErrorMessages errorMessages) {
//		this.errorMessages = errorMessages;
//	}
	
//	public GrapesEmailSender getGrapesEmail() {
//		return grapesEmailSender;
//	}

//	public void setGrapesEmail(GrapesEmail grapesEmail) {
//		this.grapesEmail = grapesEmail;
//	}

	
//	public void startGrapesEmailService(){
//		grapesEmailSender = new GrapesEmailSender();
//		GrapesEmailConfig emailConfig = grapesConfig.getGrapesEmailConfig();
//		if(emailConfig == null){
//			return;
//		}
//		grapesEmailSender.setup(emailConfig.getProperties());
//	}
//
//	public boolean isEmailServiceRunning(){
//		if(grapesEmailSender == null){
//			return false;
//		}
//
//		if(grapesEmailSender.isSetup()){
//			return true;
//		}
//
//		return false;
//	}
	
//	public String sendEmail(final String[] recipients, final String[] ccRecipients, final String subject, final String message){
//		if(recipients.length == 0 || subject.isEmpty() || message.isEmpty()){
//			return "Skipping sending email either recipients, subject, or message was not provided";
//		}
//
//		if(!isEmailServiceRunning()){
//			return "Skipping sending email as there was some error in running Grape email service";
//		}
//
//		if(grapesEmail.send(recipients, ccRecipients, subject, message, "text/html")){
//
//			StringBuilder successMessage = new StringBuilder();
//			successMessage.append("Successfully sent a notification Email to : ");
//			for(String recipient : recipients){
//				successMessage.append(recipient);
//				successMessage.append(" ");
//			}
//
//			return successMessage.toString();
//		}
//
//		return "There was an error in sending email";
//	}
	
}
