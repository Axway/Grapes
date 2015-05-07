package org.axway.grapes.core.reports;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.model.datamodel.Artifact;
import org.axway.grapes.model.datamodel.Dependency;

import java.io.IOException;

public class DependencyReportSerializer extends JsonSerializer<DependencyReport>{
    @Override
    public void serialize(DependencyReport dependencyReport, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

    }
    /*

    @Override
    public void serialize(final DependencyReport report, final JsonGenerator json,	final SerializerProvider serializer) throws IOException {
        json.writeStartArray();

        for(Artifact target: report.getDependencyTargets()){
            json.writeStartObject();

            json.writeStringField("groupId", target.getGroupId());
            json.writeStringField("artifactId", target.getArtifactId());
            json.writeStringField("lastVersion", report.getLastVersion(target));

            json.writeFieldName("occurences");
            json.writeStartArray();

            for(String version: report.getVersions(target)){
                json.writeStartObject();
                json.writeStringField("version", version);

                json.writeFieldName("sources");
                json.writeStartArray();
                for(Dependency dep: report.getDependencies(target, version)){
                    json.writeStartObject();
                    json.writeStringField("scope", dep.getScope().toString());
                    json.writeStringField("gavc", dep.getSourceName());
                    json.writeEndObject();
                }
                json.writeEndArray();

                json.writeEndObject();
            }
            json.writeEndArray();

            json.writeEndObject();
        }

        json.writeEndArray();
        json.flush();

    }*/

}

