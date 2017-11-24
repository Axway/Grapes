package org.axway.grapes.server.webapp;


import org.axway.grapes.server.db.RepositoryHandler;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RepositoryHandlerBuilder {

    private RepositoryHandler delegate;

    private Map<String, Object> overrides = new HashMap<>();

    public RepositoryHandlerBuilder start(final RepositoryHandler delegate) {
        this.delegate = delegate;
        return this;
    }

    public RepositoryHandlerBuilder replaceGetMethod(String methodName, Object result) {
        this.overrides.put(methodName, result);
        return this;
    }

    public RepositoryHandler build() {
        return (RepositoryHandler)
                Proxy.newProxyInstance(RepositoryHandlerBuilder.class.getClassLoader(),
                        new Class[]{RepositoryHandler.class},
                        new RepositoryHandlerWrapper(delegate, overrides));
    }
}
