package org.axway.grapes.server.reports;

public interface TriConsumer<T> {
    void accept(final T a, final T b, final T c);
}
