package org.axway.grapes.server.core;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.axway.grapes.server.core.cache.CacheName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CacheUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CacheUtils.class);

    public void clear(final CacheName cacheName) {
        try {
            final CacheAccess<Object, Object> cache = JCS.getInstance(cacheName.name());
            cache.getCacheControl().removeAll();
        } catch (CacheException e) {
            LOG.warn(String.format("Problem cleaning cache: %s %s", cacheName.name(), e.toString()));
        } catch (IOException e) {
            LOG.warn(String.format("Problem cleaning cache: %s %s", cacheName.name(), e.toString()));
        }
    }

    public <T> CacheAccess<String, T> initCache(final CacheName cacheName, final Class<T> valueClass) {
        try {
            return JCS.getInstance(cacheName.name());
        } catch (CacheException e) {
            LOG.warn(String.format("Problem initializing cache: %s %s", e.getMessage(), e));
        }

        throw new CacheException(String.format("Could not init cache for %s", cacheName.name()));
    }
}
