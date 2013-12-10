package org.axway.grapes.server.core.version;

public class NotHandledVersionException extends Exception {
	private static final long serialVersionUID = 927972186845432670L;
	
	private Exception exception;

	public NotHandledVersionException(final Exception e) {
		this.exception = e;
	}

	public NotHandledVersionException() {
		
	}

	public Exception getException(){
		return exception;
	}
}
