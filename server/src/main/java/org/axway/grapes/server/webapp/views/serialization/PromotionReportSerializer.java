package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.server.webapp.views.PromotionReportView;

import java.io.IOException;


public class PromotionReportSerializer extends JsonSerializer<PromotionReportView> {
    @Override
    public void serialize(final PromotionReportView promotionReportView, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeObject(promotionReportView.promotionDetails());
        jsonGenerator.flush();
        
    }
}
