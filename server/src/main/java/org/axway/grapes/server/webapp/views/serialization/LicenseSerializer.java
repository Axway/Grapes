package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.LicenseView;

import java.io.IOException;

/**
 * Serialize a license view in Json
 *
 * @author jdcoffre
 */

public class LicenseSerializer extends JsonSerializer<LicenseView> {

    @Override
    public void serialize(final LicenseView licenseView, final JsonGenerator json,	final SerializerProvider serializer) throws IOException {
        json.writeObject(licenseView.getLicense());
        json.flush();

    }

}
