package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.ArtifactView;

import java.io.IOException;

public class ArtifactSerializer extends JsonSerializer<ArtifactView> {

    @Override
    public void serialize(final ArtifactView gavcsView, final JsonGenerator json,	final SerializerProvider serializer) throws IOException {
        json.writeObject(gavcsView.getArtifact());
        json.flush();

    }

}
