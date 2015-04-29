package org.axway.grapes.model.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.License;
import org.axway.grapes.model.datamodel.Module;
import org.axway.grapes.model.datamodel.Organization;

import java.io.IOException;
import java.util.Map;

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
	 * @throws java.io.IOException
	 */
	public static String serialize(final Object obj) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
		return mapper.writeValueAsString(obj);

	}

    /**
     * Un-serialize a Json into Organization
     * @param organization String
     * @return Organization
     * @throws java.io.IOException
     */
    public static Organization unserializeOrganization(final String organization) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
        return mapper.readValue(organization, Organization.class);
    }

    /**
     * Un-serialize a Json into Module
     * @param module String
     * @return Module
     * @throws java.io.IOException
     */
    public static Module unserializeModule(final String module) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
        return mapper.readValue(module, Module.class);
    }

    /**
     * Un-serialize a Json into BuildInfo
     * @param buildInfo String
     * @return Map<String,String>
     * @throws java.io.IOException
     */
    public static Map<String,String> unserializeBuildInfo(final String buildInfo) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
        return mapper.readValue(buildInfo,   new TypeReference<Map<String, Object>>(){});
    }

	/**
	 * Un-serialize a report with Json
	 * @param artifact String
	 * @return Artifact
	 * @throws java.io.IOException
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
	 * @throws java.io.IOException
	 */
	public static License unserializeLicense(final String license) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
		return mapper.readValue(license, License.class);
	}
	
}
