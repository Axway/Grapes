package org.axway.grapes.server.core.version;

import org.junit.Test;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class InvalidVersionsTest {

	@Test
	public void versionsNotHandled() {
	    final String[] invalidVersions = new String[] {
	            null,
                "1.0.0-0-0-0",
                "1.bla.0-1",
                "1.0.0-bla",
                "1.0.0-SNAPSHOT-bla",
                "1.0.0-1-bla",
                "2.3.1.3-w-4.3"
        };

	    for(final String versionStr : invalidVersions) {
            Optional<Version> v = Version.make(versionStr);
            assertFalse("Invalid version string " + v, v.isPresent());
        }
	}
}
