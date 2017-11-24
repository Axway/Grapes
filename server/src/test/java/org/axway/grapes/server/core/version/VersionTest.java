package org.axway.grapes.server.core.version;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class VersionTest {

	private final String versionString;
	private boolean isSnapshot;
	private boolean isBranch;


	public VersionTest(final String testName,
                       final String input,
                       final boolean isSnapshot,
                       final boolean isBranch) {
		this.versionString = input;
		this.isSnapshot = isSnapshot;
		this.isBranch = isBranch;
	}

	@Parameterized.Parameters(name="{index}: {0}")
	public static Collection<Object[]> testCases() {
		return Arrays.asList(new Object[][]{
				{ "Snapshot on master", "1.0.0-SNAPSHOT", true, false},
				{ "Snapshot on branch", "1.0.0-1-SNAPSHOT", true, true},
				{ "Single digit - not snapshot", "1", false, false},
				{ "Two digits - not snapshot", "1.0", false, false},
				{ "Three digits - not snapshot", "1.0.0", false, false},
				{ "One dash - not snapshot", "1.0.0-1", false, false},
				{ "Two dashes - not snapshot", "1.0.0-1-1", false, true}
		});
	}

	@Test
	public void test() {
		Optional<Version> versionOp = Version.make(versionString);
		assertTrue(versionOp.isPresent());
		final Version version = versionOp.get();

		assertEquals(isSnapshot, version.isSnapshot());
		assertEquals(!isSnapshot, version.isRelease());
		assertEquals(isBranch, version.isBranch());
	}
}