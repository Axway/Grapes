package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.ModuleView;

import java.io.IOException;

public class ModuleSerializer extends JsonSerializer<ModuleView> {

    @Override
    public void serialize(final ModuleView moduleView, final JsonGenerator json,	final SerializerProvider serializer) throws IOException {
        json.writeObject(moduleView.getModule());
        json.flush();
    }

}
