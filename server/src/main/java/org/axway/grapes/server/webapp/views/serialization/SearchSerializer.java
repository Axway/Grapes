package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.SearchView;

import java.io.IOException;

public class SearchSerializer extends JsonSerializer<SearchView> {
    @Override
    public void serialize(SearchView search, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeObject(search.getSearchOj());
        jsonGenerator.flush();
    }
}
