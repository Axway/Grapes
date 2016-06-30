package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.ProductView;

import java.io.IOException;


public class ProductSerializer extends JsonSerializer<ProductView> {
    @Override
    public void serialize(final ProductView productView, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(productView.getProduct());
        jsonGenerator.flush();
        
    }
}
