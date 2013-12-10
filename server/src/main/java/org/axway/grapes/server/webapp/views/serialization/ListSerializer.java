package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.ListView;

import java.io.IOException;

/**
 * List Serializer
 * 
 * <p>Handle the Json serialization of list views.</p>
 * 
 * @author jdcoffre
 */
public class ListSerializer extends JsonSerializer<ListView> {

	@Override
	public void serialize(final ListView listView, final JsonGenerator json, final SerializerProvider serializer) throws IOException {
		json.writeObject(listView.getItems());
		json.flush();
		
	}

}
