package org.axway.grapes.server.core.version;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;


public class ComparisonsTest {

	@Test(expected = IncomparableException.class)
	public void cannotCompareVersions() throws IncomparableException {
		Optional<Version> v1 = Version.make("1.0.0");
        Optional<Version> v2 = Version.make("1.0.0-1-1");

        assertTrue(v1.isPresent());
        assertTrue(v2.isPresent());
		v1.get().compare(v2.get());
	}

	@Test
	public void equalVersionsTestMaster() throws IncomparableException {
        Optional<Version> version1 = Version.make("1.0.0-SNAPSHOT");
        Optional<Version> version1Bis = Version.make("1.0.0-SNAPSHOT");
        assertTrue(version1.isPresent());
        assertTrue(version1Bis.isPresent());
		assertTrue(version1.get().compare(version1Bis.get()) == 0);
	}

	@Test
	public void equalVersionsTestBranch() throws IncomparableException {
        Optional<Version> version1 = Version.make("1.0.0-1-SNAPSHOT");
        Optional<Version> version1Bis = Version.make("1.0.0-1-SNAPSHOT");
        assertTrue(version1.isPresent());
        assertTrue(version1Bis.isPresent());
		assertTrue(version1.get().compare(version1Bis.get()) == 0);
	}


	@Test
	public void compareSimpleVersions() throws IncomparableException {
		final String[] ordered = new String[] {
				"0.12.1-1",
				"1.0.0-1",
				"1.0.0-2",
				"1.0.0-SNAPSHOT",
				"1.0.20-1",
				"1.0.22-SNAPSHOT",
				"1.5.2-1",
				"1.12.0-SNAPSHOT",
				"2.0.0-SNAPSHOT",
				"3.0.0-1"
		};

		assertListIsInOrder(Arrays.asList(ordered));
	}

	@Test
	public void compareBranchVersions() throws IncomparableException {
        final String[] ordered = new String[] {
                "1.0.0-1-4",
                "1.0.0-1-SNAPSHOT",
                "1.0.0-2-SNAPSHOT",
                "1.0.0-3-1"
        };

        assertListIsInOrder(Arrays.asList(ordered));
	}


	@Test
	public void compareExoticVersions() throws IncomparableException{
        final String[] ordered = new String[] {
                "3.8.1",
                "4",
                "4.11"
        };

        assertListIsInOrder(Arrays.asList(ordered));
	}

	private void assertListIsInOrder(final List<String> versionStrings) throws IncomparableException {
        for(int i = 0; i < versionStrings.size(); i++) {
            Optional<Version> v1 = Version.make(versionStrings.get(i));
            assertTrue(v1.isPresent());

            for(int j = i + 1; j < versionStrings.size(); j++) {
                Optional<Version> v2 = Version.make(versionStrings.get(j));
                assertTrue(v2.isPresent());

                assertTrue(String.format("Failure in comparison of %s and %s",
                        v1.get().toString(),
                        v2.get().toString()),
                        v1.get().compare(v2.get()) < 0);
            }
        }
    }
}