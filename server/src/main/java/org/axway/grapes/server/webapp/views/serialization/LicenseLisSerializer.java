package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.LicenseListView;

import java.io.IOException;

/**
 * Created by jdcoffre on 18/04/14.
 */
public class LicenseLisSerializer extends JsonSerializer<LicenseListView> {
    @Override
    public void serialize(final LicenseListView licenseList, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(licenseList.getLicenses());
        jsonGenerator.flush();
    }
}
