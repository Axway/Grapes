package org.axway.grapes.utils.client;

/**
 * Grapes Client Exception
 * 
 * <p>Is thrown when a problem of communication with the Grapes server occurs.</p>
 * 
 * @author jdcoffre
 */
public class GrapesCommunicationException extends Exception {

	private static final long serialVersionUID = 7318934257349612137L;

	private int status;

	public GrapesCommunicationException(final int status) {
        super();
		this.status = status;
	}
	
	public int getHttpStatus(){
		return status;
	}
	
}
