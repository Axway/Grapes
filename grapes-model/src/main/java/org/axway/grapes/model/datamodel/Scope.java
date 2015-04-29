package org.axway.grapes.model.datamodel;

/**
 * Scope Model Enum
 *
 * <P> Model Objects are used in the communication with the Grapes server. These objects are serialized/un-serialized in JSON objects to be exchanged via http REST calls.
 *
 * <p>Defines all the available dependency scopes that could be used</p>
 */
public enum Scope { COMPILE,PROVIDED,RUNTIME,TEST,SYSTEM,IMPORT }
