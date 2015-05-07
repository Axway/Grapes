package org.axway.grapes.core.options.filters;

import org.axway.grapes.model.datamodel.Artifact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassifierFilter implements Filter {

    private String classifier;

    /**
     * The parameter must never be null
     *
     * @param classifier
     */
    public ClassifierFilter(final String classifier) {
        this.classifier = classifier;
    }

    @Override
    public boolean filter(final Object datamodelObj) {
        if(datamodelObj instanceof Artifact){
            return classifier.equals( ((Artifact)datamodelObj).getClassifier());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("classifier", classifier);
        return queryParams;
    }
}
