package org.axway.grapes.server.core.version;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class MiscTest {

    @Test
    public void toStringTest() {
        final String v = "1.4.5-1-2";
        final Optional<Version> version = Version.make(v);

        assertTrue(version.isPresent());
        assertTrue(version.get().toString().equals(v));
    }
}
