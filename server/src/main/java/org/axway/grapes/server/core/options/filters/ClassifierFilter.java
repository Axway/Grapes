package org.axway.grapes.server.core.options.filters;

import org.axway.grapes.server.db.datamodel.DbArtifact;

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
        if(datamodelObj instanceof DbArtifact){
            return classifier.equals( ((DbArtifact)datamodelObj).getClassifier());
        }

        return false;
    }

    @Override
    public Map<String, Object> moduleFilterFields() {
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> artifactFilterFields() {
        final Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(DbArtifact.CLASSIFIER_DB_FIELD, classifier);
        return queryParams;
    }
}
