package org.axway.grapes.core.exceptions;

public class GrapesException extends Exception{

    public GrapesException(final String message){
        super(message);
    }

    public GrapesException(final String message, final Exception e){
        super(message, e);
    }
}
