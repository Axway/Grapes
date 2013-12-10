package org.axway.grapes.server.core.version;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {

	@Test
	public void isSnapshotVersion() throws NotHandledVersionException {
		Version version1 = new Version("1.0.0-SNAPSHOT");
		assertTrue(version1.isSnapshot());
		
		Version version2 = new Version("1.0.0-1-SNAPSHOT");
		assertTrue(version2.isSnapshot());
		
		Version version3 = new Version("1");
		assertFalse(version3.isSnapshot());
		
		Version version4 = new Version("1.0");
		assertFalse(version4.isSnapshot());
		
		Version version5 = new Version("1.0.0");
		assertFalse(version5.isSnapshot());
		
		Version version6 = new Version("1.0.0-1");
		assertFalse(version6.isSnapshot());
		
		Version version7 = new Version("1.0.0-1-1");
		assertFalse(version7.isSnapshot());
	}

	@Test
	public void isReleaseVersion() throws NotHandledVersionException {
		Version version1 = new Version("1.0.0-SNAPSHOT");
		assertFalse(version1.isRelease());
		
		Version version2 = new Version("1.0.0-1-SNAPSHOT");
		assertFalse(version2.isRelease());
		
		Version version3 = new Version("1");
		assertTrue(version3.isRelease());
		
		Version version4 = new Version("1.0");
		assertTrue(version4.isRelease());
		
		Version version5 = new Version("1.0.0");
		assertTrue(version5.isRelease());
		
		Version version6 = new Version("1.0.0-1");
		assertTrue(version6.isRelease());
		
		Version version7 = new Version("1.0.0-1-1");
		assertTrue(version7.isRelease());
	}

	@Test
	public void isBranchVersion() throws NotHandledVersionException {
		Version version1 = new Version("1.0.0-SNAPSHOT");
		assertFalse(version1.isBranch());
		
		Version version2 = new Version("1.0.0-1-SNAPSHOT");
		assertTrue(version2.isBranch());
		
		Version version3 = new Version("1");
		assertFalse(version3.isBranch());
		
		Version version4 = new Version("1.0");
		assertFalse(version4.isBranch());
		
		Version version5 = new Version("1.0.0");
		assertFalse(version5.isBranch());
		
		Version version6 = new Version("1.0.0-1");
		assertFalse(version6.isBranch());
		
		Version version7 = new Version("1.0.0-1-1");
		assertTrue(version7.isBranch());
	}

	@Test
	public void cannotCompareVersions() throws NotHandledVersionException {
		Version version1 = new Version("1.0.0");
		Version version3 = new Version("1.0.0-1-1");
		
		IncomparableException exception = null;
		
		try {
			version1.compare(version3);
		} catch (IncomparableException e) {
			exception = e;
		}
		
		assertNotNull(exception);
	}
	
	@Test
	public void compareSimpleVersions() throws IncomparableException, NotHandledVersionException{
		Version version1 = new Version("1.0.0-SNAPSHOT");
		Version version1bis = new Version("1.0.0-SNAPSHOT");
		Version version2 = new Version("1.0.22-SNAPSHOT");
		Version version3 = new Version("1.12.0-SNAPSHOT");
		Version version4 = new Version("2.0.0-SNAPSHOT");
		Version version5 = new Version("0.12.1-1");
		Version version6 = new Version("1.0.0-1");
		Version version7 = new Version("1.0.20-1");
		Version version8 = new Version("1.5.2-1");
		Version version9 = new Version("3.0.0-1");
		Version version10 = new Version("1.0.0-2");

		assertEquals(0, version1.compare(version1bis));
		assertEquals(-1, version1.compare(version2));
		assertEquals(-1, version1.compare(version3));
		assertEquals(-1, version1.compare(version4));
		assertEquals(1, version1.compare(version5));
		assertEquals(1, version1.compare(version6));
		assertEquals(-1, version1.compare(version7));
		assertEquals(-1, version1.compare(version8));
		assertEquals(-1, version1.compare(version9));

		assertEquals(1, version2.compare(version1));
		assertEquals(-1, version2.compare(version3));
		assertEquals(-1, version2.compare(version4));
		assertEquals(1, version2.compare(version5));
		assertEquals(1, version2.compare(version6));
		assertEquals(1, version2.compare(version7));
		assertEquals(-1, version2.compare(version8));
		assertEquals(-1, version2.compare(version9));

		assertEquals(1, version3.compare(version1));
		assertEquals(1, version3.compare(version2));
		assertEquals(-1, version3.compare(version4));
		assertEquals(1, version3.compare(version5));
		assertEquals(1, version3.compare(version6));
		assertEquals(1, version3.compare(version7));
		assertEquals(1, version3.compare(version8));
		assertEquals(-1, version3.compare(version9));

		assertEquals(1, version4.compare(version1));
		assertEquals(1, version4.compare(version2));
		assertEquals(1, version4.compare(version3));
		assertEquals(1, version4.compare(version5));
		assertEquals(1, version4.compare(version6));
		assertEquals(1, version4.compare(version7));
		assertEquals(1, version4.compare(version8));
		assertEquals(-1, version4.compare(version9));

		assertEquals(-1, version5.compare(version1));
		assertEquals(-1, version5.compare(version2));
		assertEquals(-1, version5.compare(version3));
		assertEquals(-1, version5.compare(version4));
		assertEquals(-1, version5.compare(version6));
		assertEquals(-1, version5.compare(version7));
		assertEquals(-1, version5.compare(version8));
		assertEquals(-1, version5.compare(version9));

		assertEquals(-1, version6.compare(version1));
		assertEquals(-1, version6.compare(version10));
		assertEquals(1, version10.compare(version6));
		
	}
	
	@Test
	public void compareBranchVersions() throws IncomparableException, NotHandledVersionException{
		Version version1 = new Version("1.0.0-1-SNAPSHOT");
		Version version1bis = new Version("1.0.0-1-SNAPSHOT");
		Version version2 = new Version("1.0.0-1-4");
		Version version3 = new Version("1.0.0-2-SNAPSHOT");
		Version version4 = new Version("1.0.0-3-1");

		assertEquals(0, version1.compare(version1bis));
		assertEquals(1, version1.compare(version2));
		assertEquals(-1, version1.compare(version3));
		assertEquals(-1, version1.compare(version4));

		assertEquals(-1, version2.compare(version1));
		assertEquals(-1, version2.compare(version3));
		assertEquals(-1, version2.compare(version4));

		assertEquals(1, version3.compare(version1));
		assertEquals(1, version3.compare(version2));
		assertEquals(-1, version3.compare(version4));

		assertEquals(1, version4.compare(version1));
		assertEquals(1, version4.compare(version2));
		assertEquals(1, version4.compare(version3));	
	}
	
	@Test
	public void versionsNotHandled(){
		NotHandledVersionException exception = null;
	
		try {
			new Version("1.0.0-0-0-0");
		} catch (NotHandledVersionException e) {
			exception = e;
		}
		
		assertNotNull(exception);
		exception = null;
	
		try {
			new Version("1.bla.0-1");
		} catch (NotHandledVersionException e) {
			exception = e;
		}
		
		assertNotNull(exception);
		exception = null;
	
		try {
			new Version("1.0.0-bla");
		} catch (NotHandledVersionException e) {
			exception = e;
		}
		
		assertNotNull(exception);
		exception = null;
	
		try {
			new Version("1.0.0-SNAPSHOT-bla");
		} catch (NotHandledVersionException e) {
			exception = e;
		}
		
		assertNotNull(exception);
		exception = null;
	
		try {
			new Version("1.0.0-1-bla");
		} catch (NotHandledVersionException e) {
			exception = e;
		}
		
		assertNotNull(exception);
	}
	
	@Test
	public void compareExoticVersions() throws NotHandledVersionException, IncomparableException{
		Version version1 = new Version("3.8.1");
		Version version2 = new Version("4");
		Version version3 = new Version("4.11");

		assertEquals(-1, version1.compare(version2));
		assertEquals(1, version2.compare(version1));
		assertEquals(-1, version2.compare(version3));
	}

}
