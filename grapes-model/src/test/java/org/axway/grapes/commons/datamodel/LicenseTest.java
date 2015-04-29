package org.axway.grapes.commons.datamodel;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class LicenseTest {
	
	@Test
	public void checkEquals(){
		License license = DataModelFactory.createLicense("name", "longName", "comments", "regexp", "url");
		License license2 = DataModelFactory.createLicense("name", "longName", "comments", "regexp", "url");
		License license3 = DataModelFactory.createLicense("name", "longName", "comments", "regexp", "url&");
		License license4 = DataModelFactory.createLicense("name", "longName", "comments", "regexp&", "url");
		License license5 = DataModelFactory.createLicense("name", "longName", "comments&", "regexp", "url");
		License license6 = DataModelFactory.createLicense("name", "longName&", "comments", "regexp", "url");
		License license7 = DataModelFactory.createLicense("name&", "longName", "comments", "regexp", "url");

		assertTrue(license.equals(license2));
		assertFalse(license.equals(license3));
		assertFalse(license.equals(license4));
		assertFalse(license.equals(license5));
		assertFalse(license.equals(license6));
		assertFalse(license.equals(license7));
		assertFalse(license.equals("test"));
	}

}


