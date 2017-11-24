package org.axway.grapes.server.db.mongo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class JongoUtilsTest {
	 	
	 			
	@Test
	public void generateJongoQuery(){
		Map<String, Object> params = new HashMap<String, Object>();				
		params.put("key2", "value2");
		params.put("key1", "value1");
		
		//TreeMap sorts params by key. Sorted params(i.e actual result)
		//is then compared with the sorted hard coded String (i.e expected result) 
		Map<String, Object> sortParams = new TreeMap<String, Object>(params); 
		
		assertEquals("{}", JongoUtils.generateQuery(new HashMap<String, Object>()));
		assertEquals("{key1: 'value1'}", JongoUtils.generateQuery("key1", "value1"));
		assertEquals("{key1: 1}", JongoUtils.generateQuery("key1", 1));
		assertEquals("{key1: true}", JongoUtils.generateQuery("key1", true));
		assertEquals("{key1: 'value1', key2: 'value2'}",JongoUtils.generateQuery(sortParams));
		
	}
}