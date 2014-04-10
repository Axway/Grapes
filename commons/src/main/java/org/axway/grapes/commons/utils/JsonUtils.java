package org.axway.grapes.commons.utils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.axway.grapes.commons.datamodel.Artifact;
import org.axway.grapes.commons.datamodel.License;
import org.axway.grapes.commons.datamodel.Module;

import java.io.IOException;

/**
 * Json Utils
 * 
 * <P> Utility class that ease Json serialization/un-serialization
 * 
 * @author jdcoffre
 */
public final class JsonUtils {
	
	// Utility class, though no constructor
	private JsonUtils() {}

	/**
	 * Json content type id
	 */
	public static final String JSON_CONTENT_TYPE = "application/json";

	/**
	 * Serialize an object with Json
	 * @param obj Object
	 * @return String
	 * @throws IOException 
	 */
	public static String serialize(final Object obj) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
		return mapper.writeValueAsString(obj);
		
	}
	
	/**
	 * Un-serialize a Json into Module
	 * @param module String
	 * @return Module
	 * @throws IOException 
	 */
	public static Module unserializeModule(final String module) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
		return mapper.readValue(module, Module.class);
	}

	/**
	 * Un-serialize a report with Json
	 * @param artifact String
	 * @return Artifact
	 * @throws IOException 
	 */
	public static Artifact unserializeArtifact(final String artifact) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
		return mapper.readValue(artifact, Artifact.class);
	}

	/**
	 * Un-serialize a report with Json
	 * @param license String
	 * @return License
	 * @throws IOException 
	 */
	public static License unserializeLicense(final String license) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
		return mapper.readValue(license, License.class);
	}
	
}
