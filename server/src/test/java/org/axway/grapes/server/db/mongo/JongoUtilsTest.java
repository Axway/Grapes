package org.axway.grapes.server.db.mongo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JongoUtilsTest {
	
	@Test
	public void generateJongoQuery(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("key1", "value1");
		params.put("key2", "value2");

		assertEquals("{}", JongoUtils.generateQuery(new HashMap<String, Object>()));
		assertEquals("{key1: 'value1'}", JongoUtils.generateQuery("key1", "value1"));
		assertEquals("{key1: 1}", JongoUtils.generateQuery("key1", 1));
		assertEquals("{key1: true}", JongoUtils.generateQuery("key1", true));
		assertEquals("{key2: 'value2', key1: 'value1'}", JongoUtils.generateQuery(params));
	}
	

}