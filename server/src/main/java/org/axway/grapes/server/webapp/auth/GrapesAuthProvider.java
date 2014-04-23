package org.axway.grapes.server.webapp.auth;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilderSpec;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.CachingAuthenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import org.axway.grapes.server.config.GrapesServerConfig;
import org.axway.grapes.server.db.datamodel.DbCredential.AvailableRoles;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;


/**
 * GRAPES Authenticator Provider
 *
 * <p>This provider handle authentication injection with Jax-rs  and provide the list of roles of the authenticated user.
 * It is used as soon as a Jax-rs resource provide a method with @Role List<AvailableRole> expected parameter.</p>
 *
 * useful information here: http://codahale.com/what-makes-jersey-interesting-injection-providers
 *
 * @author jdcoffre
 */
@Provider
public final class GrapesAuthProvider implements InjectableProvider<Role, Type> {

    private static final Logger LOG = LoggerFactory.getLogger(GrapesAuthProvider.class);

    private final CredentialManager credentialManager;
    private final GrapesServerConfig config;

    public GrapesAuthProvider(final GrapesServerConfig config) {
        this.config = config;
        this.credentialManager = new CredentialManager(config);
        CachingAuthenticator.wrap(credentialManager, CacheBuilderSpec.parse(config.getAuthenticationCachePolicy()));
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(final ComponentContext ic, final Role role, final Type type) {
        return new AuthInjectable(credentialManager, config.getAuthenticationCachePolicy());
    }

    private final class AuthInjectable extends AbstractHttpContextInjectable<List<AvailableRoles>> {
        private static final String PREFIX = "Basic";
        private static final String HEADER_NAME = "WWW-Authenticate";
        private static final String HEADER_VALUE = PREFIX + " realm=\"%s\"";

        private final CredentialManager credentialManager;
        private final String authCachePolicy;

        private AuthInjectable(final CredentialManager credentialManager, final String authCachePolicy) {
            this.credentialManager = credentialManager;
            this.authCachePolicy = authCachePolicy;
        }

        @Override
        public List<AvailableRoles> getValue(final HttpContext c) {
            if(config.isInMaintenance()){
                throw new WebApplicationException(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("Grapes is currently in maintenance. This operation is not possible at the moment. Please, try it later.")
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .build());
            }

            final String header = c.getRequest().getHeaderValue(HttpHeaders.AUTHORIZATION);
            try {
                if (header != null) {
                    final int space = header.indexOf(' ');
                    if (space > 0) {
                        final String method = header.substring(0, space);
                        if (PREFIX.equalsIgnoreCase(method)) {
                            final String decoded = B64Code.decode(header.substring(space + 1),
                                    StringUtil.__ISO_8859_1);
                            final int i = decoded.indexOf(':');
                            if (i > 0) {
                                final String username = decoded.substring(0, i);
                                final String password = decoded.substring(i + 1);
                                final BasicCredentials credentials = new BasicCredentials(username,
                                        password);

                                final Optional<List<AvailableRoles>> result = credentialManager.authenticate(credentials);
                                if (result.isPresent()) {
                                    return result.get();
                                }
                            }
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                LOG.debug("Error decoding credentials", e);
            } catch (IllegalArgumentException e) {
                LOG.debug("Error decoding credentials", e);
            } catch (AuthenticationException e) {
                LOG.warn("Error authenticating credentials", e);
                throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
            }

            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .header(HEADER_NAME,
                            String.format(HEADER_VALUE,authCachePolicy))
                    .entity("Credentials are required to access this resource.")
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build());
        }
    }
}
