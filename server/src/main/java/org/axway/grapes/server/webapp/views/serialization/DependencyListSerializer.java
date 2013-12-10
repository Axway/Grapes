package org.axway.grapes.server.webapp.views.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.reports.DependencyList;
import org.axway.grapes.server.webapp.views.DependencyListView;

import java.io.IOException;

/**
 * Handle the serialization of Dependency List
 *
 * @author jdcoffre
 */

public class DependencyListSerializer  extends JsonSerializer<DependencyListView> {

    @Override
    public void serialize(final DependencyListView dependencyListView, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        final DependencyList list = new DependencyList();
        for(Dependency dependency: dependencyListView.getDependencies()){
            list.addDependency(dependency);
        }

        jsonGenerator.writeObject(list);
        jsonGenerator.flush();
    }
}
