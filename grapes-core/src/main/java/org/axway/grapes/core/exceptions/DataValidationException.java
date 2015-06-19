package org.axway.grapes.core.exceptions;

/**
 * Created by jennifer on 6/3/15.
 */
public class DataValidationException extends Exception {

    //Parameterless Constructor
    public DataValidationException() {
    }

    //Constructor that accepts a message
    public DataValidationException(String message) {
        super(message);
    }
}
