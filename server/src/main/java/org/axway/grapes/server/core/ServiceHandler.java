package org.axway.grapes.server.core;


import org.axway.grapes.server.config.GrapesEmailConfig;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.core.services.ErrorMessages;
import org.axway.grapes.server.core.services.GrapesEmail;

public class ServiceHandler {
	private ErrorMessages errorMessages;
	private GrapesEmail grapesEmail;
	private GrapesServerConfig grapesConfig;
	
	public ServiceHandler(final GrapesServerConfig grapesConfig){
		this.grapesConfig = grapesConfig;
	}
	
	public void loadErrorMessages(){
		errorMessages = new ErrorMessages(grapesConfig.getMessageFile());
	}

	public String getErrorMessage(final String key) {
		if(errorMessages == null){
			return ErrorMessages.DEFAULT_ERROR_MESSAGE;
		}		
		return errorMessages.get(key);
	}

	public ErrorMessages getMessageHandler() {
		return errorMessages;
	}

	public void setMessageHandler(ErrorMessages errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public GrapesEmail getGrapesEmail() {
		return grapesEmail;
	}

	public void setGrapesEmail(GrapesEmail grapesEmail) {
		this.grapesEmail = grapesEmail;
	}

	
	public void startGrapesEmailService(){
		grapesEmail = new GrapesEmail();
		GrapesEmailConfig emailConfig = grapesConfig.getGrapesEmailConfig();
		if(emailConfig == null){
			return;
		}	
		grapesEmail.setup(emailConfig.getProperties());
	}
	
	public boolean isEmailServiceRunning(){
		if(grapesEmail == null){
			return false;
		}
		
		if(grapesEmail.isSetup()){
			return true;
		}
		
		return false;
	}
	
	public String sendEmail(final String[] recipients, final String[] ccRecipients, final String subject, final String message){
		if(recipients.length == 0 || subject.isEmpty() || message.isEmpty()){
			return "Skipping sending email either recipients, subject, or message was not provided";
		}
		
		if(!isEmailServiceRunning()){
			return "Skipping sending email as there was some error in running Grape email service";
		}		
		
		if(grapesEmail.send(recipients, ccRecipients, subject, message, "text/html")){
			
			StringBuilder successMessage = new StringBuilder();
			successMessage.append("Successfully sent a notification Email to : ");
			for(String recipient : recipients){
				successMessage.append(recipient);
				successMessage.append(" ");
			}
			
			return successMessage.toString();			
		}

		return "There was an error in sending email";
	}
	
}
