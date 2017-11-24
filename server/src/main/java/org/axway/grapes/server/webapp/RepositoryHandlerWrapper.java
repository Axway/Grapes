package org.axway.grapes.server.webapp;

import org.axway.grapes.server.db.RepositoryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class for generating on the fly RepositoryHandler wrappers that only
 * override some of the methods with convenient returning objects.
 */
public class RepositoryHandlerWrapper implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryHandlerWrapper.class);

    private RepositoryHandler delegate;
    private Map<String, Object> overrides;
    private Map<String, Method> methods = new HashMap<>();


    RepositoryHandlerWrapper(RepositoryHandler delegate, Map<String, Object> overrides) {
        this.delegate = delegate;
        this.overrides = overrides;

        Arrays.stream(delegate.getClass().getDeclaredMethods())
                .forEach(m -> methods.put(m.getName(), m));
        if(LOG.isDebugEnabled()) {
            LOG.debug("Loaded method names: " + methods.keySet());
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if(overrides.containsKey(method.getName())) {
            return overrides.get(method.getName());
        }

        if(methods.containsKey(method.getName())) {
            return methods.get(method.getName()).invoke(delegate, args);
        } else {
            if(LOG.isWarnEnabled()) {
                LOG.warn(String.format("Cannot find the real method on target: %s", method.getName()));
            }
        }

        return "";
    }
}
