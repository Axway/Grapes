package org.axway.grapes.server.webapp.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Role
 *
 * <p>This annotation is used to inject authenticated user role into protected JAX-RS resource methods.</p>
 *
 * @author jdcoffre
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface Role {

}
