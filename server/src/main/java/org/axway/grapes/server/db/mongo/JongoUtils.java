package org.axway.grapes.server.db.mongo;

import org.axway.grapes.server.db.DBRegExp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Jongo utility class
 * 
 * <p>Gathers all the utility methods to use Jongo.</p>
 * 
 * @author jdcoffre
 */
public final class JongoUtils {

	private JongoUtils() {
		// Hide Utility Class Constructor
	}
	
	/**
	 * Generate a Jongo query regarding a set of parameters.
	 * 
	 * @param params Map<queryKey, queryValue> of query parameters
	 * @return String
	 */
	public static String generateQuery(final Map<String,Object> params){
		final StringBuilder sb = new StringBuilder();
		boolean newEntry = false;
		
		sb.append("{");
		for(final Entry<String,Object> param: params.entrySet()){
			if(newEntry){
				sb.append(", ");
			}

			sb.append(param.getKey());
			sb.append(": ");
			sb.append(getParam(param.getValue()));
			newEntry = true;
		}
		sb.append("}");
		
		return sb.toString();
	}

	/**
	 * Generate a Jongo query with provided the parameter.
	 * 
	 * @param key
	 * @param value
	 * @return String
	 */
	public static String generateQuery(final String key, final Object value) {
		final Map<String, Object> params = new HashMap<>();
		params.put(key, value);
		return generateQuery(params);
	}

	/**
	 * Handle the serialization of String, Integer and boolean parameters.
	 * 
	 * @param param to serialize
	 * @return Object
	 */
	private static Object getParam(final Object param) {
		final StringBuilder sb = new StringBuilder();
		if(param instanceof String){
			sb.append("'");
			sb.append((String)param);
			sb.append("'");
		}
		else if(param instanceof Boolean){
			sb.append(String.valueOf((Boolean)param));			
		}
        else if(param instanceof Integer){
            sb.append(String.valueOf((Integer)param));
        }
        else if(param instanceof DBRegExp){
            sb.append('/');
            sb.append(((DBRegExp) param).toString());
            sb.append('/');
        }
		
		return sb.toString();
	}
	
}