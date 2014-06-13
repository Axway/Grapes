package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.OrganizationView;

import java.io.IOException;


public class OrganizationSerializer extends JsonSerializer<OrganizationView> {
    @Override
    public void serialize(final OrganizationView organizationView, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(organizationView.getOrganization());
    }
}
